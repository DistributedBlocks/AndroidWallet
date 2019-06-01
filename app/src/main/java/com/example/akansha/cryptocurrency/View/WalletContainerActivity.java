package com.example.akansha.cryptocurrency.View;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.akansha.cryptocurrency.Adapter.WalletAdapter;
import com.example.akansha.cryptocurrency.Adapter.WalletSpinnerAdapter;
import com.example.akansha.cryptocurrency.Constants.GlobalConstants;
import com.example.akansha.cryptocurrency.Constants.OnItemClickListener;
import com.example.akansha.cryptocurrency.Control.CreateNewWallet;
import com.example.akansha.cryptocurrency.Control.WalletInfoAPIHandler;
import com.example.akansha.cryptocurrency.Model.WalletDataModel;
import com.example.akansha.cryptocurrency.Model.WalletNameAndAddressDataModel;
import com.example.akansha.cryptocurrency.R;
import com.example.akansha.cryptocurrency.Utils.AndroidAppUtils;
import com.example.akansha.cryptocurrency.iHelper.WebAPIResponseListener;

import java.util.ArrayList;

import mobile.Mobile;

//import android.widget.Toolbar;


/**
 * Class to show wallet list
 */
public class WalletContainerActivity extends AppCompatActivity implements View.OnClickListener, MenuItem.OnMenuItemClickListener {

    private static String TAG = WalletContainerActivity.class.getSimpleName();

    public static ArrayList<WalletDataModel> walletDataModelList = new ArrayList<>();
    private RecyclerView recyclerView;
    public static WalletAdapter mAdapter;

    private Dialog newWalletDialog, walletSeedAlertDialog;
    private EditText wallet_name_text, wallet_seed_text, wallet_seed_confirm;
    private Button cancel_button, create_button;
    private String seed_value = "";
    private boolean isCreateNewWalletClicked;
    private TextView alert_ok_button, wallet_confirm_seed_heading, internet_alert_ok_button, internet_alert_msg;

    private AppCompatActivity mActivity;

    private String KEY_SEED_VALUE = "SEED_VALUE";
    private String KEY_NUMBER_OF_ADDRESSES = "NUMBER_OF_ADDRESSES";
    private String KEY_WALLET_NAME = "WALLET_NAME";
    private String KEY_WALLET_ID = "WALLET_ID";

    private Toolbar toolbar;
    private ImageView transaction_history_icon;

    private String KEY_ALL_WALLETS_ADDRESS_LIST = "ALL_WALLETS_ADDRESS_LIST";
    private String KEY_BUNDLE = "BUNDLE";
    private ImageView settings_button;

    private Dialog internetErrorDialog;

    private RelativeLayout continue_button;
    private Dialog transactionDialog;
    private Button transaction_cancel_button, send_button, advanced_button;

    private Spinner wallet_selector;
    private EditText address_text, amount_text, note_text;

