package com.example.akansha.cryptocurrency.Preferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class SolarBankerPreferenceManager {
    // make private static instance of Sessionmanager class
    private static SolarBankerPreferenceManager SolarBankerPreferenceManager;
    // Shared Preferences
    private SharedPreferences pref;
    // Editor for Shared preferences
    private Editor editor;
    // Context
    private Context mContext;

    // Constructor
    @SuppressLint("CommitPrefEdits")
    private SolarBankerPreferenceManager(Context context) {
        this.mContext = context;
        pref = mContext.getSharedPreferences(PreferenceHelper.PREFERENCE_NAME,
                PreferenceHelper.PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * getInstance method is used to initialize SolarBankerPreferenceManager singelton
     * instance
     *
     * @param context context instance
     * @return Singelton session manager instance
     */
    public static SolarBankerPreferenceManager getInstance(Context context) {
        if (SolarBankerPreferenceManager == null) {
            SolarBankerPreferenceManager = new SolarBankerPreferenceManager(context);
        }
        return SolarBankerPreferenceManager;
    }


    public boolean getIsWalletPinSet() {
        return pref.getBoolean(PreferenceHelper.is_wallet_pin_set, false);
    }

    public void setIsWalletPinSet(boolean isWalletPinSet) {
        editor.putBoolean(PreferenceHelper.is_wallet_pin_set, isWalletPinSet);
        editor.commit();
    }

    public String getWalletPin() {
        return pref.getString(PreferenceHelper.wallet_pin, "");
    }

    public void setWalletPin(String walletPin) {
        editor.putString(PreferenceHelper.wallet_pin, walletPin);
        editor.commit();
    }

    public int getWalletUidCount() {
        return pref.getInt(PreferenceHelper.wallet_uid, 1);
    }

    public void setWalletUidCount(int uid) {
        editor.putInt(PreferenceHelper.wallet_uid, uid);
        editor.commit();
    }

    public String getNodeUrl() {
        return pref.getString(PreferenceHelper.node_url, "http://104.248.131.27:8120");
    }

    public void setNodeUrl(String nodeUrl) {
        editor.putString(PreferenceHelper.node_url, nodeUrl);
        editor.commit();
    }

    public boolean isWalletCreated() {
        return pref.getBoolean(PreferenceHelper.is_wallet_created, false);
    }

    public void setisWalletCreated(boolean isWaletCreated) {
        editor.putBoolean(PreferenceHelper.is_wallet_created, isWaletCreated);
        editor.commit();
    }


}