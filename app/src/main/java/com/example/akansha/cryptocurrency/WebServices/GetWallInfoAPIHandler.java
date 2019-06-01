package com.example.akansha.cryptocurrency.WebServices;

import android.app.Activity;
import android.app.Dialog;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.example.akansha.cryptocurrency.Application.SolarBankerApplication;
import com.example.akansha.cryptocurrency.Constants.GlobalConstants;
import com.example.akansha.cryptocurrency.Control.WebserviceAPIErrorHandler;
import com.example.akansha.cryptocurrency.Model.WalletDataModel;
import com.example.akansha.cryptocurrency.Preferences.SolarBankerPreferenceManager;
import com.example.akansha.cryptocurrency.R;
import com.example.akansha.cryptocurrency.Utils.AndroidAppUtils;
import com.example.akansha.cryptocurrency.Utils.GlobalConfig;
import com.example.akansha.cryptocurrency.View.WalletContainerActivity;
import com.example.akansha.cryptocurrency.iHelper.WebAPIResponseListener;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * API to GET Vestions from cloud.
 *
 * @author Anshuman
 */
public class GetWallInfoAPIHandler {
    /**
     * Instance object Activity
     */
    private Activity mActivity;
    /**
     * Debug TAG
     */
    private String TAG = GetWallInfoAPIHandler.class.getSimpleName();
    /**
     * API Response Listener
     */
    private String address = "", walletName = "";
    private double walletCoins;
    private int walletHours;
    private int numberOfWalletsToLoad;
    private boolean isNewWalletCreated;

    private TextView wallet_balance_num, wallet_balance_fiat, wallet_balance_hours;

    private String seedValue;
    private int numberOfAddresss;
    private String walletId;
    private WebAPIResponseListener webAPIResponseListener;


    /**
     * @param mActivity
     * @param seedValue
     * @param numberOfAddresses
     * @param walletId
     * @param webAPIResponseListener
     */
    public GetWallInfoAPIHandler(Activity mActivity, String address, String walletName, int numberOfWalletsToLoad, boolean isNewWalletCreated, String seedValue, int numberOfAddresses, String walletId, WebAPIResponseListener webAPIResponseListener) {

        this.mActivity = mActivity;
        this.address = address;
        this.walletName = walletName;
        this.numberOfWalletsToLoad = numberOfWalletsToLoad;
        this.isNewWalletCreated = isNewWalletCreated;
        this.seedValue = seedValue;
        this.numberOfAddresss = numberOfAddresses;
        this.walletId = walletId;
        this.webAPIResponseListener = webAPIResponseListener;

        initViews();
        postAPICallString();
    }

    private void initViews() {

        wallet_balance_num = mActivity.findViewById(R.id.wallet_balance_num);
        wallet_balance_fiat = mActivity.findViewById(R.id.wallet_balance_fiat);
        wallet_balance_hours = mActivity.findViewById(R.id.wallet_balance_hours);

    }

