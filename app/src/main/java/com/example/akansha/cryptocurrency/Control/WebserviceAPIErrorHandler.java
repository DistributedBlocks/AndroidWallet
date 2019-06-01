package com.example.akansha.cryptocurrency.Control;

import android.app.Activity;

import com.android.volley.error.AuthFailureError;
import com.android.volley.error.NetworkError;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.ParseError;
import com.android.volley.error.ServerError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.example.akansha.cryptocurrency.R;
import com.example.akansha.cryptocurrency.Utils.AndroidAppUtils;

/**
 * Webservice API Error Handler
 *
 * @author Shruti
 */
public class WebserviceAPIErrorHandler {
    /**
     * Instance of This class
     */
    public static WebserviceAPIErrorHandler mErrorHandler;
    /**
     * Debugging TAG
     */
    private String TAG = WebserviceAPIErrorHandler.class.getSimpleName();

    private WebserviceAPIErrorHandler() {
    }

    /**
     * Get Instance of this class
     *
     * @return
     */
    public static WebserviceAPIErrorHandler getInstance() {
        if (mErrorHandler == null)
            mErrorHandler = new WebserviceAPIErrorHandler();
        return mErrorHandler;

    }

    /**
     * Volley Error Handler
     *
     * @param mError
     */
    public void VolleyErrorHandler(VolleyError mError, Activity mActivity) {
        AndroidAppUtils.showErrorLog(TAG, "VolleyError :" + mError);
        if (mError instanceof NoConnectionError) {
            AndroidAppUtils.showErrorLog(TAG, mActivity.getResources()
                    .getString(R.string.network_error));
        } else if (mError instanceof TimeoutError) {
            AndroidAppUtils.showErrorLog(TAG, mActivity.getResources()
                    .getString(R.string.network_slow_error));
            // TODO
        } else if (mError instanceof AuthFailureError) {
            // TODO
        } else if (mError instanceof ServerError) {
            AndroidAppUtils.showErrorLog(TAG, mActivity.getResources()
                    .getString(R.string.server_error));
            // TODO
        } else if (mError instanceof NetworkError) {
            AndroidAppUtils.showErrorLog(TAG, mActivity.getResources()
                    .getString(R.string.network_error));
            // TODO
        } else if (mError instanceof ParseError) {
            // TODO
        }
    }


}
