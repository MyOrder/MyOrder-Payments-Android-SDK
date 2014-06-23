package nl.myorder;

import myorder.MyOrder;
import myorder.MyOrder.MyOrderEnvironment;
import myorder.listeners.StartPaymentTransactionListener;
import myorder.listeners.VerifyPaymentOrderListener;
import nl.myorder.lib.interfaces.MONotificationHandler;
import nl.myorder.lib.interfaces.PaymentAnalyticsListener;
import nl.myorder.lib.ui.MyOrderActivity;
import nl.myorder.lib.ui.handler.MOFragmentStateHandler;
import nl.myorder.lib.ui.providers.MOIdealTransactionFragment;
import nl.myorder.lib.utils.MyOrderStateEnum;
import nl.myorder.storage.MyOrderStorage;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class StartActivity extends MyOrderActivity implements VerifyPaymentOrderListener, PaymentAnalyticsListener, MONotificationHandler {

	private Fragment mContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MyOrder order = MyOrder.getInstance();
		order.setEnvironment(MyOrderEnvironment.MyOrderEnvironmentSandbox);
		order.setCredentialStorage(new MyOrderStorage(getApplicationContext()));
		order.setApiKey("d712d563-d5ed-4826-8920-1b2c2b743ba9");
		order.setApiSecret("hH3#1PxxS");
		order.setIdealReturnUrl("myorder-sample://ideal_return_url");
		order.setVerifyPaymentOrderListener(this);
		if (MyOrder.getInstance().hasSavedCredentials()) {
			MyOrder.getInstance().setPhoneNumber(MyOrder.getInstance().getPhoneNumber());
		}
		if (mContent == null) {
			mContent = new HomeFragment();
		}

		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mContent).commit();
	}

	@Override
	public void onStateChange(MyOrderStateEnum state, boolean supportBack, Bundle fragmentArguments) {
		if (state == MyOrderStateEnum.SHOW_HOME) {
			FragmentManager fm = this.getSupportFragmentManager();
			for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
				fm.popBackStack();
			}
			mContent = new HomeFragment();
			if (fragmentArguments != null) {
				String fragmentName = fragmentArguments.getString("FragmentClass");
				if (!TextUtils.isEmpty(fragmentName)) {
					try {
						mContent = (Fragment) Class.forName("nl.myorder.lib.ui.providers." + fragmentName).newInstance();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						return;
					}
				}
				mContent.setArguments(fragmentArguments);
			}
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			if (supportBack) {
				ft.addToBackStack(mContent.getClass().getName());
			}
			ft.replace(R.id.content_frame, mContent);
			ft.commit();
		} else {
			MOFragmentStateHandler.getInstance().onFragmentStateChanged(this, state, supportBack, fragmentArguments);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		Uri URIdata = intent.getData();
		if (URIdata != null) {
			String parameter1value = URIdata.getQueryParameter("merchantReference");
			String authResult = URIdata.getQueryParameter("authResult");
			Log.i(MyOrderActivity.class.getName(), "paramvalue: " + parameter1value);
			if (authResult != null && !"Success".equalsIgnoreCase(authResult)) {
				Toast.makeText(this, "System error: " + authResult, Toast.LENGTH_SHORT).show();
			}
			Fragment mContent = getSupportFragmentManager().findFragmentById(R.id.content_frame);
			if (mContent instanceof MOIdealTransactionFragment) {
				((MOIdealTransactionFragment) mContent).setCallbackFromIdeal(true);
			}
		}
	}

	@Override
	public void verifyOrder(StartPaymentTransactionListener arg0) {
		if (arg0 != null) {
			Toast.makeText(this, "start payment", Toast.LENGTH_LONG).show();
			arg0.startTransaction("123456");
		}
	}

	@Override
	public void logScreenName(String screenName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void logEvent(String category, String action, String lablel) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onNotification(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onWarrning(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

}
