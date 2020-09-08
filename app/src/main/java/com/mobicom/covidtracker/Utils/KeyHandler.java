package com.mobicom.covidtracker.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mobicom.covidtracker.Const.PrefKey;
import com.mobicom.covidtracker.Models.RandomBroadcastId;

import java.util.ArrayList;
import java.util.Random;

public class KeyHandler {

    private SharedPrefManager sharedPrefManager;


    // declare context
    private static Context mContext;

    // singleton
    private static KeyHandler keyHandler = null;

    // common


    public static KeyHandler getInstance(Context context) {
        if (keyHandler == null) {
            mContext = context;
            keyHandler = new KeyHandler();
        }
        return keyHandler;
    }

    private KeyHandler() {
        sharedPrefManager = SharedPrefManager.getInstance(mContext);
    }

    public String getDeviceTrackingID(){
        return sharedPrefManager.getDiagnosisKey();
    }

    public RandomBroadcastId getRandomBroadcastID(){
        String diagnosisKey = getDeviceTrackingID();
        ArrayList<String> idSecretKeys = sharedPrefManager.getIDSecretKeys();
        Random randomize = new Random();
        int key = randomize.nextInt(idSecretKeys.size());
        String randomSecret = idSecretKeys.get(key);
        String encryptedBroadcastId =AES.encrypt(diagnosisKey,randomSecret);
        return new RandomBroadcastId(encryptedBroadcastId,key);
    }

    public String getReversedDiagnosisKey(String id,int key){
        ArrayList<String> idSecretKeys = sharedPrefManager.getIDSecretKeys();
        String secret = idSecretKeys.get(key);
        String decryptedDiagnosisKey =AES.decrypt(id,secret);
        return decryptedDiagnosisKey;
    }

    public String getNewUUID(){
        char[] _base62chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        int codeLength = 6;
        Random _random = new Random();
        StringBuilder sb = new StringBuilder(codeLength);
        for (int i=0; i<codeLength; i++) {
            sb.append(_base62chars[_random.nextInt(62)]);
        }
        return sb.toString();
    }
}
