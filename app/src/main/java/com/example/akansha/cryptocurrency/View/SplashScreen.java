package com.example.akansha.cryptocurrency.View;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.akansha.cryptocurrency.Constants.GlobalConstants;
import com.example.akansha.cryptocurrency.R;
import com.example.akansha.cryptocurrency.Utils.AndroidAppUtils;
import com.example.akansha.cryptocurrency.View.SetPinActivity;

public class SplashScreen extends AppCompatActivity {

    //  Splash screen time
    private int splash_screen_time = 2;
    private AppCompatActivity mActivity;
    private String TAG = SplashScreen.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);


        if (WalletContainerActivity.walletDataModelList != null)
            WalletContainerActivity.walletDataModelList.clear();
        else
            AndroidAppUtils.showErrorLog(TAG, "walletDataModelList is null");

        if (TransactionHistoryActivity.transactionHistoryDataModelList != null)
            TransactionHistoryActivity.transactionHistoryDataModelList.clear();
        else
            AndroidAppUtils.showErrorLog(TAG, "TransactionHistoryActivity.transactionHistoryDataModelList is null");

        if (AddressContainerActivity.walletInfoDataModelList != null)
            AddressContainerActivity.walletInfoDataModelList.clear();
        else
            AndroidAppUtils.showErrorLog(TAG, "walletInfoDataModelList is null");


        mActivity = this;
        startSetPinActivity();


    }

    /**
     * Start Activity to set Pin
     */
    private void startSetPinActivity() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //  Start Set pin activity
                startActivity(new Intent(mActivity, SetPinActivity.class));
                finish();

            }
        }, GlobalConstants.TIME_ONE_SECOND * splash_screen_time);

    }
}
