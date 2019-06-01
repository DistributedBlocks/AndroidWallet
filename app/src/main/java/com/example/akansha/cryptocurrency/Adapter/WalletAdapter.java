package com.example.akansha.cryptocurrency.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.akansha.cryptocurrency.Constants.OnItemClickListener;
import com.example.akansha.cryptocurrency.Model.WalletDataModel;
import com.example.akansha.cryptocurrency.R;
import com.example.akansha.cryptocurrency.Utils.AndroidAppUtils;
import com.example.akansha.cryptocurrency.View.WalletContainerActivity;

import java.util.List;

/**
 * Wallet list adapter
 */
public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.MyViewHolder> {

    private String TAG = WalletAdapter.class.getSimpleName();

    private List<WalletDataModel> walletDataModelList;
    private OnItemClickListener onItemClickListener;
    private int rowAdditionCount = 0;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wallet_list_cell, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        WalletDataModel walletDataModel = walletDataModelList.get(position);

        rowAdditionCount++;

        AndroidAppUtils.showLog(TAG, "Item added in list at position: " + position);
        AndroidAppUtils.showLog(TAG, "wallet name: " + walletDataModel.getWallet_name());


        holder.wallet_name.setText(walletDataModel.getWallet_name());
        holder.wallet_hours.setText(String.valueOf(walletDataModel.getWallet_hours()));
        holder.wallet_balance.setText(String.valueOf(walletDataModel.getWallet_balance()));
    }

    @Override
    public int getItemCount() {
        return walletDataModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView wallet_name, wallet_hours, wallet_balance;
        private RelativeLayout wallet_info_rl;
        public ImageView chevron;

        public MyViewHolder(View view) {
            super(view);
            wallet_name = view.findViewById(R.id.wallet_name);
            wallet_hours = view.findViewById(R.id.wallet_hours);
            wallet_balance = view.findViewById(R.id.wallet_balance);
            chevron = view.findViewById(R.id.chevron);

            wallet_info_rl = view.findViewById(R.id.wallet_info_rl);

            chevron.setOnClickListener(this);
            wallet_info_rl.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, getAdapterPosition());
            } else
                AndroidAppUtils.showErrorLog(TAG, "onItemClickListener is null");

        }
    }

    public WalletAdapter(List<WalletDataModel> walletDataModelList, OnItemClickListener onItemClickListener) {
        this.walletDataModelList = walletDataModelList;
        this.onItemClickListener = onItemClickListener;
    }

}
