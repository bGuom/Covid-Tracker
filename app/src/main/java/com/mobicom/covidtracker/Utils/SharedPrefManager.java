package com.mobicom.covidtracker.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mobicom.covidtracker.Const.PrefKey;

import java.util.ArrayList;
import java.util.Arrays;

public class SharedPrefManager {

    // declare context
    private static Context mContext;

    // singleton
    private static SharedPrefManager appPreferenceMngr = null;

    // common
    private SharedPreferences sharedPreferences, appPreferences;
    private SharedPreferences.Editor editor;

    public static SharedPrefManager getInstance(Context context) {
        if (appPreferenceMngr == null) {
            mContext = context;
            appPreferenceMngr = new SharedPrefManager();
        }
        return appPreferenceMngr;
    }

    private SharedPrefManager() {
        sharedPreferences = mContext.getSharedPreferences(PrefKey.APP_PREF_NAME, Context.MODE_PRIVATE);
        appPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = sharedPreferences.edit();
    }

    public void setString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public void setBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public Boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public void setInteger(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInteger(String key) {
        return sharedPreferences.getInt(key, -1);
    }




    public String getContactNumber() {
        return appPreferences.getString(PrefKey.KEY_CONTACT_NUMBER, "");
    }

    public String getDiagnosisKey() {
        return appPreferences.getString(PrefKey.KEY_DIAGNOSIS_KEY, "");
        //return "z5KyMg";
    }

    public String getBroadcastSecretKey() {
        return appPreferences.getString(PrefKey.KEY_BROADCAST_SECRET_KEY, "mobiComCovidTracker");
        //return "e3d95be4-d6c9-4d94-a3ff-d8008e740fa3";
    }

    public ArrayList<String> getIDSecretKeys() {
        ArrayList<String> keys = new ArrayList<String>();
        keys.addAll(Arrays.asList( appPreferences.getString(PrefKey.KEY_ID_SECRET_KEYS, "mobiComCovidTracker").split(",")));
        /**
        keys.add("ed524a3e-d498-4951-aea4-4d2347241c94");
        keys.add("92c8df76-b035-4ee8-a12c-d55cb0a7191e");
        keys.add("cef67a4f-50a0-41e4-b7f7-35aae0d66051");
        keys.add("1f36a740-6675-4dd4-8db3-6cb0fa93c173");
         **/



        return keys;
    }


}
