package com.example.akansha.cryptocurrency.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.akansha.cryptocurrency.Model.WalletAddressJSONDataModel;
import com.example.akansha.cryptocurrency.Utils.AndroidAppUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHelper is used for storing and extracting user details from Database.
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "WALLET_INFO_DB";
    private static final int DATABASE_VERSION = 1;
    private final String WALLET_INFO_TABLE = "WALLET_DETAIL";
    private final String WALLET_UID = "_wallet_id";
    private final String WALLET_NAME = "WALLET_NAME";
    private final String SEED_VALUE = "SEED_VALUE";
    private final String NUMBER_OF_ADDRESS = "NUMBER_OF_ADDRESS";
    private final String ADDRESS_JSON = "WALLET_INFO";


    private Context context;
    private String TAG = DatabaseHelper.class.getSimpleName();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createWalletTable = "CREATE TABLE " + WALLET_INFO_TABLE + " (" + WALLET_UID + " VARCHAR PRIMARY KEY ," + WALLET_NAME + " VARCHAR ," + SEED_VALUE + " VARCHAR ," + NUMBER_OF_ADDRESS + " INT ," + ADDRESS_JSON + " VARCHAR );";


        try {
            AndroidAppUtils.showInfoLog(TAG, "Creating tables");

            db.execSQL(createWalletTable);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        AndroidAppUtils.showLog(TAG, "onUpgrade database: old version: " + oldVersion + " new version: " + newVersion);


    }


    /**
     * Insert Log file details in database
     *
     * @return
     */
    public boolean insertWalletInfo(String walletId, String walletName, String walletInfo, String seedValue, int number_of_address) {
        if (!(walletName.isEmpty() && walletInfo.isEmpty())) {
            try {
                AndroidAppUtils.showLog(TAG, "Adding data in wallet Table");
                SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

                ContentValues contentValues = new ContentValues();
                contentValues.put(WALLET_NAME, walletName);
                contentValues.put(ADDRESS_JSON, walletInfo);
                contentValues.put(WALLET_UID, walletId);
                contentValues.put(SEED_VALUE, seedValue);
                contentValues.put(NUMBER_OF_ADDRESS, number_of_address);

                sqLiteDatabase.insert(WALLET_INFO_TABLE, null, contentValues);
                sqLiteDatabase.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            AndroidAppUtils.showErrorLog(TAG, "Fields are empty:: ");
            return false;
        }
    }


    /**
     * Fetch first LOG detail from LOG TABLE.
     *
     * @return
     */
    public String getWalletName(String walletId) {

        try {

            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            String readQuery = "SELECT * FROM " + WALLET_INFO_TABLE + " WHERE " + WALLET_UID + " = '" + walletId + "'";
            Cursor cursor = sqLiteDatabase.rawQuery(readQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToFirst()) {
                    String walletName = (cursor.getString(cursor.getColumnIndex(WALLET_NAME)));
                    AndroidAppUtils.showLog(TAG, "Fetched wallet name::" + walletName);
                    return walletName;
                }
            } else {
                AndroidAppUtils.showErrorLog(TAG, "Cursor may be null or size zero in wallet TABLE:: " + cursor.getCount());
            }
            if (cursor != null)
                cursor.close();
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get wallet info
     *
     * @param walletId
     * @return
     */
    public String getWalletInfo(String walletId) {

        try {

            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            String readQuery = "SELECT * FROM " + WALLET_INFO_TABLE + " WHERE " + WALLET_UID + " = '" + walletId + "'";
            Cursor cursor = sqLiteDatabase.rawQuery(readQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToFirst()) {
                    String walletInfo = (cursor.getString(cursor.getColumnIndex(ADDRESS_JSON)));
                    AndroidAppUtils.showLog(TAG, "Fetched wallet info::" + walletInfo);
                    return walletInfo;
                }
            } else {
                AndroidAppUtils.showErrorLog(TAG, "Cursor may be null or size zero in wallet TABLE:: " + cursor.getCount());
            }
            if (cursor != null)
                cursor.close();
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get All wallets Information
     *
     * @return
     */
    public List<WalletAddressJSONDataModel> getAllWalletInformation() {
        List<WalletAddressJSONDataModel> list = new ArrayList<WalletAddressJSONDataModel>();

        try {
            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            String readQuery = "SELECT * FROM " + WALLET_INFO_TABLE;
            Cursor cursor = sqLiteDatabase.rawQuery(readQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    WalletAddressJSONDataModel walletAddressJSONDataModel = new WalletAddressJSONDataModel();

                    walletAddressJSONDataModel.setWalletName(cursor.getString(cursor.getColumnIndex(WALLET_NAME)));
                    walletAddressJSONDataModel.setAddressInfoJson(cursor.getString(cursor.getColumnIndex(ADDRESS_JSON)));
                    walletAddressJSONDataModel.setSeedValue(cursor.getString(cursor.getColumnIndex(SEED_VALUE)));
                    walletAddressJSONDataModel.setNumberOfAddress(cursor.getInt(cursor.getColumnIndex(NUMBER_OF_ADDRESS)));
                    walletAddressJSONDataModel.setWalletId(cursor.getString(cursor.getColumnIndex(WALLET_UID)));

                    list.add(walletAddressJSONDataModel);
                    AndroidAppUtils.showLog(TAG, "Fetched JsonObject from DataBase is::" + walletAddressJSONDataModel.getAddressInfoJson());
                    AndroidAppUtils.showLog(TAG, "Fetched File path from DataBase is::" + walletAddressJSONDataModel.getWalletName());
                }
            } else {
                AndroidAppUtils.showErrorLog(TAG, "Cursor may be null or size zero in LOG TABLE:: " + cursor.getCount());

            }
            if (cursor != null)
                cursor.close();
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    public void updateNumberOfAddressAndJSONForWallet(String walletId, int numberOfAddress, String addressJson) {

        AndroidAppUtils.showLog(TAG, "updating JSON and number of address");
        SQLiteDatabase database = this.getWritableDatabase();
        if (database != null) {
            ContentValues values = new ContentValues();
            values.put(NUMBER_OF_ADDRESS, numberOfAddress);
            values.put(ADDRESS_JSON, addressJson);
            database.update(WALLET_INFO_TABLE, values, WALLET_UID + "=?", new String[]{walletId});
            database.close();
        } else
            AndroidAppUtils.showErrorLog(TAG, "database is null");
    }

}
