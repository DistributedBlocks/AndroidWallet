package com.example.akansha.cryptocurrency.View;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.akansha.cryptocurrency.Adapter.TransactionHistoryAdapter;
import com.example.akansha.cryptocurrency.Constants.GlobalConstants;
import com.example.akansha.cryptocurrency.Constants.OnItemClickListener;
import com.example.akansha.cryptocurrency.Control.WalletInfoAPIHandler;
import com.example.akansha.cryptocurrency.Model.TransactionHistoryDataModel;
import com.example.akansha.cryptocurrency.Model.WalletNameAndAddressDataModel;
import com.example.akansha.cryptocurrency.R;
import com.example.akansha.cryptocurrency.Utils.AndroidAppUtils;
import com.example.akansha.cryptocurrency.WebServices.GetTransactionInfoAPIHandler;
import com.example.akansha.cryptocurrency.iHelper.WebAPIResponseListener;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryActivity extends AppCompatActivity implements View.OnClickListener {

    private final String HHC = " HHC";
    private RecyclerView recyclerView;
    public static TransactionHistoryAdapter mAdapter;
    public static List<TransactionHistoryDataModel> transactionHistoryDataModelList = new ArrayList<>();
    private static String TAG = TransactionHistoryActivity.class.getSimpleName();

    private Dialog transactionDetailDialog;
    private TextView txn_title, internet_alert_ok_button, internet_alert_message;
    private ImageView txn_icon, status_checkbox;
    private TextView date_label, status_label, address_label, from_label, to_label, to_address_label, amount_label;
    private ImageView wallet_button;

    private Dialog internetErrorDialog;
    private RelativeLayout continue_button;
    private ImageView transactions_refresh;

    private static Dialog loadingDialog;
    private TextView alert_message;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        initViews();

        setAdapter();

        initClickListener();

        showTransactionHistoryList();


    }

    private void showInternetAlertDialog(boolean isImternetError, boolean isBadUrl) {

        hideLoadingDialog();

        if (internetErrorDialog == null) {

            internetErrorDialog = new Dialog(this);
            internetErrorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            internetErrorDialog.setContentView(R.layout.internet_alert_dialog);

            internet_alert_ok_button = internetErrorDialog.findViewById(R.id.internet_alert_ok_button);
            internet_alert_message = internetErrorDialog.findViewById(R.id.alert_message);

            if (isBadUrl)
                internet_alert_message.setText(getResources().getString(R.string.invalid_node_url));
            else
                internet_alert_message.setText(getResources().getString(R.string.internet_alert_msg));

            internet_alert_ok_button.setOnClickListener(this);

            WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();

            lWindowParams.copyFrom(internetErrorDialog.getWindow().getAttributes());
            lWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            internetErrorDialog.show();
            internetErrorDialog.getWindow().setAttributes(lWindowParams);

        } else {
            if (internetErrorDialog.isShowing()) {
                internetErrorDialog.dismiss();
                internetErrorDialog.cancel();
            } else
                AndroidAppUtils.showErrorLog(TAG, "internetErrorDialog is not showing");

            internetErrorDialog.show();

        }

    }

    private void initClickListener() {

        wallet_button.setOnClickListener(this);
        continue_button.setOnClickListener(this);
        transactions_refresh.setOnClickListener(this);
    }


    private void showTransactionHistoryList() {

//        if (getIntent() != null) {
//            Intent intent = getIntent();
//            String KEY_ALL_WALLETS_ADDRESS_LIST = "ALL_WALLETS_ADDRESS_LIST";
//            String KEY_BUNDLE = "BUNDLE";
//            if (intent.hasExtra(KEY_BUNDLE)) {
//
//                Bundle bundle = intent.getBundleExtra(KEY_BUNDLE);
//
//                ArrayList<WalletNameAndAddressDataModel> allWalletsAddressList = (ArrayList<WalletNameAndAddressDataModel>) bundle.getSerializable(KEY_ALL_WALLETS_ADDRESS_LIST);

        ArrayList<WalletNameAndAddressDataModel> allWalletsAddressList = new WalletInfoAPIHandler(this).allWalletsAddresses();

        if (allWalletsAddressList != null) {

            showLoadingDialog();

            for (int i = 0; i < allWalletsAddressList.size(); i++) {

                new GetTransactionInfoAPIHandler(this, allWalletsAddressList.get(i).getAddress(), allWalletsAddressList.get(i).getWalletName(), allWalletsAddressList.size(), new WebAPIResponseListener() {
                    @Override
                    public void onSuccessResponse(Object... arguments) {

                    }

                    @Override
                    public void onFailResponse(Object... arguments) {

                       /* if (arguments != null && arguments[0] != null) {

                            AndroidAppUtils.showErrorLog(TAG, "code: " + arguments[0]);

                            if ((Integer) arguments[0] == 400) {
                                AndroidAppUtils.showErrorLog(TAG, "Bad URL: ");
                                showInternetAlertDialog(false, true);
                            } else
                                showInternetAlertDialog(true, false);

                        } else
                            showInternetAlertDialog(true, false);*/

                        showInternetAlertDialog(true, false);


                    }

                    @Override
                    public void onOfflineResponse(Object... arguments) {

                    }
                });

            }

//                    for (String address : allWalletsAddressList) {
//
//
//                    }
        } else
            AndroidAppUtils.showErrorLog(TAG, "allWalletsAddressList is null");

//            } else
//                AndroidAppUtils.showErrorLog(TAG, "intent does not have KEY_ALL_WALLETS_ADDRESS_LIST");
//        } else
//            AndroidAppUtils.showErrorLog(TAG, "getIntent is null");
    }

    /**
     * Initialise GUI  elements
     */
    private void initViews() {

        recyclerView = findViewById(R.id.transactions_list_view);
        wallet_button = findViewById(R.id.wallet_button);
        continue_button = findViewById(R.id.continue_button);
        transactions_refresh = findViewById(R.id.transactions_refresh);
    }

    /**
     * Set wallet list adapter
     */
    private void setAdapter() {

        mAdapter = new TransactionHistoryAdapter(transactionHistoryDataModelList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                AndroidAppUtils.showLog(TAG, "onclick on recycleview position:  " + position);
                openTransactionDetailDialog(position);

            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    private void openTransactionDetailDialog(int position) {

        transactionDetailDialog = new Dialog(this);
        transactionDetailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();

        lWindowParams.copyFrom(transactionDetailDialog.getWindow().getAttributes());
        lWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        transactionDetailDialog.setCanceledOnTouchOutside(true);
        transactionDetailDialog.setContentView(R.layout.transaction_details_fragment);

        initDialogViews();
        if (transactionHistoryDataModelList != null && transactionHistoryDataModelList.size() >= position + 1) {

            txn_title.setText(transactionHistoryDataModelList.get(position).getTransaction_type());

            String sentHHC = "Sent HHC";
            if (transactionHistoryDataModelList.get(position).getTransaction_type().equalsIgnoreCase(sentHHC)) {
                amount_label.setText("-" + transactionHistoryDataModelList.get(position).getTransaction_coins() + HHC);
                txn_icon.setImageResource(R.drawable.ic_yellow_send);
                from_label.setVisibility(View.VISIBLE);
                from_label.setText(transactionHistoryDataModelList.get(position).getWallet_name());
                to_label.setVisibility(View.GONE);
            } else {
                amount_label.setText(transactionHistoryDataModelList.get(position).getTransaction_coins() + HHC);
                txn_icon.setImageResource(R.drawable.recv_blue);
                to_label.setVisibility(View.VISIBLE);
                to_label.setText(transactionHistoryDataModelList.get(position).getWallet_name());
                from_label.setVisibility(View.GONE);
            }

            date_label.setText(transactionHistoryDataModelList.get(position).getDate_and_time());
            if (transactionHistoryDataModelList.get(position).isTransaction_status()) {
                status_checkbox.setImageResource(R.drawable.check_green);
                String confirmed = "Confirmed";
                status_label.setText(confirmed);
            } else {
                status_checkbox.setImageResource(R.drawable.check_green);
                String failed = "Failed";
                status_label.setText(failed);
            }


            address_label.setText(transactionHistoryDataModelList.get(position).getFrom_address());


            to_address_label.setText(transactionHistoryDataModelList.get(position).getTo_address());

            transactionDetailDialog.show();
            transactionDetailDialog.getWindow().setAttributes(lWindowParams);

        } else
            AndroidAppUtils.showErrorLog(TAG, "list is null or size is less than position+1  position: " + position + " size: " + transactionHistoryDataModelList.size());

    }

    /**
     * Initialise transaction detail  dialog views
     */
    private void initDialogViews() {

        if (transactionDetailDialog != null) {
            txn_title = transactionDetailDialog.findViewById(R.id.title);
            txn_icon = transactionDetailDialog.findViewById(R.id.send_icon);
            status_checkbox = transactionDetailDialog.findViewById(R.id.status_checkbox);
            date_label = transactionDetailDialog.findViewById(R.id.date_label);
            status_label = transactionDetailDialog.findViewById(R.id.status_label);

            address_label = transactionDetailDialog.findViewById(R.id.from_address);
            from_label = transactionDetailDialog.findViewById(R.id.from_label);
            to_label = transactionDetailDialog.findViewById(R.id.to_label);
            to_address_label = transactionDetailDialog.findViewById(R.id.to_address_label);
            amount_label = transactionDetailDialog.findViewById(R.id.amount_label);


        } else
            AndroidAppUtils.showErrorLog(TAG, "transactionDetailDialog is  null");
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.wallet_button:

                startActivity(new Intent(this, WalletContainerActivity.class));
                transactionHistoryDataModelList.clear();
                WalletContainerActivity.walletDataModelList.clear();
                finish();


                break;

            case R.id.internet_alert_ok_button:

                if (internetErrorDialog != null) {

                    if (internetErrorDialog.isShowing()) {

                        internetErrorDialog.dismiss();
                        internetErrorDialog.cancel();

                    } else
                        AndroidAppUtils.showErrorLog(TAG, "internetErrorDialog is not showing");
                } else
                    AndroidAppUtils.showErrorLog(TAG, "internetErrorDialog is null");

                break;

            case R.id.continue_button:


                Intent transactionActivityIntent = new Intent(this, TransactionActivity.class);
                transactionActivityIntent.putExtra(GlobalConstants.KEY_WALLET_ACTIVITY, GlobalConstants.TRANSACTION_ACTIVITY);
                startActivity(transactionActivityIntent);
                finish();

                break;

            case R.id.transactions_refresh:


                transactionHistoryDataModelList.clear();
                mAdapter.notifyDataSetChanged();
                hideLoadingDialog();
                showTransactionHistoryList();


                break;


        }

    }


    public static void hideLoadingDialog() {

        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog.cancel();
        } else
            AndroidAppUtils.showErrorLog(TAG, "loading dialog is  null");
    }


    private void showLoadingDialog() {


        if (internetErrorDialog != null) {
            internetErrorDialog.dismiss();
            internetErrorDialog.cancel();
        } else
            AndroidAppUtils.showErrorLog(TAG, "internetErrorDialog is null");

        loadingDialog = new Dialog(this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.loading_dialog);

        alert_message = loadingDialog.findViewById(R.id.alert_message);
        alert_message.setText(getResources().getString(R.string.loading));

        WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();

        lWindowParams.copyFrom(loadingDialog.getWindow().getAttributes());
        lWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();
        loadingDialog.getWindow().setAttributes(lWindowParams);

    }


}
