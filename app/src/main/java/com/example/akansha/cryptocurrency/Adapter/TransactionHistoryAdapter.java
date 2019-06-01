package com.example.akansha.cryptocurrency.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.akansha.cryptocurrency.Constants.OnItemClickListener;
import com.example.akansha.cryptocurrency.Model.TransactionHistoryDataModel;
import com.example.akansha.cryptocurrency.R;
import com.example.akansha.cryptocurrency.Utils.AndroidAppUtils;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Wallet list adapter
 */
public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.MyViewHolder> {

    private String TAG = TransactionHistoryAdapter.class.getSimpleName();

    private List<TransactionHistoryDataModel> transactionHistoryDataModelList;
    private OnItemClickListener onItemClickListener;
    private String coins_suffix = " HHC";

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tx_history_cell, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        TransactionHistoryDataModel transactionHistoryDataModel = transactionHistoryDataModelList.get(position);

        String sentTransaction = "Sent HHC";

        if (transactionHistoryDataModel.getTransaction_type().equalsIgnoreCase(sentTransaction))
            holder.transaction_icon.setImageResource(R.drawable.ic_yellow_send);
        else
            holder.transaction_icon.setImageResource(R.drawable.recv_blue);


        holder.transaction_type.setText(transactionHistoryDataModel.getTransaction_type());
        holder.date.setText(transactionHistoryDataModel.getDate());
        holder.address.setText(transactionHistoryDataModel.getAddress());

        if (transactionHistoryDataModel.getTransaction_type().equalsIgnoreCase(sentTransaction))

            holder.amount_label.setText("-" + String.valueOf(transactionHistoryDataModel.getTransaction_coins()) + coins_suffix);
        else
            holder.amount_label.setText(String.valueOf(transactionHistoryDataModel.getTransaction_coins()) + coins_suffix);

    }


    @Override
    public int getItemCount() {
        return transactionHistoryDataModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        LinearLayout transaction_ll;
        ImageView transaction_icon;
        TextView transaction_type, date, address, amount_label;

        MyViewHolder(View view) {
            super(view);

            transaction_ll = view.findViewById(R.id.transaction_ll);
            transaction_icon = view.findViewById(R.id.transaction_icon);
            transaction_type = view.findViewById(R.id.transaction_type);
            date = view.findViewById(R.id.date);
            address = view.findViewById(R.id.address);
            amount_label = view.findViewById(R.id.amount_label);
            transaction_ll.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, getAdapterPosition());
            } else
                AndroidAppUtils.showErrorLog(TAG, "onItemClickListener is null");

        }
    }

    public TransactionHistoryAdapter(List<TransactionHistoryDataModel> transactionHistoryDataModels, OnItemClickListener onItemClickListener) {
        this.transactionHistoryDataModelList = transactionHistoryDataModels;
        this.onItemClickListener = onItemClickListener;
    }

}
