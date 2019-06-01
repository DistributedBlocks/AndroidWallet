package com.example.akansha.cryptocurrency.Control;

import android.support.v7.app.AppCompatActivity;

import com.example.akansha.cryptocurrency.Database.DatabaseHelper;
import com.example.akansha.cryptocurrency.Model.WalletAddressJSONDataModel;
import com.example.akansha.cryptocurrency.Model.WalletNameAndAddressDataModel;
import com.example.akansha.cryptocurrency.Utils.AndroidAppUtils;
import com.example.akansha.cryptocurrency.WebServices.GetWallInfoAPIHandler;
import com.example.akansha.cryptocurrency.WebServices.GetWalletAddressAPIHandler;
import com.example.akansha.cryptocurrency.iHelper.WebAPIResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mobile.Mobile;

public class WalletInfoAPIHandler {


    private String TAG = WalletInfoAPIHandler.class.getSimpleName();
    private DatabaseHelper databaseHelper;
    private AppCompatActivity mActivity;

    private String NEXT_SEED_KEY = "NextSeed";
    private String SECRET_KEY = "Secret";
    private String PUBLIC_KEY = "Public";
    private String ADDRESS_KEY = "Address";


    public WalletInfoAPIHandler(AppCompatActivity mActivity) {
        this.mActivity = mActivity;
        databaseHelper = new DatabaseHelper(mActivity);
    }


    public void getUserWalletsAddressInfo(boolean isNewWalletInfo, WebAPIResponseListener webAPIResponseListener) {
        List<WalletAddressJSONDataModel> walletAddressJSONDataModels = databaseHelper.getAllWalletInformation();
        String walletName = "", addressJson = "", seedValue = "", walletId = "";
        int numberOfAddresses = 0;

        if (walletAddressJSONDataModels != null && walletAddressJSONDataModels.size() > 0) {

            if (!isNewWalletInfo) {
                for (WalletAddressJSONDataModel walletDataModel : walletAddressJSONDataModels) {

                    walletName = walletDataModel.getWalletName();
                    addressJson = walletDataModel.getAddressInfoJson();
                    walletId = walletDataModel.getWalletId();
                    seedValue = walletDataModel.getSeedValue();
                    numberOfAddresses = walletDataModel.getNumberOfAddress();

                    getWalletInfo(addressJson, walletName, walletAddressJSONDataModels.size(), isNewWalletInfo, seedValue, numberOfAddresses, walletId, webAPIResponseListener);
                }
            } else {

                walletName = walletAddressJSONDataModels.get(walletAddressJSONDataModels.size() - 1).getWalletName();
                addressJson = walletAddressJSONDataModels.get(walletAddressJSONDataModels.size() - 1).getAddressInfoJson();
                seedValue = walletAddressJSONDataModels.get(walletAddressJSONDataModels.size() - 1).getSeedValue();
                numberOfAddresses = walletAddressJSONDataModels.get(walletAddressJSONDataModels.size() - 1).getNumberOfAddress();
                walletId = walletAddressJSONDataModels.get(walletAddressJSONDataModels.size() - 1).getWalletId();

                getWalletInfo(addressJson, walletName, walletAddressJSONDataModels.size(), isNewWalletInfo, seedValue, numberOfAddresses, walletId, webAPIResponseListener);

            }
        } else
            AndroidAppUtils.showErrorLog(TAG, "walletAddressJSONDataModels is null or empty");
    }

