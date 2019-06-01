package com.example.akansha.cryptocurrency.View;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.akansha.cryptocurrency.Adapter.WalletAddressAdapter;
import com.example.akansha.cryptocurrency.Constants.GlobalConstants;
import com.example.akansha.cryptocurrency.Constants.OnItemClickListener;
import com.example.akansha.cryptocurrency.Control.WalletInfoAPIHandler;
import com.example.akansha.cryptocurrency.Database.DatabaseHelper;
import com.example.akansha.cryptocurrency.Model.WalletInfoDataModel;
import com.example.akansha.cryptocurrency.Model.WalletNameAndAddressDataModel;
import com.example.akansha.cryptocurrency.R;
import com.example.akansha.cryptocurrency.Utils.AndroidAppUtils;
import com.example.akansha.cryptocurrency.Utils.GlobalConfig;
import com.example.akansha.cryptocurrency.iHelper.WebAPIResponseListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.List;

import mobile.Mobile;

public class AddressContainerActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = AddressContainerActivity.class.getSimpleName();
    private String seedValue;
    private int numberOfAddress;

    private String KEY_SEED_VALUE = "SEED_VALUE";
    private String KEY_NUMBER_OF_ADDRESSES = "NUMBER_OF_ADDRESSES";
    private String KEY_WALLET_NAME = "WALLET_NAME";
    private String KEY_WALLET_ID = "WALLET_ID";


    public static List<WalletInfoDataModel> walletInfoDataModelList = new ArrayList<>();
    private RecyclerView recyclerView;
    public static WalletAddressAdapter mAdapter;
    private Toolbar toolbar;
    private TextView wallet_heading, internet_alert_ok_button;
    private ImageView settings_button;
    private String walletName;

    private Dialog qrCodeDialog;
    private ImageView qr_image, copy_icon;
    private TextView address_label;
    private String walletId = "";

    private Dialog internetErrorDialog, seedValueDialog;
    private TextView seed_value, seed_value_ok_button;
    private ImageView transaction_history_icon;

    private String KEY_ALL_WALLETS_ADDRESS_LIST = "ALL_WALLETS_ADDRESS_LIST";
    private String KEY_BUNDLE = "BUNDLE";


    private RelativeLayout continue_button;

    private TextView alert_message, internet_alert_message;
    private static Dialog loadingDialog;
    private ImageView seed_value_view;
    private boolean isGetSeedValue;
    private boolean isPerformingTransaction;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.address_container_activity);

        toolbar = findViewById(R.id.toolbar);

        getSeedAndNumberOfAddresses();

        inittoolbar();

        recyclerView = findViewById(R.id.address_list);
        transaction_history_icon = findViewById(R.id.transaction_history_icon);
        continue_button = findViewById(R.id.continue_button);
        seed_value_view = findViewById(R.id.seed_value_view);

        setAdapter();

        getWalletAddressInfo();

        intitClickListener();


    }

    public static void hideLoadingDialog() {

        if (loadingDialog != null && loadingDialog.isShowing()) {

            loadingDialog.dismiss();
            loadingDialog.cancel();
        } else
            AndroidAppUtils.showErrorLog(TAG, "loading dialog is  null");
    }


    private void intitClickListener() {

        settings_button.setOnClickListener(this);
        transaction_history_icon.setOnClickListener(this);
        continue_button.setOnClickListener(this);
        seed_value_view.setOnClickListener(this);
    }

    private void inittoolbar() {

        wallet_heading = findViewById(R.id.wallet_heading);
        settings_button = findViewById(R.id.settings_button);

        wallet_heading.setText(walletName);
        settings_button.setImageResource(R.drawable.back_white);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
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


    public void getSeedAndNumberOfAddresses() {

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(KEY_SEED_VALUE))
                seedValue = intent.getStringExtra(KEY_SEED_VALUE);
            else
                AndroidAppUtils.showErrorLog(TAG, "intent do nit have seed key");

            if (intent.hasExtra(KEY_NUMBER_OF_ADDRESSES))
                numberOfAddress = intent.getIntExtra(KEY_NUMBER_OF_ADDRESSES, 0);
            else
                AndroidAppUtils.showErrorLog(TAG, "intent do nit have KEY_NUMBER_OF_ADDRESSES key");

            if (intent.hasExtra(KEY_WALLET_NAME))
                walletName = intent.getStringExtra(KEY_WALLET_NAME);
            else
                AndroidAppUtils.showErrorLog(TAG, "intent do nit have KEY_WALLET_NAME key");

            if (intent.hasExtra(KEY_WALLET_ID))
                walletId = intent.getStringExtra(KEY_WALLET_ID);
            else
                AndroidAppUtils.showErrorLog(TAG, "intent do nit have KEY_WALLET_ID key");

        } else
            AndroidAppUtils.showErrorLog(TAG, "AddressContainerActivity intent is null");

    }

    /**
     * Set wallet list adapter
     */
    private void setAdapter() {

        mAdapter = new WalletAddressAdapter(walletInfoDataModelList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                AndroidAppUtils.showLog(TAG, "onclick on recycleview position:  " + position);
                String address = walletInfoDataModelList.get(position).getAddress();
                openQRCodeDialog(address);
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * Open dialog to show QR code
     *
     * @param address
     */
    private void openQRCodeDialog(String address) {

        if (!address.isEmpty()) {
            qrCodeDialog = new Dialog(this);
            qrCodeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            qrCodeDialog.setContentView(R.layout.qr_code_dialog_fragment);

            WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
            lWindowParams.copyFrom(qrCodeDialog.getWindow().getAttributes());
            lWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            initDialogViews();
            initDialogViewsClickListener();
            Bitmap qrCoddeBitmap = generateQRCode(address);

            if (qrCoddeBitmap != null) {
                address_label.setText(address);
                qr_image.setImageBitmap(qrCoddeBitmap);
                qrCodeDialog.setCanceledOnTouchOutside(true);
                qrCodeDialog.show();
            } else
                AndroidAppUtils.showErrorLog(TAG, "qr code bitmap is  null");

        } else
            AndroidAppUtils.showErrorLog(TAG, "address is empty");
    }

    private void initDialogViewsClickListener() {

        copy_icon.setOnClickListener(this);

    }

    private void initDialogViews() {

        if (qrCodeDialog != null) {

            qr_image = qrCodeDialog.findViewById(R.id.qr_image);
            address_label = qrCodeDialog.findViewById(R.id.address_label);
            copy_icon = qrCodeDialog.findViewById(R.id.copy_icon);

        } else
            AndroidAppUtils.showErrorLog(TAG, "qrCodeDialog is null");

    }

    /**
     * Generatr QR code
     */
    private Bitmap generateQRCode(String address) {

        try {

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(address, BarcodeFormat.QR_CODE, 150, 150);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            return bitmap;


        } catch (Exception e) { //eek }
            return null;
        }
    }

    public void getWalletAddressInfo() {

        if (!seedValue.isEmpty() && numberOfAddress != 0) {

            showLoadingDialog();

            new WalletInfoAPIHandler(this).getWalletAddresses(seedValue, numberOfAddress, false, new WebAPIResponseListener() {
                @Override
                public void onSuccessResponse(Object... arguments) {

                }

                @Override
                public void onFailResponse(Object... arguments) {

                    /*if (arguments != null && arguments[0] != null) {

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

        } else
            AndroidAppUtils.showErrorLog(TAG, "seed value is empty or number of address is zero");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, WalletContainerActivity.class));
        walletInfoDataModelList.clear();
        WalletContainerActivity.walletDataModelList.clear();
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.address_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.new_address:

                AndroidAppUtils.showLog(TAG, "Selected new address");
                walletInfoDataModelList.clear();
                createNewAddress();

                return true;

            case R.id.refresh_address:

                walletInfoDataModelList.clear();
                getWalletAddressInfo();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createNewAddress() {

        if (!walletId.isEmpty()) {
            numberOfAddress++;
            AndroidAppUtils.showLog(TAG, "new number of address for seed: " + seedValue + " is: " + numberOfAddress);
            try {

                String address = Mobile.getAddresses(seedValue, numberOfAddress);
                new DatabaseHelper(this).updateNumberOfAddressAndJSONForWallet(walletId, numberOfAddress, address);

                showLoadingDialog();
                new WalletInfoAPIHandler(this).getWalletAddresses(seedValue, numberOfAddress, true, new WebAPIResponseListener() {
                    @Override
                    public void onSuccessResponse(Object... arguments) {
                    }

                    @Override
                    public void onFailResponse(Object... arguments) {

                        /*if (arguments != null && arguments[0] != null) {

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

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            AndroidAppUtils.showErrorLog(TAG, "wallet id is empty");

    }


    private void showInternetAlertDialog(boolean isInternetError, boolean isBadUrl) {


        hideLoadingDialog();

        if (internetErrorDialog == null) {

            internetErrorDialog = new Dialog(this);
            internetErrorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            internetErrorDialog.setContentView(R.layout.internet_alert_dialog);
            internet_alert_ok_button = internetErrorDialog.findViewById(R.id.internet_alert_ok_button);
            internet_alert_message = internetErrorDialog.findViewById(R.id.alert_message);

            if (isBadUrl)
                internet_alert_message.setText(getResources().getString(R.string.invalid_node_url));
            else if (isInternetError)
                internet_alert_message.setText(getResources().getString(R.string.internet_alert_msg));
            else
                internet_alert_message.setText(getResources().getString(R.string.invalid_pin));

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

    private void showSeedValue() {

        if (!seedValue.isEmpty()) {

            seedValueDialog = new Dialog(this);
            seedValueDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


            WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();

            lWindowParams.copyFrom(seedValueDialog.getWindow().getAttributes());
            lWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            seedValueDialog.setCanceledOnTouchOutside(false);
            seedValueDialog.setContentView(R.layout.seed_value_dialog);
            seed_value_ok_button = seedValueDialog.findViewById(R.id.seed_value_ok_button);
            seed_value = seedValueDialog.findViewById(R.id.seed_value);
            seed_value.setText(seedValue);
            seed_value_ok_button.setOnClickListener(this);

            seedValueDialog.show();
            seedValueDialog.getWindow().setAttributes(lWindowParams);

        } else
            AndroidAppUtils.showErrorLog(TAG, "seed value is empty");
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.settings_button:
                onBackPressed();
                break;
            case R.id.copy_icon:

                copyAddressToClipBoard();

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

            case R.id.transaction_history_icon:


                ArrayList<WalletNameAndAddressDataModel> allWalletAddresses = new WalletInfoAPIHandler(this).allWalletsAddresses();
                Intent intent = new Intent(this, TransactionHistoryActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable(KEY_ALL_WALLETS_ADDRESS_LIST, allWalletAddresses);
                intent.putExtra(KEY_BUNDLE, bundle);
                startActivity(intent);
                walletInfoDataModelList.clear();
                finish();

                break;


            case R.id.continue_button:


                Intent transactionActivityIntent = new Intent(this, TransactionActivity.class);
                transactionActivityIntent.putExtra(GlobalConstants.KEY_WALLET_ACTIVITY, GlobalConstants.ADDRESS_ACTIVITY);
                isPerformingTransaction = true;
                startActivity(transactionActivityIntent);
//                finish();

                break;

            case R.id.seed_value_view:

                Intent pinActivityintent = new Intent(this, SetPinActivity.class);
                pinActivityintent.putExtra(GlobalConstants.KEY_GET_SEED_VALUE, true);
                GlobalConfig.isWalletActionAuthenticated = false;
                isGetSeedValue = true;
                startActivity(pinActivityintent);


                break;
            case R.id.seed_value_ok_button:

                if (seedValueDialog != null) {

                    seedValueDialog.dismiss();
                    seedValueDialog.cancel();
                } else
                    AndroidAppUtils.showErrorLog(TAG, "seedValueDialog  is null");

                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        AndroidAppUtils.showLog(TAG, "onResume");

        if (isPerformingTransaction) {

            isPerformingTransaction = false;
            walletInfoDataModelList.clear();
            getWalletAddressInfo();

        } else if (isGetSeedValue) {

            isGetSeedValue = false;

            if (GlobalConfig.isWalletActionAuthenticated) {

                GlobalConfig.isWalletActionAuthenticated = false;
                showSeedValue();

            } else {
                AndroidAppUtils.showErrorLog(TAG, "seed value request is not authenticated");
                showInternetAlertDialog(false, false);

            }
        } else
            AndroidAppUtils.showErrorLog(TAG, "not a seed value  request");

    }

    /**
     * Copy wallet address to clipboard
     */
    private void copyAddressToClipBoard() {

        if (address_label != null && !address_label.getText().toString().isEmpty()) {
            String address = address_label.getText().toString().trim();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("wallet address", address);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, getResources().getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
                if (copy_icon != null)
                    copy_icon.setImageResource(R.drawable.valid);
                else
                    AndroidAppUtils.showErrorLog(TAG, "copy icon is null");
            } else
                AndroidAppUtils.showErrorLog(TAG, "clipboard is null");
        }
    }
}
