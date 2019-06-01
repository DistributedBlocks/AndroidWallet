package com.example.akansha.cryptocurrency.Application;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.akansha.cryptocurrency.Utils.AndroidAppUtils;
import com.example.akansha.cryptocurrency.Utils.GlobalConfig;

import java.util.Locale;

public class SolarBankerApplication extends Application {

    public static SolarBankerApplication solarBankerApplicationContext;
    private String TAG = SolarBankerApplication.class.getSimpleName();
    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();


        solarBankerApplicationContext = this;

        getCurrentLanguageOfDevice();

        if (Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
        }

    }

    public static SolarBankerApplication getInstance() {
        return solarBankerApplicationContext;
    }


    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());//,new HurlStack(null, ClientSSLSocketFactory.getSocketFactory()));
        } else
            mRequestQueue.getCache().clear();

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        req.setShouldCache(false);
        getRequestQueue().getCache().clear();
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void getCurrentLanguageOfDevice() {

        GlobalConfig.currentDeviceLanguage = Locale.getDefault().getDisplayLanguage();
        AndroidAppUtils.showLog(TAG, "Current language: " + GlobalConfig.currentDeviceLanguage);
        if (GlobalConfig.currentDeviceLanguage.contains(GlobalConfig.azərbaycan_language))
            GlobalConfig.IS_KOREAN_LANGUAGE = true;
        else
            GlobalConfig.IS_KOREAN_LANGUAGE = false;
    }

    public static class Config {
        public static final boolean DEVELOPER_MODE = false;
    }
}