    private WalletSpinnerAdapter walletSpinnerAdapter;
    private static Dialog loadingDialog;
    private TextView alert_message;
    private TextView wallet_balance_num, wallet_balance_hours;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallets_container_activity);

        mActivity = this;

        initViews();
        initClickListener();
        inittoolbar();

        setAdapter();

        showLoadingDialog();

        new WalletInfoAPIHandler(this).getUserWalletsAddressInfo(false, new WebAPIResponseListener() {
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


//                showInternetAlertDialog();

            }

            @Override
            public void onOfflineResponse(Object... arguments) {

            }
        });

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

    private void initClickListener() {
        transaction_history_icon.setOnClickListener(this);
        settings_button.setOnClickListener(this);
        continue_button.setOnClickListener(this);
    }

    public static void hideLoadingDialog() {

        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog.cancel();
        } else
            AndroidAppUtils.showErrorLog(TAG, "loading dialog is  null");
    }

    private void initViews() {

        recyclerView = findViewById(R.id.wallet_list);
        toolbar = findViewById(R.id.toolbar);
        transaction_history_icon = findViewById(R.id.transaction_history_icon);
        settings_button = findViewById(R.id.settings_button);
        continue_button = findViewById(R.id.continue_button);
        wallet_balance_num = findViewById(R.id.wallet_balance_num);
        wallet_balance_hours = findViewById(R.id.wallet_balance_hours);
    }

    /*
     * Initialise the toolbar
     * */
    private void inittoolbar() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.wallet_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_icon);
        menuItem.setOnMenuItemClickListener(this);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.new_wallet:

                AndroidAppUtils.showLog(TAG, "Selected new wallet");
                isCreateNewWalletClicked = true;
                openNewWalletDialogFragment();

                return true;
            case R.id.load_wallet:

                AndroidAppUtils.showLog(TAG, "Selected load wallet");
                isCreateNewWalletClicked = false;
                openNewWalletDialogFragment();

                return true;

            case R.id.refresh_wallet:

                AndroidAppUtils.showLog(TAG, "Selected refresh wallet");

                walletDataModelList.clear();
                mAdapter.notifyDataSetChanged();
                wallet_balance_num.setText(String.valueOf(0.0));
                wallet_balance_hours.setText(String.valueOf(0));
                hideLoadingDialog();
                showLoadingDialog();

                new WalletInfoAPIHandler(this).getUserWalletsAddressInfo(false, new WebAPIResponseListener() {
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

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Set wallet list adapter
     */
    private void setAdapter() {

        mAdapter = new WalletAdapter(walletDataModelList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                AndroidAppUtils.showLog(TAG, "onclick on recycleview position:  " + position);
                switch (view.getId()) {

                    case R.id.wallet_info_rl:

                        AndroidAppUtils.showLog(TAG, "Wallet info row clicked at position: " + position);

                        String seedValue = walletDataModelList.get(position).getSeedValue();
                        int numberOfAddresses = walletDataModelList.get(position).getNumberOfAddress();
                        String walletName = walletDataModelList.get(position).getWallet_name();
                        String walletId = walletDataModelList.get(position).getWalletId();


                        Intent intent = new Intent(mActivity, AddressContainerActivity.class);
                        intent.putExtra(KEY_SEED_VALUE, seedValue);
                        intent.putExtra(KEY_NUMBER_OF_ADDRESSES, numberOfAddresses);
                        intent.putExtra(KEY_WALLET_NAME, walletName);
                        intent.putExtra(KEY_WALLET_ID, walletId);


                        mActivity.startActivity(intent);
//                        walletDataModelList.clear();
                        mActivity.finish();

                        break;
                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    private void openNewWalletDialogFragment() {

        newWalletDialog = new Dialog(this);

        WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
        lWindowParams.copyFrom(newWalletDialog.getWindow().getAttributes());
        lWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        lWindowParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        newWalletDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        newWalletDialog.setContentView(R.layout.new_wallet_dialog_fragment);

        initNewWalletDialogViews();
        initDialogViewClickListener();


        if (!isCreateNewWalletClicked) {

            wallet_confirm_seed_heading.setVisibility(View.GONE);
            wallet_seed_confirm.setVisibility(View.GONE);
            wallet_seed_text.setText("");
            create_button.setEnabled(true);

        } else {
            AndroidAppUtils.showLog(TAG, "Load new wallet selected");
            setSeedValue();

        }
        newWalletDialog.show();
        newWalletDialog.getWindow().setAttributes(lWindowParams);


    }

    private void setWalletSeedTextListener() {

        wallet_seed_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                AndroidAppUtils.showLog(TAG, "seed text changed: " + wallet_seed_text.getText());
                seed_value = wallet_seed_text.getText().toString();

            }
        });

    }

    private void setConfirmSeedTextListener() {

        wallet_seed_confirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                AndroidAppUtils.showLog(TAG, "confirm seed text changed: " + wallet_seed_confirm.getText());
                if (wallet_seed_confirm.getText().toString().equals(seed_value)) {
                    AndroidAppUtils.showLog(TAG, "seed value and confirm seed value are equal");
                    create_button.setEnabled(true);
                } else
                    create_button.setEnabled(false);

            }
        });

    }


    /**
     * Create New Wallet
     */
    private void createNewWallet() {

        if (!seed_value.isEmpty()) {

            String walletName = wallet_name_text.getText().toString();
            if (walletName.trim().isEmpty())
                walletName = "unNamedWallet";

            try {

                newWalletDialog.dismiss();
                newWalletDialog.cancel();
                new CreateNewWallet(this).createNewWallet(seed_value, walletName);

                showLoadingDialog();

                new WalletInfoAPIHandler(this).getUserWalletsAddressInfo(true, new WebAPIResponseListener() {
                    @Override
                    public void onSuccessResponse(Object... arguments) {

                    }

                    @Override
                    public void onFailResponse(Object... arguments) {


                        if (arguments != null && arguments[0] != null) {

                            AndroidAppUtils.showErrorLog(TAG, "code: " + arguments[0]);

                            if ((Integer) arguments[0] == 400) {
                                AndroidAppUtils.showErrorLog(TAG, "Bad URL: ");
                                showInternetAlertDialog(false, true);
                            } else
                                showInternetAlertDialog(true, false);

                        } else
                            showInternetAlertDialog(true, false);


//                        showInternetAlertDialog();

                    }

                    @Override
                    public void onOfflineResponse(Object... arguments) {

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            AndroidAppUtils.showErrorLog(TAG, "seed value is empty");
            showWalletSeedAlert();
        }
    }

    private void showInternetAlertDialog(boolean isInternetError, boolean isBadUrl) {

        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog.cancel();
        }

        if (internetErrorDialog == null) {

            internetErrorDialog = new Dialog(this);
            internetErrorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            internetErrorDialog.setContentView(R.layout.internet_alert_dialog);

            internet_alert_ok_button = internetErrorDialog.findViewById(R.id.internet_alert_ok_button);
            internet_alert_msg = internetErrorDialog.findViewById(R.id.alert_message);


            if (isBadUrl)
                internet_alert_msg.setText(getResources().getString(R.string.invalid_node_url));
            else
                internet_alert_msg.setText(getResources().getString(R.string.internet_alert_msg));

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

    /**
     * Show wallet alert seed
     */
    private void showWalletSeedAlert() {


        walletSeedAlertDialog = new Dialog(this);
        walletSeedAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        walletSeedAlertDialog.setContentView(R.layout.seed_value_alert_dialog);
        alert_ok_button = walletSeedAlertDialog.findViewById(R.id.seed_alert_ok_button);
        alert_ok_button.setOnClickListener(this);
        walletSeedAlertDialog.show();


    }

    /**
     * Set seed value
     */
    private void setSeedValue() {

        try {

            seed_value = Mobile.newWordSeed();
            wallet_seed_text.setText(seed_value);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialise click listener on dialog views
     */
    private void initDialogViewClickListener() {

        cancel_button.setOnClickListener(this);
        create_button.setOnClickListener(this);
        setWalletSeedTextListener();
        setConfirmSeedTextListener();

    }

    /**
     * Initialise new wallet dialog views
     */
    private void initNewWalletDialogViews() {

        if (newWalletDialog != null) {

            wallet_name_text = newWalletDialog.findViewById(R.id.wallet_name_text);
            wallet_seed_text = newWalletDialog.findViewById(R.id.wallet_seed_text);

            wallet_seed_text.setRawInputType(InputType.TYPE_CLASS_TEXT);
            wallet_seed_text.setImeOptions(EditorInfo.IME_ACTION_NEXT);

            wallet_seed_confirm = newWalletDialog.findViewById(R.id.wallet_seed_confirm);
            wallet_seed_confirm.setRawInputType(InputType.TYPE_CLASS_TEXT);
            wallet_seed_confirm.setImeOptions(EditorInfo.IME_ACTION_DONE);

            wallet_confirm_seed_heading = newWalletDialog.findViewById(R.id.wallet_confirm_seed_heading);
            cancel_button = newWalletDialog.findViewById(R.id.cancel_button);
            create_button = newWalletDialog.findViewById(R.id.create_button);

        } else
            AndroidAppUtils.showErrorLog(TAG, "newWalletDialog is null");
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.create_button:

                if (newWalletDialog != null) {

                    createNewWallet();


                } else
                    AndroidAppUtils.showErrorLog(TAG, "newWalletDialog is null");


                break;
            case R.id.cancel_button:

                if (newWalletDialog != null) {

                    newWalletDialog.dismiss();
                    newWalletDialog.cancel();

                } else
                    AndroidAppUtils.showErrorLog(TAG, "newWalletDialog is null");

                break;

            case R.id.seed_alert_ok_button:

                if (walletSeedAlertDialog != null) {

                    walletSeedAlertDialog.dismiss();
                    walletSeedAlertDialog.cancel();

                } else
                    AndroidAppUtils.showErrorLog(TAG, "Wallet seed alet is null");

                break;
            case R.id.transaction_history_icon:


                ArrayList<WalletNameAndAddressDataModel> allWalletAddresses = new WalletInfoAPIHandler(this).allWalletsAddresses();
                Intent intent = new Intent(this, TransactionHistoryActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable(KEY_ALL_WALLETS_ADDRESS_LIST, allWalletAddresses);
                intent.putExtra(KEY_BUNDLE, bundle);
                startActivity(intent);
                finish();

                break;
            case R.id.settings_button:

                startActivity(new Intent(this, SettingsActivity.class));
                walletDataModelList.clear();
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
                transactionActivityIntent.putExtra(GlobalConstants.KEY_WALLET_ACTIVITY, GlobalConstants.WALLET_ACTIVITY);
                startActivity(transactionActivityIntent);
                finish();

                break;


        }

    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.new_wallet:

                AndroidAppUtils.showLog(TAG, "Selected new wallet");
                return true;
            case R.id.load_wallet:
                AndroidAppUtils.showLog(TAG, "Selected load wallet");
                return true;


        }
        return false;

    }

}
