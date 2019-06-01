package com.example.akansha.cryptocurrency.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.akansha.cryptocurrency.R;

/**
 * Class to access wallet
 */
public class WalletAccessActivity extends AppCompatActivity{

    private String TAG=WalletAccessActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_activity);

    }
}