    /**
     * Making String object request
     */
    private void postAPICallString() {

        String nodeUrl = "http://104.248.131.27:8120";

        if (SolarBankerApplication.solarBankerApplicationContext != null)
            nodeUrl = SolarBankerPreferenceManager.getInstance(SolarBankerApplication.solarBankerApplicationContext).getNodeUrl();
        else
            AndroidAppUtils.showErrorLog(TAG, "solarBankerApplicationContext is null");

        AndroidAppUtils.showLog(TAG, "Node url is: " + nodeUrl);

//        String URL = nodeUrl + "/api/v1/explorer/address?address=" + address;
//
//        AndroidAppUtils.showLog(TAG, "API: " + URL);


        String URL = nodeUrl + "/api/v1/balance?addrs=" + address;

        AndroidAppUtils.showLog(TAG, "API: " + URL);
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.GET, URL, null
                , new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                AndroidAppUtils.showInfoLog(TAG, "Version API Response :"
                        + response);

                parseAPIResponse(response);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                AndroidAppUtils.showErrorLog(TAG, error + "");
                WebserviceAPIErrorHandler.getInstance().VolleyErrorHandler(error, mActivity);

                if (webAPIResponseListener != null)
                    webAPIResponseListener.onFailResponse();
                else
                    AndroidAppUtils.showErrorLog(TAG, "webAPIResponseListener is null");

            }
        });
        // Adding request to request queue
        SolarBankerApplication.getInstance().addToRequestQueue(strReq, GetWallInfoAPIHandler.class.getSimpleName());

        // set request time-out
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                GlobalConstants.TIME_ONE_SECOND * 10,
                GlobalConstants.API_RETRY,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    /**
     * Parse API response
     *
     * @param response
     */
    private void parseAPIResponse(JSONObject response) {

        JSONObject mainConfirmedJson = null;
        long confirmedCoins = 0;
        int confirmedHours = 0;


        if (response != null) {
            try {

                if (response.has(GlobalConstants.KEY_CONFIRMED))
                    mainConfirmedJson = response.getJSONObject(GlobalConstants.KEY_CONFIRMED);
                else
                    AndroidAppUtils.showErrorLog(TAG, "response for address: " + address + " doesnot containn confirmed key");

                if (mainConfirmedJson != null) {

                    if (mainConfirmedJson.has(GlobalConstants.KEY_COINS))
                        confirmedCoins = mainConfirmedJson.getLong(GlobalConstants.KEY_COINS);
                    else
                        AndroidAppUtils.showErrorLog(TAG, "mainConfirmedJson do not have coins key");

                    if (mainConfirmedJson.has(GlobalConstants.KEY_HOURS))
                        confirmedHours = mainConfirmedJson.getInt(GlobalConstants.KEY_HOURS);
                    else
                        AndroidAppUtils.showErrorLog(TAG, "mainConfirmedJson do not have hours key");

                    walletCoins = roundTwoDecimals(confirmedCoins / 1000000.0);
                    walletHours = confirmedHours;


                } else
                    AndroidAppUtils.showErrorLog(TAG, "mainConfirmedJson is null");


                addWalletToList();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else
            AndroidAppUtils.showErrorLog(TAG, "response is null for addres: " + address);

    }

    /**
     * Add wallet to  list
     */
    private void addWalletToList() {

        if (WalletContainerActivity.walletDataModelList != null && WalletContainerActivity.mAdapter != null) {


            if (walletName.isEmpty()) {
                AndroidAppUtils.showErrorLog(TAG, "walletName is empty");
                walletName = "unNamedWallet";
            }

            WalletDataModel walletDataModel = new WalletDataModel(walletName, walletHours, walletCoins);
            walletDataModel.setSeedValue(seedValue);
            walletDataModel.setNumberOfAddress(numberOfAddresss);
            walletDataModel.setWalletId(walletId);
            WalletContainerActivity.walletDataModelList.add(walletDataModel);
            WalletContainerActivity.mAdapter.notifyDataSetChanged();

            if (WalletContainerActivity.walletDataModelList.size() == numberOfWalletsToLoad)
                WalletContainerActivity.hideLoadingDialog();

            setCompleteWalletInfo();


        }

    }

    /**
     * Set Complete wallet info
     */
    private void setCompleteWalletInfo() {

        if (wallet_balance_num != null && wallet_balance_fiat != null && mActivity != null) {

            Double currentWalletBalance = Double.valueOf(wallet_balance_num.getText().toString());

            DecimalFormat twoDForm;


            if (GlobalConfig.IS_KOREAN_LANGUAGE) {
                NumberFormat nf = NumberFormat.getNumberInstance(Locale.KOREAN);
                twoDForm = (DecimalFormat) nf;
            } else
                twoDForm = new DecimalFormat("#" + GlobalConfig.DECIMAL_FORMAT + "###");

            final double newWalletBalance = Double.valueOf(twoDForm.format(currentWalletBalance + walletCoins));

            int currentWalletHurs = Integer.valueOf(wallet_balance_hours.getText().toString());

            AndroidAppUtils.showLog(TAG, "current wallet coins: " + currentWalletBalance);
            AndroidAppUtils.showLog(TAG, "wallet coins: " + walletCoins);
            AndroidAppUtils.showLog(TAG, "new wallet coins: " + newWalletBalance);

            final int newWalletHours = currentWalletHurs + walletHours;

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    wallet_balance_num.setText(String.valueOf(newWalletBalance));
                    wallet_balance_hours.setText(String.valueOf(newWalletHours));
                }
            });


        } else
            AndroidAppUtils.showErrorLog(TAG, "wallet_balance_num or wallet_balance_fiat is null");

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


}
