package com.example.akansha.cryptocurrency.Preferences;

/**
 * Interface to store the App Prefernces
 */
public interface PreferenceHelper {


    String PREFERENCE_NAME = "solar_bankers_preference";
    int PRIVATE_MODE = 0;

    String is_wallet_pin_set = "is_wallet_pin_set";
    String wallet_pin = "wallet_pin";

    String wallet_uid = "wallet_uid";

    String node_url = "node_url";

    String is_wallet_created = "is_wallet_created";
}
