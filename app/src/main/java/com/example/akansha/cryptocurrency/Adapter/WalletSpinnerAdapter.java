package com.example.akansha.cryptocurrency.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.akansha.cryptocurrency.Model.WalletDataModel;
import com.example.akansha.cryptocurrency.R;

import java.util.ArrayList;

public class WalletSpinnerAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList walletDataModelList;
    public Resources res;
    private LayoutInflater inflater;
    private WalletDataModel tempValues;


    public WalletSpinnerAdapter(
            Context activitySpinner,
            int textViewResourceId,
            ArrayList objects,
            Resources resLocal) {
        super(activitySpinner, textViewResourceId, objects);

        context = activitySpinner;
        walletDataModelList = objects;
        res = resLocal;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }


    // This funtion called for each row ( Called data.size() times )
    private View getCustomView(int position, View convertView, ViewGroup parent) {

        View row = inflater.inflate(R.layout.custom_spinner_row, parent, false);
        tempValues = (WalletDataModel) walletDataModelList.get(position);

        TextView walletName = row.findViewById(R.id.wallet_name);
        TextView coins = row.findViewById(R.id.coins);

        // Set values for spinner each row
        walletName.setText(tempValues.getWallet_name());
        coins.setText(String.valueOf(tempValues.getWallet_balance()));


        return row;
    }


}
