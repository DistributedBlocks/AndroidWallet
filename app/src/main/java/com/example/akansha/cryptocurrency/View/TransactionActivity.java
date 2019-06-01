package com.example.akansha.cryptocurrency.View;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.akansha.cryptocurrency.Adapter.WalletSpinnerAdapter;
import com.example.akansha.cryptocurrency.Constants.GlobalConstants;
import com.example.akansha.cryptocurrency.Database.DatabaseHelper;
import com.example.akansha.cryptocurrency.R;
import com.example.akansha.cryptocurrency.Utils.AndroidAppUtils;
import com.example.akansha.cryptocurrency.Utils.GlobalConfig;
import com.example.akansha.cryptocurrency.WebServices.GetWalletHashAPIHandler;
import com.example.akansha.cryptocurrency.WebServices.PostTransactionAPIHandler;
import com.example.akansha.cryptocurrency.iHelper.WebAPIResponseListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import mobile.Mobile;

public class TransactionActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {


    private String TAG = TransactionActivity.class.getSimpleName();

    private final String KEY_HEAD_OUTPUTS = "head_outputs";
    private AppCompatActivity mActivity;

    private Button transaction_cancel_button, send_button;

    private Spinner wallet_selector;
    private EditText address_text, amount_text, note_text;

    private WalletSpinnerAdapter walletSpinnerAdapter;
    private int walletId;
    private String ADDRESS_KEY = "Address";
    private ImageView qr_button;


    private String KEY_SECRET = "Secret";

    private HashMap<String, ArrayList<String>> addressHashValueMap;
    private HashMap<String, ArrayList<String>> addressCoinsMap;
    private HashMap<String, ArrayList<String>> addressHoursMap;
    private ArrayList<String> walletAddressesList;
    private HashMap<String, String> walletAddressSecretMap;

    private String walletAddress = "";

    private double total_coins_to_send = 0.0;
    private double wallet_coins = 0.0;
    private double current_coins = 0.0;
    private double remaining_coins_to_Send = 0.0;

    private String KEY_HASH = "hash";
    private String KEY_ADDRESS = "address";
    private String KEY_COINS = "coins";
    private String KEY_HOURS = "hours";
    private String KEY_CALCULATED_HOURS = "calculated_hours";

    private String sendToAddress = "";

    String amount_value = "";
    private String KEY_IS_TRANSACTION = "IS_TRANSACTION";

    private Dialog sendingDialog;
    private boolean isPerformTransaction = false;
    private Dialog internetErrorDialog;
    private TextView internet_alert_ok_button, alert_message;

    private Dialog sendSuccessDialog;
    private TextView send_ok_button, success_msg, copy_txid_btn;

