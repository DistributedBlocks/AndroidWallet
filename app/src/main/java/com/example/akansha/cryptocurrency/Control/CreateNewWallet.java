package com.example.akansha.cryptocurrency.Control;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.akansha.cryptocurrency.Database.DatabaseHelper;
import com.example.akansha.cryptocurrency.Preferences.SolarBankerPreferenceManager;
import com.example.akansha.cryptocurrency.Utils.AndroidAppUtils;
import com.example.akansha.cryptocurrency.View.SetPinActivity;

import mobile.Mobile;

/**
 * Class to create new wallet
 */
public class CreateNewWallet {

    private String TAG = CreateNewWallet.class.getSimpleName();
    private DatabaseHelper databaseHelper;
    private AppCompatActivity activity;

    public CreateNewWallet(AppCompatActivity activity) {
        this.activity = activity;
        databaseHelper = new DatabaseHelper(activity);
    }

    public void createNewWallet(String seedValue, String walletName) {
        if (seedValue != null && !seedValue.isEmpty()) {
            try {

                String address = Mobile.getAddresses(seedValue, 1);

                int walletUid = SolarBankerPreferenceManager.getInstance(activity).getWalletUidCount();
                int newWalletId = ++walletUid;
                SolarBankerPreferenceManager.getInstance(activity).setWalletUidCount(newWalletId);
                AndroidAppUtils.showLog(TAG, "seed value:  " + seedValue);
                AndroidAppUtils.showLog(TAG, "address: " + address + " new wallet id: " + newWalletId);

                databaseHelper.insertWalletInfo(String.valueOf(newWalletId), walletName, address, seedValue, 1);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            AndroidAppUtils.showErrorLog(TAG, "seed value is null or  empty");
    }
}
