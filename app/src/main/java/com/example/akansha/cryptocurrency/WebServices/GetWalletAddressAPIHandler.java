package com.example.akansha.cryptocurrency.WebServices;

import android.app.Activity;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.example.akansha.cryptocurrency.Application.SolarBankerApplication;
import com.example.akansha.cryptocurrency.Constants.GlobalConstants;
import com.example.akansha.cryptocurrency.Control.WebserviceAPIErrorHandler;
import com.example.akansha.cryptocurrency.Model.WalletInfoDataModel;
import com.example.akansha.cryptocurrency.Preferences.SolarBankerPreferenceManager;
import com.example.akansha.cryptocurrency.R;
import com.example.akansha.cryptocurrency.Utils.AndroidAppUtils;
import com.example.akansha.cryptocurrency.Utils.GlobalConfig;
import com.example.akansha.cryptocurrency.View.AddressContainerActivity;
import com.example.akansha.cryptocurrency.View.WalletContainerActivity;
import com.example.akansha.cryptocurrency.iHelper.WebAPIResponseListener;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * API to GET Vestions from cloud.
 */
public class GetWalletAddressAPIHandler {
    /**
     * Instance object Activity
     */
    private Activity mActivity;
    /**
     * Debug TAG
     */
    private String TAG = GetWalletAddressAPIHandler.class.getSimpleName();
    /**
     * API Response Listener
     */
    private String address = "";
    private double walletCoins;
    private int walletHours;
    private int numberOfWalletsToLoad;
    private boolean isNewAddressCreated;

    private TextView wallet_balance_num, wallet_balance_fiat, wallet_balance_hours;
    private ArrayList<String> walletAddressArrayList;

    private double addressCoins;
    private int addressHours;
    private WebAPIResponseListener webAPIResponseListener;

    /**
     * @param mActivity
     * @param webAPIResponseListener
     */
    public GetWalletAddressAPIHandler(Activity mActivity, String address, int numberOfWalletsToLoad, boolean isNewAddressCreated, ArrayList<String> walletAddressArrayList, WebAPIResponseListener webAPIResponseListener) {

        this.mActivity = mActivity;
        this.address = address;
        this.numberOfWalletsToLoad = numberOfWalletsToLoad;
        this.isNewAddressCreated = isNewAddressCreated;

        this.walletAddressArrayList = walletAddressArrayList;
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
                AndroidAppUtils.showErrorLog(TAG, "error: " + error + "");


                WebserviceAPIErrorHandler.getInstance().VolleyErrorHandler(error, mActivity);
                if (webAPIResponseListener != null)
                    webAPIResponseListener.onFailResponse();
                else
                    AndroidAppUtils.showErrorLog(TAG, "webAPIResponseListener  is null");

            }
        });
        // Adding request to request queue
        SolarBankerApplication.getInstance().addToRequestQueue(strReq, GetWalletAddressAPIHandler.class.getSimpleName());

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
        JSONObject addressConfirmedJson = null;
        JSONObject addressesJsonObject = null;
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

//                    walletCoins = confirmedCoins / 1000000.0;
//                    walletHours = confirmedHours;


                } else
                    AndroidAppUtils.showErrorLog(TAG, "mainConfirmedJson is null");


                if (response.has(GlobalConstants.KEY_ADDRESSES)) {
                    addressesJsonObject = response.getJSONObject(GlobalConstants.KEY_ADDRESSES);

                    AndroidAppUtils.showLog(TAG, "walletAddressArrayList size: " + walletAddressArrayList.size());

                    for (String addressFromJson : walletAddressArrayList) {

                        AndroidAppUtils.showLog(TAG, "Address from arrayList: " + addressFromJson);

                        if (addressesJsonObject.has(addressFromJson)) {
                            JSONObject addressInfoJsonObject = addressesJsonObject.getJSONObject(addressFromJson);

                            if (addressInfoJsonObject.has(GlobalConstants.KEY_CONFIRMED))
                                addressConfirmedJson = addressInfoJsonObject.getJSONObject(GlobalConstants.KEY_CONFIRMED);
                            else
                                AndroidAppUtils.showErrorLog(TAG, "response for address: " + addressFromJson + " doesnot containn confirmed key");

                            if (addressConfirmedJson != null) {

                                if (addressConfirmedJson.has(GlobalConstants.KEY_COINS))
                                    addressCoins = addressConfirmedJson.getLong(GlobalConstants.KEY_COINS);
                                else
                                    AndroidAppUtils.showErrorLog(TAG, "addressConfirmedJson do not have coins key");

                                if (addressConfirmedJson.has(GlobalConstants.KEY_HOURS))
                                    addressHours = addressConfirmedJson.getInt(GlobalConstants.KEY_HOURS);
                                else
                                    AndroidAppUtils.showErrorLog(TAG, "addressConfirmedJson do not have hours key");

                                addressCoins = addressCoins / 1000000.0;

                                AndroidAppUtils.showLog(TAG, "address " + addressFromJson + "coins: " + addressCoins + " Hours: " + addressHours);
                                addAddressToList(addressFromJson);

                            } else
                                AndroidAppUtils.showErrorLog(TAG, "addressConfirmedJson is null");

                        }

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else
            AndroidAppUtils.showErrorLog(TAG, "response is null for addres: " + address);

    }

    /**
     * Add wallet to  list
     */
    private void addAddressToList(String address) {

        if (WalletContainerActivity.walletDataModelList != null && WalletContainerActivity.mAdapter != null) {


            WalletInfoDataModel walletInfoDataModel = new WalletInfoDataModel();
            walletInfoDataModel.setAddress(address);
            walletInfoDataModel.setHours(addressHours);
            walletInfoDataModel.setBalance(addressCoins);

            AddressContainerActivity.walletInfoDataModelList.add(walletInfoDataModel);
            AddressContainerActivity.mAdapter.notifyDataSetChanged();

            if (AddressContainerActivity.walletInfoDataModelList.size() == numberOfWalletsToLoad)
                AddressContainerActivity.hideLoadingDialog();
            else
                AndroidAppUtils.showErrorLog(TAG, "not loaded total addresses: " + numberOfWalletsToLoad);

            setCompleteWalletInfo();


        }

    }

    /**
     * Set Complete wallet info
     */
    private void setCompleteWalletInfo() {

        if (wallet_balance_num != null && wallet_balance_fiat != null && mActivity != null) {

//            if (isNewAddressCreated) {
//                walletCoins += addressCoins;

            AndroidAppUtils.showLog(TAG, "Wallet coins: " + walletCoins + " address coins: " + addressCoins);
            walletCoins = roundTwoDecimals(walletCoins + addressCoins);
            walletHours = walletHours + addressHours;
//            } else
//                AndroidAppUtils.showLog(TAG, "Showing already created addresses");

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    wallet_balance_num.setText(String.valueOf(walletCoins));
                    wallet_balance_hours.setText(String.valueOf(walletHours));
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
