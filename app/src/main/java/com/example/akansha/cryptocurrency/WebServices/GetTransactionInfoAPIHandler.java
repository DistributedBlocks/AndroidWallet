package com.example.akansha.cryptocurrency.WebServices;

import android.app.Activity;
import android.text.format.DateFormat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonArrayRequest;
import com.example.akansha.cryptocurrency.Application.SolarBankerApplication;
import com.example.akansha.cryptocurrency.Constants.GlobalConstants;
import com.example.akansha.cryptocurrency.Model.TransactionHistoryDataModel;
import com.example.akansha.cryptocurrency.Preferences.SolarBankerPreferenceManager;
import com.example.akansha.cryptocurrency.Utils.AndroidAppUtils;
import com.example.akansha.cryptocurrency.View.TransactionHistoryActivity;
import com.example.akansha.cryptocurrency.iHelper.WebAPIResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

/**
 * API to GET Vestions from cloud.
 *
 * @author Anshuman
 */
public class GetTransactionInfoAPIHandler {
    /**
     * Instance object Activity
     */
    private Activity mActivity;
    /**
     * Debug TAG
     */
    private String TAG = GetTransactionInfoAPIHandler.class.getSimpleName();
    /**
     * API Response Listener
     */
    private String address = "";

    private String KEY_STATUS = "status";
    private String KEY_CONFIRMED = "confirmed";
    private String KEY_TIMESTAMP = "timestamp";
    private String KEY_INPUTS = "inputs";
    private String KEY_OUTPUTS = "outputs";
    private String KEY_COIN = "coins";
    private String KEY_OWNER = "owner";
    private String KEY_DST = "dst";

    private boolean status;
    private long timestamp;
    private String transaction_type = "";
    private String date = "";
    private String date_and_time = "";
    private String to_address = "";
    private String from_address = "";
    private double transaction_coins = 0.0;
    private String sent_HHC = "Sent HHC";
    private String reveived_HHC = "Received HHC";
    private String from_address_for_received_txn = "";
    private String walletName = "";
    private WebAPIResponseListener webAPIResponseListener;
    private int numberOfAddressesToLoad;