    private String walletActivity = "";
    private long totalCalculatedHours;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_fragment);
        mActivity = this;

        Intent intent = getIntent();

        if (intent != null) {
            if (intent.hasExtra(GlobalConstants.KEY_WALLET_ACTIVITY))
                walletActivity = intent.getStringExtra(GlobalConstants.WALLET_ACTIVITY);
            else
                AndroidAppUtils.showErrorLog(TAG, "intent donot have KEY_IS_FROM_WALLET_CONTAINER_ACTIVITY");
        } else
            AndroidAppUtils.showErrorLog(TAG, "intent is null");


        initViews();
    }


    /**
     * Initialise view elements
     */
    private void initViews() {


        walletAddressSecretMap = new HashMap<>();
        addressHashValueMap = new HashMap<>();
        addressCoinsMap = new HashMap<>();
        addressHoursMap = new HashMap<>();
        walletAddressesList = new ArrayList<>();


        initTransactionDialogViews();


        initTransactionDialogClickListener();

        initAmountTextWatcher();

        initAddressTextWatcher();

        setAdapterToWalletList();


    }

    private void setAdapterToWalletList() {

        // Resources passed to adapter to get image
        Resources res = getApplicationContext().getResources();

        // Create custom adapter object ( see below CustomAdapter.java )
        walletSpinnerAdapter = new WalletSpinnerAdapter(mActivity.getApplicationContext(), R.layout.custom_spinner_row, WalletContainerActivity.walletDataModelList, res);

        // Set adapter to spinner
        wallet_selector.setAdapter(walletSpinnerAdapter);


    }

    @Override
    protected void onResume() {

        super.onResume();
        AndroidAppUtils.showLog(TAG, "onResume");
        if (isPerformTransaction) {


            if (GlobalConfig.isWalletActionAuthenticated) {
                GlobalConfig.isWalletActionAuthenticated = false;
                showProgressDialog();
                performTransaction();
            } else {
                AndroidAppUtils.showErrorLog(TAG, "transaction is not authenticated");
                showAlertDialog(false, false);
            }
        }


    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void showProgressDialog() {

        sendingDialog = new Dialog(this);

        sendingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        sendingDialog.setContentView(R.layout.loading_dialog);

        WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();

        lWindowParams.copyFrom(sendingDialog.getWindow().getAttributes());
        lWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        sendingDialog.setCanceledOnTouchOutside(false);
        sendingDialog.show();
        sendingDialog.getWindow().setAttributes(lWindowParams);

    }

    private void initTransactionDialogViews() {


        transaction_cancel_button = findViewById(R.id.transaction_cancel_button);
        send_button = findViewById(R.id.send_button);
        wallet_selector = findViewById(R.id.wallet_selector);
        address_text = findViewById(R.id.address_text);
        amount_text = findViewById(R.id.amount_text);
        note_text = findViewById(R.id.note_text);
        qr_button = findViewById(R.id.qr_button);

        send_button.setEnabled(false);


    }

    private void initTransactionDialogClickListener() {

        if (transaction_cancel_button != null)
            transaction_cancel_button.setOnClickListener(this);
        else
            AndroidAppUtils.showErrorLog(TAG, "transaction_cancel_button is null");

        if (send_button != null)
            send_button.setOnClickListener(this);
        else
            AndroidAppUtils.showErrorLog(TAG, "send_button is null");

        if (wallet_selector != null)
            wallet_selector.setOnItemSelectedListener(this);
        else
            AndroidAppUtils.showErrorLog(TAG, "wallet_selector is null");

        if (qr_button != null)
            qr_button.setOnClickListener(this);
        else
            AndroidAppUtils.showErrorLog(TAG, "qr_button is null");


    }

    private void initAddressTextWatcher() {

        if (address_text != null) {

            address_text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    sendToAddress = address_text.getText().toString().trim();

                    if (!sendToAddress.isEmpty() && !amount_value.isEmpty() && Double.parseDouble(amount_value) <= wallet_coins && Double.parseDouble(amount_value) != 0.0)
                        send_button.setEnabled(true);
                    else {

                        AndroidAppUtils.showErrorLog(TAG, "send to address is empty or amount value is greater than wallet amount");
                        send_button.setEnabled(false);
                    }
                }
            });
        } else
            AndroidAppUtils.showErrorLog(TAG, "address_text is  null");

    }

    private void initAmountTextWatcher() {

        if (amount_text != null) {

            amount_text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    amount_value = amount_text.getText().toString().trim();

                    AndroidAppUtils.showLog(TAG, "amount value: " + amount_value);

                    try {

                        if (!sendToAddress.isEmpty() && !amount_value.isEmpty() && Double.parseDouble(amount_value) <= wallet_coins && Double.parseDouble(amount_value) != 0.0)
                            send_button.setEnabled(true);
                        else {
                            AndroidAppUtils.showErrorLog(TAG, "send to address is empty or amount value is greater than wallet amount");
                            send_button.setEnabled(false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        } else
            AndroidAppUtils.showErrorLog(TAG, "amount text is  null");

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.transaction_cancel_button:

                AndroidAppUtils.showLog(TAG, "Clicked cancel button");

                if (walletActivity.equals(GlobalConstants.WALLET_ACTIVITY)) {
                    WalletContainerActivity.walletDataModelList.clear();
                    startActivity(new Intent(this, WalletContainerActivity.class));
                } else if (walletActivity.equals(GlobalConstants.TRANSACTION_ACTIVITY)) {
                    TransactionHistoryActivity.transactionHistoryDataModelList.clear();
                    startActivity(new Intent(this, TransactionHistoryActivity.class));
                }
                finish();

                break;
            case R.id.send_button:


                if (!amount_value.isEmpty()) {

                    double amount = roundTwoDecimals(Double.parseDouble(amount_value));
                    AndroidAppUtils.showLog(TAG,"Amount:  "+amount);
//                        sendToAddress = address_text.getText().toString().trim();

                    if (amount != 0.0) {
                        total_coins_to_send = amount;
                        remaining_coins_to_Send = amount;

                        Intent intent = new Intent(this, SetPinActivity.class);
                        intent.putExtra(KEY_IS_TRANSACTION, true);
                        GlobalConfig.isWalletActionAuthenticated = false;
                        isPerformTransaction = true;
                        startActivity(intent);

//                        performTransaction();
                    } else
                        AndroidAppUtils.showErrorLog(TAG, "amount to send is 0");


                } else
                    AndroidAppUtils.showErrorLog(TAG, "amount is empty");


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
            case R.id.send_ok_button:

                if (sendSuccessDialog != null) {
                    sendSuccessDialog.dismiss();
                    sendSuccessDialog.cancel();

                } else
                    AndroidAppUtils.showErrorLog(TAG, "alert dialog is null");

                finishTransaction();

                break;

            case R.id.copy_txid_btn:

                copyTXIDToClipboard();


                break;

            case R.id.qr_button:

                scanQRCode();

                break;
        }

    }

    private void finishTransaction() {


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (walletActivity.equals(GlobalConstants.WALLET_ACTIVITY)) {
                    AndroidAppUtils.showLog(TAG, "opening wallet container activity");
                    WalletContainerActivity.walletDataModelList.clear();
                    startActivity(new Intent(mActivity, WalletContainerActivity.class));
                } else if (walletActivity.equals(GlobalConstants.TRANSACTION_ACTIVITY)) {
                    AndroidAppUtils.showLog(TAG, "opening transaction history activity");
                    TransactionHistoryActivity.transactionHistoryDataModelList.clear();
                    startActivity(new Intent(mActivity, TransactionHistoryActivity.class));
                }
                finish();

            }
        }, GlobalConstants.TIME_ONE_SECOND);


    }

    /**
     *
     */
    private void copyTXIDToClipboard() {

        if (success_msg != null && !success_msg.getText().toString().isEmpty()) {
            String txid = success_msg.getText().toString().trim();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("TXID", txid);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, getResources().getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
            } else
                AndroidAppUtils.showErrorLog(TAG, "clipboard is null");
        }
    }

    /**
     * Scan QR code
     */
    private void scanQRCode() {

        new IntentIntegrator(this).initiateScan();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {

                AndroidAppUtils.showLog(TAG, "scan data: " + result.getContents());
                if (address_text != null)
                    address_text.setText(result.getContents());
                else
                    AndroidAppUtils.showErrorLog(TAG, "address text is null");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    /**
     * Perform transaction
     */
    private void performTransaction() {

        if (mActivity != null && WalletContainerActivity.walletDataModelList != null) {

            DatabaseHelper databaseHelper = new DatabaseHelper(mActivity);
            String addressesJson = databaseHelper.getWalletInfo(WalletContainerActivity.walletDataModelList.get(walletId).getWalletId());
            String walletAddresses = getWalletAddresses(addressesJson);
            if (!walletAddresses.isEmpty()) {

                new GetWalletHashAPIHandler(walletAddresses, new WebAPIResponseListener() {
                    @Override
                    public void onSuccessResponse(Object... arguments) {

                        parserAPIResponse(arguments);


                    }

                    @Override
                    public void onFailResponse(Object... arguments) {

                        if (sendingDialog != null) {
                            sendingDialog.dismiss();
                            sendingDialog.cancel();
                        } else
                            AndroidAppUtils.showErrorLog(TAG, "sendingDialog is null");

                        AndroidAppUtils.showErrorLog(TAG, "onFailResponse: Get API");


                       /* if (arguments != null && arguments[0] != null) {

                            AndroidAppUtils.showErrorLog(TAG, "code: " + arguments[0]);

                            if ((Integer) arguments[0] == 400) {
                                AndroidAppUtils.showErrorLog(TAG, "Bad URL: ");
                                showAlertDialog(false, true);
                            } else
                                showAlertDialog(true, false);

                        } else
                            showAlertDialog(true, false);*/


                        showAlertDialog(true, false);
                        resetTransaction();

                    }

                    @Override
                    public void onOfflineResponse(Object... arguments) {

                    }
                });

            } else
                AndroidAppUtils.showErrorLog(TAG, "walletAddresses is empty");

        } else
            AndroidAppUtils.showErrorLog(TAG, "mActivity is null");

    }

    private void resetTransaction() {

        total_coins_to_send = 0.0;
        current_coins = 0.0;
        remaining_coins_to_Send = 0.0;
        isPerformTransaction = false;

    }

    private void showAlertDialog(boolean isInternetError, boolean isBadUrl) {

        internetErrorDialog = new Dialog(this);
        internetErrorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        internetErrorDialog.setContentView(R.layout.internet_alert_dialog);

        WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();

        lWindowParams.copyFrom(internetErrorDialog.getWindow().getAttributes());
        lWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        internet_alert_ok_button = internetErrorDialog.findViewById(R.id.internet_alert_ok_button);
        alert_message = internetErrorDialog.findViewById(R.id.alert_message);

        if (isBadUrl)
            alert_message.setText(getResources().getString(R.string.invalid_node_url));
        else if (isInternetError)
            alert_message.setText(getResources().getString(R.string.internet_alert_msg));
        else
            alert_message.setText(getResources().getString(R.string.error_transaction_failed));

        internet_alert_ok_button.setOnClickListener(this);

        internetErrorDialog.show();
        internetErrorDialog.getWindow().setAttributes(lWindowParams);

    }

    private void parserAPIResponse(Object[] arguments) {

        if (arguments != null && arguments.length > 0) {

            try {

                String address = "", hash = "", coins = "";
                long hours = 0;

                JSONObject outputsJson = (JSONObject) arguments[0];
                if (outputsJson.has(KEY_HEAD_OUTPUTS)) {

                    JSONArray head_outputs_array = outputsJson.getJSONArray(KEY_HEAD_OUTPUTS);

                    if (head_outputs_array != null && head_outputs_array.length() > 0) {
                        for (int i = 0; i < head_outputs_array.length(); i++) {
                            JSONObject head_output_json = head_outputs_array.getJSONObject(i);
                            if (head_output_json != null) {

                                if (head_output_json.has(KEY_HASH))
                                    hash = head_output_json.getString(KEY_HASH);
                                else
                                    AndroidAppUtils.showErrorLog(TAG, "head_output_json donot have KEY_HASH");

                                if (head_output_json.has(KEY_ADDRESS))
                                    address = head_output_json.getString(KEY_ADDRESS);
                                else
                                    AndroidAppUtils.showErrorLog(TAG, "head_output_json donot have KEY_ADDRESS");

                                if (head_output_json.has(KEY_COINS))
                                    coins = head_output_json.getString(KEY_COINS);
                                else
                                    AndroidAppUtils.showErrorLog(TAG, "head_output_json donot have KEY_COINS");

                                if (head_output_json.has(KEY_CALCULATED_HOURS))
                                    hours = head_output_json.getLong(KEY_CALCULATED_HOURS);
                                else
                                    AndroidAppUtils.showErrorLog(TAG, "head_output_json donot have KEY_CALCULATED_HOURS");


                                AndroidAppUtils.showLog(TAG, "Address: " + address + " hash: " + hash + "Coins: " + coins + " Hours: " + hours);

                                if (!address.isEmpty() && !coins.isEmpty() && !hash.isEmpty()) {


                                    boolean isTransactionAmountComplete = addCoinsToTransfer(coins, address, hash, hours);

                                    if (isTransactionAmountComplete) {

                                        prepareTransaction();
                                        break;
                                    }

                                } else
                                    AndroidAppUtils.showErrorLog(TAG, "address or coins or hash is empty");

                            } else
                                AndroidAppUtils.showErrorLog(TAG, "head output json is null");
                        }
                    } else
                        AndroidAppUtils.showErrorLog(TAG, "head_outputs_array is null or empty");


                } else
                    AndroidAppUtils.showErrorLog(TAG, "json donot have head_outputs");


            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else
            AndroidAppUtils.showErrorLog(TAG, "arguments is  null or empty");

    }

    private void prepareTransaction() {

        JSONArray inputJsonArray = new JSONArray();
        JSONArray outputJsonArray = new JSONArray();

        String KEY_SECRET = "Secret";
        String KEY_HASH = "Hash";
        String secret = "", hash = "";
        String KEY_ADDRESS = "Address";
        String KEY_COINS = "Coins";
        String KEY_HOURS = "Hours";

        ArrayList<String> hashValueArrayList = new ArrayList<>();
        JSONObject inputJson, outputJson;

        if (walletAddressSecretMap != null && walletAddressSecretMap.size() > 0 && addressHashValueMap != null && addressHashValueMap.size() > 0) {

            for (String address : walletAddressesList) {

                AndroidAppUtils.showLog(TAG, "address: " + address);

                for (Map.Entry<String, String> entry : walletAddressSecretMap.entrySet()) {
                    AndroidAppUtils.showLog(TAG, entry.getKey() + " = " + entry.getValue());
                    if (entry.getKey().equals(address)) {
                        AndroidAppUtils.showLog(TAG, "Secret for address: " + address + "  " + secret);
                        secret = entry.getValue();
                        break;
                    }
                }

                for (Map.Entry<String, ArrayList<String>> entry : addressHashValueMap.entrySet()) {
                    AndroidAppUtils.showLog(TAG, entry.getKey() + " = " + entry.getValue());
                    if (entry.getKey().equals(address)) {
                        hashValueArrayList = entry.getValue();
                        break;
                    }
                }

                for (String hashValue : hashValueArrayList) {
                    inputJson = new JSONObject();
                    try {

                        AndroidAppUtils.showLog(TAG, "adding secret: " + secret + " hash value: " + hashValue);
                        inputJson.put(KEY_SECRET, secret);
                        inputJson.put(KEY_HASH, hashValue);
                        inputJsonArray.put(inputJson);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }

        } else
            AndroidAppUtils.showErrorLog(TAG, "walletAddressSecretMap is null or empty or addressHashValueMap is null or empty");


        if (addressCoinsMap != null && addressCoinsMap.size() > 0) {

            AndroidAppUtils.showLog(TAG, "Total calculated hours: " + totalCalculatedHours);

            long feeHours = (long) Math.floor(totalCalculatedHours / 2);

            AndroidAppUtils.showLog(TAG, "Fee hours: " + feeHours);

            long hoursToSend = (long) Math.floor(feeHours / addressCoinsMap.size());

            AndroidAppUtils.showLog(TAG, "Hours to send: " + hoursToSend);

            for (Map.Entry<String, ArrayList<String>> entry : addressCoinsMap.entrySet()) {

                AndroidAppUtils.showLog(TAG, entry.getKey() + " = " + entry.getValue());
                try {

                    String address = entry.getKey();
                    Double coinsInAddress = 0.0;

                    ArrayList<String> coinsList = entry.getValue();
                    long hours = 0;

                    if (coinsList != null && coinsList.size() > 0) {

                        for (String coins : coinsList) {

                            AndroidAppUtils.showLog(TAG, "address: " + address + " coins: " + coins);
                            coinsInAddress = roundTwoDecimals(coinsInAddress + Double.parseDouble(coins));
                            AndroidAppUtils.showLog(TAG, "total coins in address: " + coinsInAddress);

                        }


                        AndroidAppUtils.showLog(TAG, "Adding address: " + address + "coins: " + coinsInAddress + " Hours: " + hoursToSend);


                        outputJson = new JSONObject();
                        outputJson.put(KEY_ADDRESS, address);
                        outputJson.put(KEY_COINS, coinsInAddress * 1000000);
                        outputJson.put(KEY_HOURS, hoursToSend);
                        outputJsonArray.put(outputJson);

                    } else
                        AndroidAppUtils.showErrorLog(TAG, "coinsList is null or empty");


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        } else
            AndroidAppUtils.showErrorLog(TAG, "addressCoinsMap is null  or empty");

        if (inputJsonArray.length() > 0 && outputJsonArray.length() > 0) {
            try {

                String KEY_RAWTX = "rawtx";

                AndroidAppUtils.showLog(TAG, "input array: " + inputJsonArray.toString());
                AndroidAppUtils.showLog(TAG, "output array: " + outputJsonArray.toString());

                String transactionHexValue = Mobile.prepareTransaction(inputJsonArray.toString(), outputJsonArray.toString());
                AndroidAppUtils.showLog(TAG, "hex value: " + transactionHexValue);

                JSONObject postTransactionJson = new JSONObject();
                postTransactionJson.put(KEY_RAWTX, transactionHexValue);


                new PostTransactionAPIHandler(mActivity, new WebAPIResponseListener() {
                    @Override
                    public void onSuccessResponse(final Object... arguments) {

                        AndroidAppUtils.showLog(TAG, "success response: " + arguments[0]);

                        if (mActivity != null) {

                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (sendingDialog != null) {
                                        sendingDialog.dismiss();
                                        sendingDialog.cancel();
                                    } else
                                        AndroidAppUtils.showErrorLog(TAG, "sendingDialog is null");

                                    if (arguments[0] != null)
                                        showSendSuccessDialog((String) arguments[0]);
                                    else
                                        AndroidAppUtils.showErrorLog(TAG, "arguments[0] is null");

                                }
                            });


                        } else
                            AndroidAppUtils.showErrorLog(TAG, "mActivity is null");

                    }

                    @Override
                    public void onFailResponse(Object... arguments) {

                        if (sendingDialog != null) {
                            sendingDialog.dismiss();
                            sendingDialog.cancel();
                        } else
                            AndroidAppUtils.showErrorLog(TAG, "sendingDialog is null");

                        AndroidAppUtils.showErrorLog(TAG, "onFailResponse: post API");

                        current_coins = 0.0;
                        remaining_coins_to_Send = 0.0;

                        showAlertDialog(false, false);
                        resetTransaction();

                    }

                    @Override
                    public void onOfflineResponse(Object... arguments) {

                    }
                }).postData(postTransactionJson);
//
            } catch (Exception e) {
                e.printStackTrace();

                if (sendingDialog != null) {
                    sendingDialog.dismiss();
                    sendingDialog.cancel();
                } else
                    AndroidAppUtils.showErrorLog(TAG, "sendingDialog is null");
            }
        } else
            AndroidAppUtils.showErrorLog(TAG, "inputJsonArray or outputJsonArray length is zero");

    }

    private boolean addCoinsToTransfer(String coins, String address, String hash, long hours) {

        boolean isCoinsComplete = false;
        Double apiCoins = roundTwoDecimals(Double.parseDouble(coins));

        AndroidAppUtils.showLog(TAG, "Api  coins: " + apiCoins);
        AndroidAppUtils.showLog(TAG, "Remaining coins: " + remaining_coins_to_Send);
        AndroidAppUtils.showLog(TAG, "current coins: " + current_coins);
        AndroidAppUtils.showLog(TAG, "Total coins to send: " + total_coins_to_send);

        if (apiCoins == remaining_coins_to_Send) {

            AndroidAppUtils.showLog(TAG, "api coin is equal to remaining coins");

            if (!walletAddressesList.contains(address))
                walletAddressesList.add(address);
            else
                AndroidAppUtils.showErrorLog(TAG, "address list already contains address: " + address);

            // Add hash value to map
            if (!addressHashValueMap.containsKey(address)) {

                ArrayList<String> hashList = new ArrayList<>();
                hashList.add(hash);
                addressHashValueMap.put(address, hashList);
            } else {
                AndroidAppUtils.showLog(TAG, "hashmap already contains address: " + address);
                addressHashValueMap.get(address).add(hash);
            }


            if (!addressCoinsMap.containsKey(sendToAddress)) {

                ArrayList<String> coinsList = new ArrayList<>();
                coinsList.add(coins);
                addressCoinsMap.put(sendToAddress, coinsList);
            } else {

                AndroidAppUtils.showLog(TAG, "addressCoinsMap already contains address: " + sendToAddress);
                addressCoinsMap.get(sendToAddress).add(coins);

            }


//            if (!addressHoursMap.containsKey(address)) {
//
//                ArrayList<String> hoursList = new ArrayList<>();
//                hoursList.add(String.valueOf(hours));
//                addressHoursMap.put(address, hoursList);
//
//            } else {
//
//                AndroidAppUtils.showLog(TAG, "addressHoursMap already contains address: " + address);
//                addressHoursMap.get(address).add(String.valueOf(hours));
//
//            }

            totalCalculatedHours += hours;


            current_coins = roundTwoDecimals(current_coins + apiCoins);
            remaining_coins_to_Send = roundTwoDecimals(total_coins_to_send - current_coins);
            isCoinsComplete = true;


        } else if (apiCoins > remaining_coins_to_Send) {

            AndroidAppUtils.showLog(TAG, "api coin is greater then remaining coins");

            if (!walletAddressesList.contains(address))
                walletAddressesList.add(address);
            else
                AndroidAppUtils.showErrorLog(TAG, "address list already contains address: " + address);

            // Add hash value to map
            if (!addressHashValueMap.containsKey(address)) {

                ArrayList<String> hashList = new ArrayList<>();
                hashList.add(hash);
                addressHashValueMap.put(address, hashList);
            } else {
                AndroidAppUtils.showLog(TAG, "hashmap already contains address: " + address);
                addressHashValueMap.get(address).add(hash);
            }


            if (!addressCoinsMap.containsKey(sendToAddress)) {

                ArrayList<String> coinsList = new ArrayList<>();
                coinsList.add(String.valueOf(remaining_coins_to_Send));
                addressCoinsMap.put(sendToAddress, coinsList);
            } else {
                AndroidAppUtils.showLog(TAG, "addressCoinsMap already contains address: " + address);
                addressCoinsMap.get(sendToAddress).add(String.valueOf(remaining_coins_to_Send));
            }


            if (!addressCoinsMap.containsKey(address)) {

                ArrayList<String> coinsList = new ArrayList<>();
                Double remaining_coins_in_addr = roundTwoDecimals(apiCoins - remaining_coins_to_Send);
                coinsList.add(String.valueOf(remaining_coins_in_addr));
                addressCoinsMap.put(address, coinsList);

            } else {
                AndroidAppUtils.showLog(TAG, "addressCoinsMap already contains address: " + address);
                addressCoinsMap.get(address).add(coins);
            }

          /*  if (!addressHoursMap.containsKey(address)) {

                ArrayList<String> hoursList = new ArrayList<>();
                hoursList.add(String.valueOf(hours));
                addressHoursMap.put(address, hoursList);

            } else {

                AndroidAppUtils.showLog(TAG, "addressHoursMap already contains address: " + address);
                addressHoursMap.get(address).add(String.valueOf(hours));

            }*/

            totalCalculatedHours += hours;

            current_coins = roundTwoDecimals(current_coins + remaining_coins_to_Send);
            remaining_coins_to_Send = roundTwoDecimals(total_coins_to_send - current_coins);
            isCoinsComplete = true;

        } else {

            AndroidAppUtils.showLog(TAG, "api coin is less than remaining coins");

            if (!walletAddressesList.contains(address))
                walletAddressesList.add(address);
            else
                AndroidAppUtils.showErrorLog(TAG, "address list already contains address: " + address);

            // Add hash value to map
            if (!addressHashValueMap.containsKey(address)) {

                ArrayList<String> hashList = new ArrayList<>();
                hashList.add(hash);
                addressHashValueMap.put(address, hashList);
            } else {
                AndroidAppUtils.showLog(TAG, "hashmap already contains address: " + address);
                addressHashValueMap.get(address).add(hash);
            }

            if (!addressCoinsMap.containsKey(sendToAddress)) {

                ArrayList<String> coinsList = new ArrayList<>();
                coinsList.add(coins);
                addressCoinsMap.put(sendToAddress, coinsList);
            } else {
                AndroidAppUtils.showLog(TAG, "addressCoinsMap already contains address: " + address);
                addressCoinsMap.get(sendToAddress).add(coins);
            }

           /* if (!addressHoursMap.containsKey(address)) {

                ArrayList<String> hoursList = new ArrayList<>();
                hoursList.add(String.valueOf(hours));
                addressHoursMap.put(address, hoursList);

            } else {

                AndroidAppUtils.showLog(TAG, "addressHoursMap already contains address: " + address);
                addressHoursMap.get(address).add(String.valueOf(hours));

            }*/

            totalCalculatedHours += hours;

            current_coins = roundTwoDecimals(current_coins + apiCoins);
            remaining_coins_to_Send = roundTwoDecimals(total_coins_to_send - current_coins);
            isCoinsComplete = false;

        }

        return isCoinsComplete;


    }


    double roundTwoDecimals(double d) {
        DecimalFormat twoDForm;
        if (GlobalConfig.IS_KOREAN_LANGUAGE) {
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.KOREAN);
            twoDForm = (DecimalFormat) nf;
        } else
            twoDForm = new DecimalFormat("#" + GlobalConfig.DECIMAL_FORMAT + "###");

        return Double.valueOf(twoDForm.format(d));
    }


    /**
     * Show Alert Dialog
     */
    private void showSendSuccessDialog(String txid) {

        if (mActivity != null) {

            sendSuccessDialog = new Dialog(this);
            sendSuccessDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            sendSuccessDialog.setContentView(R.layout.send_success_dialog);

            WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();

            lWindowParams.copyFrom(sendSuccessDialog.getWindow().getAttributes());
            lWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            success_msg = sendSuccessDialog.findViewById(R.id.success_msg);
            send_ok_button = sendSuccessDialog.findViewById(R.id.send_ok_button);
            copy_txid_btn = sendSuccessDialog.findViewById(R.id.copy_txid_btn);

            success_msg.setText(txid);
            send_ok_button.setOnClickListener(this);
            copy_txid_btn.setOnClickListener(this);

            sendSuccessDialog.show();
            sendSuccessDialog.getWindow().setAttributes(lWindowParams);

        } else
            AndroidAppUtils.showErrorLog(TAG, "mActivity is null");


    }


    /**
     * Get wallet addresses
     *
     * @param addressesJson
     * @return
     */
    private String getWalletAddresses(String addressesJson) {

        String walletAddresses = "";
        String address = "";

        if (addressesJson != null && !addressesJson.isEmpty()) {

            try {

                JSONArray jsonArray = new JSONArray(addressesJson);


                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject walletAddressJson = jsonArray.getJSONObject(i);

                    if (walletAddressJson.has(ADDRESS_KEY)) {

                        address = walletAddressJson.getString(ADDRESS_KEY);
                        walletAddresses = walletAddresses + address + ",";

                        if (walletAddressJson.has(KEY_SECRET)) {

                            String secret = walletAddressJson.getString(KEY_SECRET);
                            walletAddressSecretMap.put(address, secret);

                        } else
                            AndroidAppUtils.showErrorLog(TAG, "JSON doesnot have secret key");

                    } else
                        AndroidAppUtils.showErrorLog(TAG, "JSON doesnot have address key");

                }

                AndroidAppUtils.showLog(TAG, "Addresses for  wallet is: " + walletAddresses);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else
            AndroidAppUtils.showErrorLog(TAG, "addressesJson is null or empty");

        return walletAddresses;

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (WalletContainerActivity.walletDataModelList != null) {

            AndroidAppUtils.showLog(TAG, "wallet selected: " + WalletContainerActivity.walletDataModelList.get(i).getWallet_name());
            walletId = i;
            wallet_coins = WalletContainerActivity.walletDataModelList.get(walletId).getWallet_balance();


        } else
            AndroidAppUtils.showErrorLog(TAG, "walletDataModelList is null");
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}
