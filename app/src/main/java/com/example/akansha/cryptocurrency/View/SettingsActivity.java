package com.example.akansha.cryptocurrency.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.akansha.cryptocurrency.Application.SolarBankerApplication;
import com.example.akansha.cryptocurrency.Constants.GlobalConstants;
import com.example.akansha.cryptocurrency.Preferences.SolarBankerPreferenceManager;
import com.example.akansha.cryptocurrency.R;
import com.example.akansha.cryptocurrency.Utils.AndroidAppUtils;
import com.example.akansha.cryptocurrency.Utils.GlobalConfig;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {


    private String TAG = SettingsActivity.class.getSimpleName();
    private TextView versionName, backendVersion, dbVersion;
    private String versionPrefix = "Version: ";
    private String nodePrefix = "Backend node min version: ";
    private String dbVersionPrefix = "DB version: ";
    private Button pin_button, cancel_button, save_button, restore_button;
    private EditText url_text;
    private String KEY_IS_CHANGE_PIN = "IS_CHANGE_PIN";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        initViews();
        initClickListener();
    }

    private void initClickListener() {

        pin_button.setOnClickListener(this);
        cancel_button.setOnClickListener(this);
        save_button.setOnClickListener(this);
        restore_button.setOnClickListener(this);

    }

    private void initViews() {

        versionName = findViewById(R.id.versionName);
        backendVersion = findViewById(R.id.backendVersion);
        dbVersion = findViewById(R.id.dbVersion);
        pin_button = findViewById(R.id.pin_button);
        cancel_button = findViewById(R.id.cancel_button);
        save_button = findViewById(R.id.save_button);
        restore_button = findViewById(R.id.restore_button);
        url_text = findViewById(R.id.url_text);

        versionName.setText(versionPrefix + GlobalConstants.aap_version);
        backendVersion.setText(nodePrefix + GlobalConstants.backendNodeVersion);
        dbVersion.setText(dbVersionPrefix + GlobalConstants.dbVersion);
        url_text.setText(SolarBankerPreferenceManager.getInstance(this).getNodeUrl());

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.pin_button:

                Intent intent = new Intent(this, SetPinActivity.class);
                intent.putExtra(KEY_IS_CHANGE_PIN, true);
                startActivity(intent);

                break;
            case R.id.cancel_button:

                startActivity(new Intent(this, WalletContainerActivity.class));
                finish();

                break;
            case R.id.save_button:

                String nodeUrl = url_text.getText().toString().trim();
                AndroidAppUtils.showLog(TAG, "Node url: " + nodeUrl);
                if (!nodeUrl.isEmpty()) {
                    SolarBankerPreferenceManager.getInstance(this).setNodeUrl(nodeUrl);
                } else
                    AndroidAppUtils.showErrorLog(TAG, "node url is empty");

                startActivity(new Intent(this, WalletContainerActivity.class));
                finish();

                break;
            case R.id.restore_button:

                url_text.setText(GlobalConstants.nodeUrl);

                break;

        }


    }
}