    /**
     * @param mActivity
     * @param webAPIResponseListener
     */
    public GetTransactionInfoAPIHandler(Activity mActivity, String address, String walletName, int numerOfAddressesToLoad, WebAPIResponseListener webAPIResponseListener) {

        this.mActivity = mActivity;
        this.address = address;
        this.walletName = walletName;
        this.webAPIResponseListener = webAPIResponseListener;
        this.numberOfAddressesToLoad = numerOfAddressesToLoad;
        postAPICallString();
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

        String URL = nodeUrl + "/api/v1/explorer/address?address=" + address;

        AndroidAppUtils.showLog(TAG, "API: " + URL);


        JsonArrayRequest strReq = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Do something with response

                        AndroidAppUtils.showLog(TAG, "response:  " + response);
                        parseAPIResponse(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something when error occurred
                        AndroidAppUtils.showErrorLog(TAG, "error: " + error);
                        if (webAPIResponseListener != null)
                            webAPIResponseListener.onFailResponse();
                        else
                            AndroidAppUtils.showErrorLog(TAG, "webAPIResponseListener is null");
                    }
                }
        ) {
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };


        // Adding request to request queue
        SolarBankerApplication.getInstance().addToRequestQueue(strReq, GetTransactionInfoAPIHandler.class.getSimpleName());

        // set request time-out
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                GlobalConstants.TIME_ONE_SECOND * 10,
                GlobalConstants.API_RETRY,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    /**
     * Parse received Json array
     *
     * @param response
     */
    private void parseAPIResponse(JSONArray response) {

        if (response != null && response.length() > 0) {


            String owner = "";

            for (int i = 0; i < response.length(); i++) {
                try {

                    JSONObject transactionData = response.getJSONObject(i);

                    if (transactionData.has(KEY_STATUS)) {

                        JSONObject statusJson = transactionData.getJSONObject(KEY_STATUS);

                        if (statusJson.has(KEY_CONFIRMED))
                            status = statusJson.getBoolean(KEY_CONFIRMED);
                        else
                            AndroidAppUtils.showErrorLog(TAG, "status json donot  have confirmedkey");

                    } else
                        AndroidAppUtils.showErrorLog(TAG, "transactionData donot have status key");

                    if (transactionData.has(KEY_TIMESTAMP)) {
                        timestamp = transactionData.getLong(KEY_TIMESTAMP);
                    } else
                        AndroidAppUtils.showErrorLog(TAG, "transactionData donot have KEY_TIMESTAMP");

                    if (transactionData.has(KEY_INPUTS)) {

                        JSONArray jsonArray = transactionData.getJSONArray(KEY_INPUTS);
                        boolean isRequestedAddressFound = false;

                        if (jsonArray != null && jsonArray.length() > 0) {


                            JSONObject inputJsonObjectForReceivedTxn = jsonArray.getJSONObject(0);

                            if (inputJsonObjectForReceivedTxn != null && inputJsonObjectForReceivedTxn.has(KEY_OWNER))
                                from_address_for_received_txn = inputJsonObjectForReceivedTxn.getString(KEY_OWNER);
                            else
                                AndroidAppUtils.showErrorLog(TAG, "inputJsonObjectForReceivedTxn is null or do not have owner key");


                            for (int i1 = 0; i1 < jsonArray.length(); i1++) {

                                JSONObject inputJsonObject = jsonArray.getJSONObject(i1);

                                if (inputJsonObject != null) {

                                    if (inputJsonObject.has(KEY_OWNER)) {
                                        owner = inputJsonObject.getString(KEY_OWNER);

                                        if (owner.equals(address)) {
                                            isRequestedAddressFound = true;
                                            break;
                                        } else
                                            AndroidAppUtils.showLog(TAG, "requested address not found in input json");
                                        //                                            transaction_type = sent_HHC;
//                                        else
//                                            transaction_type = reveived_HHC;
                                        break;
                                    }


                                } else
                                    AndroidAppUtils.showErrorLog(TAG, "inputJson is null");
                            }
                            if (isRequestedAddressFound)
                                transaction_type = sent_HHC;
                            else
                                transaction_type = reveived_HHC;
                        } else
                            AndroidAppUtils.showErrorLog(TAG, "jsonarray is null or empty");
                    } else
                        AndroidAppUtils.showErrorLog(TAG, "KEY_INPUTS not available or inputs array length is zero");


                    if (transactionData.has(KEY_OUTPUTS)) {
                        JSONArray jsonArray = transactionData.getJSONArray(KEY_OUTPUTS);
                        if (jsonArray != null && jsonArray.length() > 0) {

                            if (transaction_type.equals(sent_HHC)) {

                                from_address = address;

                                for (int j = 0; j < jsonArray.length(); j++) {

                                    JSONObject outputJsonObject = jsonArray.getJSONObject(j);

                                    if (outputJsonObject.has(KEY_DST)) {
                                        String dst = outputJsonObject.getString(KEY_DST);
                                        if (!dst.equals(address)) {

                                            to_address = dst;

                                            if (outputJsonObject.has(KEY_COIN)) {
                                                transaction_coins = outputJsonObject.getDouble(KEY_COIN);
                                            } else
                                                AndroidAppUtils.showErrorLog(TAG, "outputJsonObject donot has KEY_COIN");
                                            break;
                                        }
                                    } else
                                        AndroidAppUtils.showErrorLog(TAG, "outputJsonObject donot has dst key");

                                }
                            } else {

                                to_address = address;
                                from_address = from_address_for_received_txn;

                                for (int k = 0; k < jsonArray.length(); k++) {

                                    JSONObject outputJsonObject = jsonArray.getJSONObject(k);

                                    if (outputJsonObject.has(KEY_DST)) {
                                        String dst = outputJsonObject.getString(KEY_DST);
                                        if (dst.equals(address)) {


                                            if (outputJsonObject.has(KEY_COIN)) {
                                                transaction_coins = outputJsonObject.getDouble(KEY_COIN);
                                            } else
                                                AndroidAppUtils.showErrorLog(TAG, "outputJsonObject donot has KEY_COIN");
                                            break;
                                        }
                                    } else
                                        AndroidAppUtils.showErrorLog(TAG, "outputJsonObject donot has dst key");


                                }
                            }


                        } else
                            AndroidAppUtils.showErrorLog(TAG, "jsonArray is null or empty");
                    } else
                        AndroidAppUtils.showErrorLog(TAG, "KEY_OUTPUTS is not available");

                    setDataInTransactionList();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        } else {
            AndroidAppUtils.showErrorLog(TAG, "response is null or lenth is zero");
            TransactionHistoryActivity.hideLoadingDialog();
        }
    }

    private void setDataInTransactionList() {

        if (TransactionHistoryActivity.transactionHistoryDataModelList != null) {
            TransactionHistoryDataModel transactionHistoryDataModel = new TransactionHistoryDataModel();

            transactionHistoryDataModel.setTransaction_status(status);
            transactionHistoryDataModel.setTo_address(to_address);
            transactionHistoryDataModel.setFrom_address(from_address);
            transactionHistoryDataModel.setTimestamp(timestamp);
            transactionHistoryDataModel.setTransaction_coins(transaction_coins);
            transactionHistoryDataModel.setTransaction_type(transaction_type);
            transactionHistoryDataModel.setDate(getTransactionDate());
            transactionHistoryDataModel.setWallet_name(walletName);

            if (transaction_type.equals(sent_HHC))
                transactionHistoryDataModel.setAddress(to_address);
            else
                transactionHistoryDataModel.setAddress(from_address);

            transactionHistoryDataModel.setDate_and_time(getTransactionDateAndTime());

            TransactionHistoryActivity.transactionHistoryDataModelList.add(transactionHistoryDataModel);
            TransactionHistoryActivity.mAdapter.notifyDataSetChanged();

            if (TransactionHistoryActivity.transactionHistoryDataModelList.size() == numberOfAddressesToLoad)
                TransactionHistoryActivity.hideLoadingDialog();

        } else
            AndroidAppUtils.showErrorLog(TAG, "transactionHistoryDataModelList is null");


    }

    private String getTransactionDate() {

        AndroidAppUtils.showLog(TAG, "Timestamp: " + timestamp);
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp * 1000L);
//        return DateFormat.format("dd-MM-yyyy", cal).toString();
        return DateFormat.format("dd MMMM yyyy", cal).toString();


    }


    private String getTransactionDateAndTime() {

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp * 1000L);
        date_and_time = DateFormat.format("hh:mm:yy dd MMMM yyyy", cal).toString();
        AndroidAppUtils.showLog(TAG, "Date and time: " + date_and_time);
        return date_and_time;
    }

}
