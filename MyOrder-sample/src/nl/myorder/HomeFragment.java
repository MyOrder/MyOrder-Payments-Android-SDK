package nl.myorder;

import java.util.ArrayList;
import java.util.List;

import myorder.MyOrder;
import myorder.order.MOOrder;
import myorder.order.OrderItem;
import nl.myorder.lib.interfaces.OnFragmentStateChangeListener;
import nl.myorder.lib.ui.CustomDialog;
import nl.myorder.lib.utils.MyOrderStateEnum;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class HomeFragment extends Fragment implements OnClickListener {
	private CustomDialog progressDialog;
	private OnFragmentStateChangeListener mCallback;
	private Button login, loggout, register, placeOrder, wallet, purchaseHistory;
	private EditText phoneNumber;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.home_fragment, container, false);
		login = (Button) v.findViewById(R.id.login_btn_login);
		placeOrder = (Button) v.findViewById(R.id.place_order);
		purchaseHistory = (Button) v.findViewById(R.id.purchase_history);
		purchaseHistory.setOnClickListener(this);
		placeOrder.setOnClickListener(this);
		phoneNumber = (EditText) v.findViewById(R.id.phone_number);
		loggout = (Button) v.findViewById(R.id.loggout);
		register = (Button) v.findViewById(R.id.register);
		wallet = (Button) v.findViewById(R.id.wallet);
		login.setOnClickListener(this);
		wallet.setOnClickListener(this);
		register.setOnClickListener(this);
		loggout.setOnClickListener(this);
		return v;

	}

	@Override
	public void onResume() {
		super.onResume();
		toggleButton();
	}

	private void toggleButton() {
		if (!TextUtils.isEmpty(MyOrder.getInstance().getPhoneNumber())) {
			phoneNumber.setText(MyOrder.getInstance().getPhoneNumber());
		}

		if (MyOrder.getInstance().isLoggedIn()) {
			login.setVisibility(View.GONE);
			loggout.setVisibility(View.VISIBLE);
			register.setVisibility(View.GONE);
			purchaseHistory.setVisibility(View.VISIBLE);
			phoneNumber.setEnabled(false);
		} else {
			login.setVisibility(View.VISIBLE);
			loggout.setVisibility(View.GONE);
			register.setVisibility(View.VISIBLE);
			purchaseHistory.setVisibility(View.GONE);
			phoneNumber.setEnabled(true);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (OnFragmentStateChangeListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnFragmentStateChangeListener");
		}
	}

	public class LoggoutAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (TextUtils.isEmpty(result)) {
				toggleButton();
			} else {
				Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				MyOrder.getInstance().logout();
			} catch (Exception e) {
				return e.getMessage();
			}
			return null;
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == placeOrder.getId()) {
			if (TextUtils.isEmpty(phoneNumber.getText().toString())) {
				Toast.makeText(getActivity(), "please login or set the phone number", Toast.LENGTH_LONG).show();
				return;
			}
			MOOrder order = new MOOrder();
			order.setPrice(1.0);
			order.setExternalOrderId("123456");
			order.setOrderId("123456");
			//OrderItem item = new OrderItem(0.5, 2, "Test orderitems");
			List<OrderItem> items = new ArrayList<OrderItem>();
			//items.add(item);
			order.setOrderItems(items);
			MyOrder.getInstance().setPhoneNumber(phoneNumber.getText().toString());
			Bundle arg = new Bundle();
			arg.putSerializable(MOOrder.class.getSimpleName(), order);
			mCallback.onStateChange(MyOrderStateEnum.SHOW_WALLET, true, arg);
		} else if (v.getId() == login.getId()) {
			mCallback.onStateChange(MyOrderStateEnum.DO_LOGIN, true, null);
		} else if (v.getId() == loggout.getId()) {
			if (progressDialog == null) {
				progressDialog = new CustomDialog(getActivity());
				progressDialog.show();
			}
			new LoggoutAsyncTask().execute();
		} else if (v.getId() == register.getId()) {
			mCallback.onStateChange(MyOrderStateEnum.REGISTRATION, true, null);
		} else if (v.getId() == wallet.getId()) {
			if (TextUtils.isEmpty(phoneNumber.getText().toString())) {
				Toast.makeText(getActivity(), "please login or set the phone number", Toast.LENGTH_LONG).show();
				return;
			}
			MyOrder.getInstance().setPhoneNumber(phoneNumber.getText().toString());
			mCallback.onStateChange(MyOrderStateEnum.SHOW_WALLET, true, null);
		} else if (v.getId() == purchaseHistory.getId()) {
			mCallback.onStateChange(MyOrderStateEnum.PURCHASE_HISTORY, true, null);
		}
	}
}
