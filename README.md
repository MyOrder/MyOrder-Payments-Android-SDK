MyOrder-Payments-Android-SDK
============================

Public Android framework for the [MyOrder payments SDK](http://myorder.nl/sdk).. Online API documentation here 

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
			order.setApiKey("d712d563-d5ed-4826-8920-1b2c2b743ba9");
			order.setApiSecret("hH3#1PxxS");
			order.setIdealReturnUrl("myapp-scheme://ideal_return_url");

```

Please, note that you will need your own `apiKey` and `apiSecret`. You can get request them on the [Official MyOrder developers portal](http://myorder.nl/sdk). 

Your Ideal ReturnUrl is used to open the app after an iDeal transaction has been made, and therefore it is required when iDeal is active. Set a URL Type in your project Info settings and use the same for the lib `IdealReturnUrl`, and implement this method in your app:

Additionally, you can customize other MyOrder properties depending on your project needs:

* `environment`: use `MyOrderEnvironmentLive` for real money operations or `MyOrderEnvironmentSandbox` for testing purposes.
* `defaultMerchantDescription`: Customize the description that some services like iDeal will show in the bank description receipt.
* Available payment providers can be changed by using the `configureWithPaymentProviders` method. Example:
```
		Set<PaymentProvider> set = new HashSet<PaymentProvider>();
		set.add(new PaymentProvider(true, true, "MiniTix", 1, false, getResources().getString(nl.myorder.lib.R.string.minitix_description), "MiniTix"));
		set.add(new PaymentProvider(true, true, "iDeal", 2, false, getResources().getString(nl.myorder.lib.R.string.ideal_description), "iDEAL"));
		set.add(new PaymentProvider(true, true, "CreditCard", 4, false, getResources().getString(nl.myorder.lib.R.string.creadit_card_description),
				"CreditCard"));
		set.add(new PaymentProvider(false, false, "PayPal", 3, false, getResources().getString(nl.myorder.lib.R.string.paypal_description), "PayPal"));
		set.add(new PaymentProvider(true, false, "Card", 5, false, getResources().getString(nl.myorder.lib.R.string.card_description), "Card"));
		set.add(new PaymentProvider(true, false, "OTA", 6, false, getResources().getString(nl.myorder.lib.R.string.ota_description), getString(R.string.ota_payment)));
		set.add(new PaymentProvider(false, false, "AUTO", 6, false, getResources().getString(nl.myorder.lib.R.string.lbl_auto_reload_title), getString(R.string.lbl_auto_reload_title)));
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
		order.setOrderId(String.valueOf(shoppingCartOrder.getId()));
		List<OrderItem> item = new ArrayList<OrderItem>()
		order.setOrderItems(item);
		Bundle bundle = new Bundle();
		bundle.putSerializable(MOOrder.class.getSimpleName(), order);

```

Similarly to the wallet method, making a payment does not necessarily require login. However, If the user is not logged in, some payment methods will not be presented. If login is required, then this method could potentially return a controller for login in, already set up for continuing with the payment when successfully logged in.


