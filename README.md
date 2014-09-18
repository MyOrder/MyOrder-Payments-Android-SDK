MyOrder-Payments-Android-SDK
============================

Public Android framework for the [MyOrder payments SDK](http://myorder.nl/sdk).. Check the online [API documentation here](http://htmlpreview.github.io/?https://github.com/MyOrder/MyOrder-Payments-Android-SDK/blob/develop/docs/html/index.html) 

## Installation guide

The Android SDK is delivered as a compiled android lib. It can be installed by manually adding the lib to your project.


And these third party dependencies:

* [PayPalMPL](https://developer.paypal.com/webapps/developer/docs/classic/mobile/gs_MPL/) (Only required when PayPal is used as a payment method)
* [CardIO](https://www.jumio.com/) (only required when Credit card is used as a payment method, adds a camera card reader to the credit card payments)

Instructions on how to install the previous libraries can be found in their webpages.


## Project setup

### Configure the SDK


Before using the MyOrderSDK, you need to set up the credentials. You can do it by placing the following code before its use (typically in your `Main Activity onCreate` method):

```
			MyOrder order = MyOrder.getInstance();
			order.setEnvironment(MyOrderEnvironment.MyOrderEnvironmentLive);
			order.setCredentialStorage(new MyOrderStorage(getApplicationContext()));
			order.setApiKey("36bd8913-bf56-4aa0-9492-49a3240597ea");
			order.setApiSecret("12H@c9kT$At);
			order.setUrlScheme("myapp-scheme");

```

Please, note that you will need your own `apiKey` and `apiSecret`. You can get request them on the [Official MyOrder developers portal](http://myorder.nl/sdk). 

Your Ideal ReturnUrl is used to open the app after an iDeal transaction has been made, and therefore it is required when iDeal is active. Set a URL Type in your project Info settings and use the same for the lib `IdealReturnUrl`, and implement this method in your app:

Additionally, you can customize other MyOrder properties depending on your project needs:

* `environment`: use `MyOrderEnvironmentLive` for real money operations or `MyOrderEnvironmentSandbox` for testing purposes.
* `defaultMerchantDescription`: Customize the description that some services like iDeal will show in the bank description receipt.
* Available payment providers can be changed by using the `configureWithPaymentProviders` method. Example:
```
		Set<PaymentProvider> set = new HashSet<PaymentProvider>();
		set.add(new PaymentProvider(true, true, MOConstant.PAYMENT_PROVIDER_MINITIX, 1, false, getResources().getString(nl.myorder.lib.R.string.minitix_description), MOConstant.PAYMENT_PROVIDER_MINITIX));
		set.add(new PaymentProvider(true, true, MOConstant.PAYMENT_PROVIDER_IDEAL, 2, false, getResources().getString(nl.myorder.lib.R.string.ideal_description), "iDEAL"));
		set.add(new PaymentProvider(true, true, MOConstant.PAYMENT_PROVIDER_CREDITCARD, 4, false, getResources().getString(nl.myorder.lib.R.string.creadit_card_description),
				MOConstant.PAYMENT_PROVIDER_CREDITCARD));
		set.add(new PaymentProvider(false, false, MOConstant.PAYMENT_PROVIDER_PAYPAL, 3, false, getResources().getString(nl.myorder.lib.R.string.paypal_description), MOConstant.PAYMENT_PROVIDER_PAYPAL));
		set.add(new PaymentProvider(true, false, MOConstant.PAYMENT_PROVIDER_CARD, 5, false, getResources().getString(nl.myorder.lib.R.string.card_description), MOConstant.PAYMENT_PROVIDER_CARD));
		set.add(new PaymentProvider(true, false, MOConstant.PAYMENT_PROVIDER_OTA, 6, false, getResources().getString(nl.myorder.lib.R.string.ota_description),
				getString(R.string.ota_payment)));
		set.add(new PaymentProvider(false, false, MOConstant.PAYMENT_PROVIDER_AUTO, 6, false, getResources().getString(nl.myorder.lib.R.string.lbl_auto_reload_title),
				getString(R.string.lbl_auto_reload_title)));
		order.setConfiguredPaymentProviders(set);
```

### Present the wallet

Wallet can be presented by the use of `WalletFragment` Class. Example:

### Present payment screen with list of methods

Payments can be easily performed by using the `WalletFragment`. Note that you need to create a `MOOrder` with the order information and send this order as bundel to walletFragment. Example:

```

		MOOrder order = new MOOrder();
		order.setPrice(shoppingCartOrder.getTotal().getAmount());
		order.setExternalOrderId(String.valueOf(shoppingCartOrder.getId()));
		List<OrderItem> item = new ArrayList<OrderItem>()
		order.setOrderItems(item);
		Bundle bundle = new Bundle();
		bundle.putSerializable(MOOrder.class.getSimpleName(), order);

```

Similarly to the wallet method, making a payment does not necessarily require login. However, If the user is not logged in, some payment methods will not be presented. If login is required, then this method could potentially return a controller for login in, already set up for continuing with the payment when successfully logged in.

### Make a payment with a specific payment method

If you know which payment method to use (for example if you customize the UI for selecting payments in your app), then you can open an specific payment method screen by using:

```
			Bundle arg = new Bundle();
			arg.putString(PaymentProvider.class.getSimpleName(), providerName);
			arg.putString(MOFragmentStateHandler.FRAGMENT_CLASS_ARGS, MyOrder.getInstance().newTransactionForProvider(providerName).getClass().getSimpleName()
					+ "Fragment");
			if (order != null) {
				arg.putSerializable(MOOrder.class.getSimpleName(), order);
			}
			MyOrderFragment fragment = (MyOrderFragment) Class.forName("nl.myorder.lib.ui.providers." + MyOrder.getInstance().newTransactionForProvider(providerName).getClass().getSimpleName()
					+ "Fragment").newInstance();
			fragment.setArguments(arg);
			fm.beginTransaction().replace(resourceId, fragment).commit();
			
```

Additionally, this way gives you some extra blocks to perform custom actions when the transaction has an error, is completed or started. Example:


```
	Actity can implement VerifyPaymentOrderListener then SDK will call 
		VerifyPaymentOrderListener.verifyOrder(StartPaymentTransactionListener arg0) 
        //Custom logic to control whether the transaction is ready. Return YES to allow start
        call arg0.startTransaction("123456");
    };
	
```
Activity can implement MONotificationHandler listener to get notified in case of error, warring and notification.

Note that if you use the library this way you need to be sure the user is logged in properly for those methods that require a login. You can do that by using the login methods in 'MyOrder` class or by creating a `MyOrderLoginFragment` controller. And example is provided in the Example project.

## Final notes

This guide presents some of the most useful and common integration methods. However, the SDK comes with many other options that allow third party developers to make advanced integrations where the UI can be fully customized, new payment methods can be added, etc.
Please, check the [API documentation](http://htmlpreview.github.io/?https://github.com/MyOrder/MyOrder-Payments-Android-SDK/blob/develop/docs/html/index.html) to get more details of all options available
