package com.mobicom.covidtracker.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
    }

    public String getBroadcastSecretKey() {
        return appPreferences.getString(PrefKey.KEY_BROADCAST_SECRET_KEY, "mobiComCovidTracker");
    }

    public ArrayList<String> getIDSecretKeys() {
        ArrayList<String> keys = new ArrayList<String>();
        keys.addAll(Arrays.asList( appPreferences.getString(PrefKey.KEY_ID_SECRET_KEYS, "").split(",")));
        return keys;
    }


}
