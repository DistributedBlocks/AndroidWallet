package com.example.akansha.cryptocurrency.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.akansha.cryptocurrency.Constants.OnItemClickListener;
import com.example.akansha.cryptocurrency.Model.WalletInfoDataModel;
import com.example.akansha.cryptocurrency.R;
import com.example.akansha.cryptocurrency.Utils.AndroidAppUtils;

import java.util.List;

/**
 * Wallet list adapter
 */
public class WalletAddressAdapter extends RecyclerView.Adapter<WalletAddressAdapter.MyViewHolder> {

    private String TAG = WalletAddressAdapter.class.getSimpleName();

    private List<WalletInfoDataModel> walletInfoDataModelList;
    private OnItemClickListener onItemClickListener;
    private int rowAdditionCount = 0;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.address_list_cell, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        WalletInfoDataModel walletInfoDataModel = walletInfoDataModelList.get(position);

        AndroidAppUtils.showLog(TAG, "Item added in list at position: " + position);

        holder.index.setText(String.valueOf(position));
        holder.address.setText(walletInfoDataModel.getAddress());
        holder.hours.setText(String.valueOf(walletInfoDataModel.getHours()));
        holder.balance.setText(String.valueOf(walletInfoDataModel.getBalance()));

    }

    @Override
    public int getItemCount() {
        return walletInfoDataModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView index, address, hours, balance;
        public LinearLayout main_contaoner;

        public MyViewHolder(View view) {
            super(view);

            index = view.findViewById(R.id.index);
            address = view.findViewById(R.id.address);
            hours = view.findViewById(R.id.hours);
            balance = view.findViewById(R.id.balance);
            main_contaoner = view.findViewById(R.id.main_container);

            main_contaoner.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, getAdapterPosition());
            } else
                AndroidAppUtils.showErrorLog(TAG, "onItemClickListener is null");

        }
    }

    public WalletAddressAdapter(List<WalletInfoDataModel> walletInfoDataModelList, OnItemClickListener onItemClickListener) {
        this.walletInfoDataModelList = walletInfoDataModelList;
        this.onItemClickListener = onItemClickListener;
    }

}
