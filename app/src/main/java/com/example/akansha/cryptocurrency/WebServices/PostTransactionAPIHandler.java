package com.example.akansha.cryptocurrency.WebServices;

import android.app.Activity;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.HttpHeaderParser;
import com.example.akansha.cryptocurrency.Application.SolarBankerApplication;
import com.example.akansha.cryptocurrency.Constants.GlobalConstants;
import com.example.akansha.cryptocurrency.Preferences.SolarBankerPreferenceManager;
import com.example.akansha.cryptocurrency.Utils.AndroidAppUtils;
import com.example.akansha.cryptocurrency.iHelper.WebAPIResponseListener;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * API to GET Vestions from cloud.
 *
 * @author Anshuman
 */
public class PostTransactionAPIHandler {
    /**
     * Instance object Activity
     */
    private Activity mActivity;
    /**
     * Debug TAG
     */
    private String TAG = PostTransactionAPIHandler.class.getSimpleName();
    /**
     * API Response Listener
     */
    private WebAPIResponseListener mResponseListener;


    /**
     * @param mActivity
     * @param webAPIResponseListener
     */
    public PostTransactionAPIHandler(Activity mActivity, WebAPIResponseListener webAPIResponseListener) {

        this.mActivity = mActivity;
        this.mResponseListener = webAPIResponseListener;

    }

    public void postData(final JSONObject transactionData) {


        String nodeUrl = "http://104.248.131.27:8120";

        if (SolarBankerApplication.solarBankerApplicationContext != null)
            nodeUrl = SolarBankerPreferenceManager.getInstance(SolarBankerApplication.solarBankerApplicationContext).getNodeUrl();
        else
            AndroidAppUtils.showErrorLog(TAG, "solarBankerApplicationContext is null");

        AndroidAppUtils.showLog(TAG, "Node url is: " + nodeUrl);

//        String URL = nodeUrl + "/api/v1/explorer/address?address=" + address;
//
//        AndroidAppUtils.showLog(TAG, "API: " + URL);


        String url = nodeUrl + "/api/v1/injectTransaction";
        AndroidAppUtils.showLog(TAG, "API: " + url);

        if (transactionData != null) {
            final String requestBody = transactionData.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);

                   /* if (response.contains("200")) {
                        if (mResponseListener != null)
                            mResponseListener.onSuccessResponse(response);
                        else
                            AndroidAppUtils.showErrorLog(TAG, "mResponseListener is null");
                    } else {

                        if (mResponseListener != null)
                            mResponseListener.onFailResponse();
                        else
                            AndroidAppUtils.showErrorLog(TAG, "mResponseListener is null");

                    }*/

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());

                    if (mResponseListener != null)
                        mResponseListener.onFailResponse();
                    else
                        AndroidAppUtils.showErrorLog(TAG, "mResponseListener is null");

                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);

                        AndroidAppUtils.showLog(TAG, "Network string: " + responseString);
                        if (responseString.contains("200")) {

                            String responseData = new String(response.data);
                            AndroidAppUtils.showLog(TAG, "response data: " + responseData);
                            if (mResponseListener != null)
                                mResponseListener.onSuccessResponse(responseData);
                            else
                                AndroidAppUtils.showErrorLog(TAG, "mResponseListener is null");
                        } else {

                            if (mResponseListener != null)
                                mResponseListener.onFailResponse();
                            else
                                AndroidAppUtils.showErrorLog(TAG, "mResponseListener is null");

                        }

                        // can get more details such as response.headers
                    } else
                        AndroidAppUtils.showErrorLog(TAG, "response is null");
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };


            // Adding request to request queue
            SolarBankerApplication.getInstance().addToRequestQueue(stringRequest, GetWallInfoAPIHandler.class.getSimpleName());

            // set request time-out
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    GlobalConstants.TIME_ONE_SECOND * 10,
                    GlobalConstants.API_RETRY,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        } else
            AndroidAppUtils.showErrorLog(TAG, "transactionData is null");
    }

    private void formDataToSend(Map<String, String> params, JSONObject transactionData) {

        String KEY_RAWTX = "rawtx";

        try {

            if (transactionData != null && params != null) {

                if (transactionData.has(KEY_RAWTX))
                    params.put(KEY_RAWTX, transactionData.getString(KEY_RAWTX));
                else
                    AndroidAppUtils.showErrorLog(TAG, "transactionData doesnot  contain KEY_RAWTX");

                AndroidAppUtils.showLog(TAG, "transaction data: " + params.toString());

            } else
                AndroidAppUtils.showErrorLog(TAG, "transactionData json is null or params is null");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
