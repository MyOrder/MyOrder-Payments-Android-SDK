package nl.myorder.storage;

import myorder.MOCredentialStorage;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MyOrderStorage implements MOCredentialStorage {

	private static SharedPreferences userPreff;
	private static final String MY_ORDER_PREFERENCE_KEY = "MyOrderpreferenceKey";

	public MyOrderStorage(Context mContext) {
		if (userPreff == null) {
			userPreff = mContext.getSharedPreferences(MY_ORDER_PREFERENCE_KEY, Context.MODE_PRIVATE);
		}
	}

	@Override
	public String get(String key) {
		return userPreff.getString(key, null);
	}

	@Override
	public void put(String key, String value) {
		Editor edit = userPreff.edit();
		edit.putString(key, value);
		edit.commit();
	}

	@Override
	public boolean remove(String key) {
		userPreff.edit().remove(key).commit();
		return true;
	}

	@Override
	public boolean removeAll() {
		userPreff.edit().clear().commit();
		return true;
	}

}
