package com.example.akansha.cryptocurrency.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.akansha.cryptocurrency.Control.NewWalletViewControl;
import com.example.akansha.cryptocurrency.R;

/**
 * Class to create new wallet
 */
public class NewWalletActivity extends AppCompatActivity {

    private String TAG = NewWalletActivity.class.getSimpleName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_wallet_activity);

        new NewWalletViewControl(this);

    }


}
