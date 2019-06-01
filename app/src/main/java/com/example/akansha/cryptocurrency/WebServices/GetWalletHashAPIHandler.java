package com.example.akansha.cryptocurrency.WebServices;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.example.akansha.cryptocurrency.Application.SolarBankerApplication;
import com.example.akansha.cryptocurrency.Constants.GlobalConstants;
import com.example.akansha.cryptocurrency.Preferences.SolarBankerPreferenceManager;
import com.example.akansha.cryptocurrency.Utils.AndroidAppUtils;
import com.example.akansha.cryptocurrency.iHelper.WebAPIResponseListener;

import org.json.JSONObject;

/**
 * API to GET Vestions from cloud.
 *
 * @author Anshuman
 */
public class GetWalletHashAPIHandler {
    /**
     * Debug TAG
     */
    private String TAG = GetWalletHashAPIHandler.class.getSimpleName();
    /**
     * API Response Listener
     */
    private String address = "";


    private WebAPIResponseListener webAPIResponseListener;

    /**
     * @param webAPIResponseListener
     */
    public GetWalletHashAPIHandler(String address, WebAPIResponseListener webAPIResponseListener) {

        this.address = address;
        this.webAPIResponseListener = webAPIResponseListener;
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

//        String URL = nodeUrl + "/api/v1/explorer/address?address=" + address;
//
//        AndroidAppUtils.showLog(TAG, "API: " + URL);


        String URL = nodeUrl+"/api/v1/outputs?addrs=" + address;

        AndroidAppUtils.showLog(TAG, "API: " + URL);





        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.GET, URL, null
                , new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                AndroidAppUtils.showInfoLog(TAG, "Version API Response :"
                        + response);
                if (webAPIResponseListener != null)
                    webAPIResponseListener.onSuccessResponse(response);
                else
                    AndroidAppUtils.showErrorLog(TAG, "webAPIResponseListener  is null");

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidAppUtils.showErrorLog(TAG, error + "");
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


}