    /**
     * Get Wallet Info
     *
     * @param addressJson
     * @param walletName
     * @param size
     * @param isNewWalletInfo
     * @param seedValue
     * @param numberOfAddresses
     * @param walletId
     * @param webAPIResponseListener
     */
    private void getWalletInfo(String addressJson, final String walletName, int size, boolean isNewWalletInfo, String seedValue, int numberOfAddresses, String walletId, WebAPIResponseListener webAPIResponseListener) {
        String address = "";
        try {

            JSONArray jsonArray = new JSONArray(addressJson);


            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject walletAddressJson = jsonArray.getJSONObject(i);

                if (walletAddressJson.has(ADDRESS_KEY)) {

                    address = address + walletAddressJson.getString(ADDRESS_KEY) + ",";


                } else
                    AndroidAppUtils.showErrorLog(TAG, "JSON doesnot have address key");

            }

            AndroidAppUtils.showLog(TAG, "Address value for seed: " + address);

            new GetWallInfoAPIHandler(mActivity, address, walletName, size, isNewWalletInfo, seedValue, numberOfAddresses, walletId, webAPIResponseListener);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * Get wallet addresses using seed
     *
     * @param seedValue
     * @param numberOfAdresses
     * @param isCreatingNewAddress
     * @param webAPIResponseListener
     */
    public void getWalletAddresses(String seedValue, int numberOfAdresses, boolean isCreatingNewAddress, WebAPIResponseListener webAPIResponseListener) {
        try {

            String addressFromJson = "";
            String addressForAPI = "";

            String address = Mobile.getAddresses(seedValue, numberOfAdresses);

            JSONArray jsonArray = new JSONArray(address);
            ArrayList<String> addressArrayList = new ArrayList<>();

            AndroidAppUtils.showLog(TAG, "addresses: " + address);

//            if (!isCreatingNewAddress) {
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject walletAddressJson = jsonArray.getJSONObject(i);

                if (walletAddressJson.has(ADDRESS_KEY)) {


                    addressFromJson = walletAddressJson.getString(ADDRESS_KEY);
                    addressForAPI = addressForAPI + addressFromJson + ",";
                    addressArrayList.add(addressFromJson);

                } else
                    AndroidAppUtils.showErrorLog(TAG, "JSON doesnot have address key");


            }
//            } else {

//            if (isCreatingNewAddress) {
//                if (jsonArray.length() > 0) {
//
//                    JSONObject walletAddressJson = jsonArray.getJSONObject(jsonArray.length() - 1);
//
//                    if (walletAddressJson.has(ADDRESS_KEY)) {
//
//
//                        addressFromJson = walletAddressJson.getString(ADDRESS_KEY);
//                        addressForAPI = addressForAPI + addressFromJson + ",";
//                        addressArrayList.add(addressFromJson);
//
//                    } else
//                        AndroidAppUtils.showErrorLog(TAG, "JSON doesnot have address key");
//                }
//            } else
//                AndroidAppUtils.showLog(TAG, "Showing already created addresses");
//            }

            AndroidAppUtils.showLog(TAG, "Address List: " + addressArrayList.size());
            new GetWalletAddressAPIHandler(mActivity, addressForAPI, addressArrayList.size(), isCreatingNewAddress, addressArrayList, webAPIResponseListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<WalletNameAndAddressDataModel> allWalletsAddresses() {

        ArrayList<WalletNameAndAddressDataModel> allWalletsAddressList = new ArrayList<>();

        List<WalletAddressJSONDataModel> walletAddressJSONDataModels = databaseHelper.getAllWalletInformation();

        if (walletAddressJSONDataModels != null && walletAddressJSONDataModels.size() > 0) {

            for (WalletAddressJSONDataModel walletDataModel : walletAddressJSONDataModels) {


                try {

                    JSONArray jsonArray = new JSONArray(walletDataModel.getAddressInfoJson());
                    String walletName = walletDataModel.getWalletName();

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject walletAddressJson = jsonArray.getJSONObject(i);

                        if (walletAddressJson.has(ADDRESS_KEY)) {

                            AndroidAppUtils.showLog(TAG, "Address:  " + walletAddressJson.getString(ADDRESS_KEY));
                            WalletNameAndAddressDataModel walletNameAndAddressDataModel = new WalletNameAndAddressDataModel();

                            walletNameAndAddressDataModel.setAddress(walletAddressJson.getString(ADDRESS_KEY));
                            walletNameAndAddressDataModel.setWalletName(walletName);
                            allWalletsAddressList.add(walletNameAndAddressDataModel);

                        } else
                            AndroidAppUtils.showErrorLog(TAG, "JSON doesnot have address key");

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        } else
            AndroidAppUtils.showErrorLog(TAG, "walletAddressJSONDataModels is null or empty");
        return allWalletsAddressList;
    }

}
