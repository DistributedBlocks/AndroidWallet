package com.example.akansha.cryptocurrency.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.akansha.cryptocurrency.Application.SolarBankerApplication;
import com.example.akansha.cryptocurrency.Constants.GlobalConstants;
import com.example.akansha.cryptocurrency.Control.PinControl;
import com.example.akansha.cryptocurrency.Preferences.SolarBankerPreferenceManager;
import com.example.akansha.cryptocurrency.R;
import com.example.akansha.cryptocurrency.Utils.AndroidAppUtils;

public class SetPinActivity extends AppCompatActivity {

    private static final String TAG = SetPinActivity.class.getSimpleName();
    private SolarBankerPreferenceManager solarBankerPreferenceManager;

    private PinControl pinControl;
    private boolean isPerformTransaction = false;
    private String KEY_IS_TRANSACTION = "IS_TRANSACTION";
    private String KEY_IS_CHANGE_PIN = "IS_CHANGE_PIN";
    private boolean isChangePin = false;
    private boolean isGetSeedValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_activity);
        solarBankerPreferenceManager = SolarBankerPreferenceManager.getInstance(SolarBankerApplication.solarBankerApplicationContext);

        Intent intent = getIntent();
        if (intent != null) {

            if (intent.hasExtra(KEY_IS_TRANSACTION))
                isPerformTransaction = intent.getBooleanExtra(KEY_IS_TRANSACTION, false);
            else
                AndroidAppUtils.showErrorLog(TAG, "No  transaction key");

            if (intent.hasExtra(GlobalConstants.KEY_GET_SEED_VALUE))
                isGetSeedValue = intent.getBooleanExtra(GlobalConstants.KEY_GET_SEED_VALUE, false);
            else
                AndroidAppUtils.showErrorLog(TAG, "No GlobalConstants.KEY_GET_SEED_VALUE");

            if (intent.hasExtra(KEY_IS_CHANGE_PIN))
                isChangePin = intent.getBooleanExtra(KEY_IS_CHANGE_PIN, false);
            else
                AndroidAppUtils.showErrorLog(TAG, "No change pin key");

        } else
            AndroidAppUtils.showErrorLog(TAG, "intent is null");

        pinControl = new PinControl(this, isPerformTransaction, isChangePin,isGetSeedValue);
        pinControl.showPinGUI();


    }


}
