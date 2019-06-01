package com.example.akansha.cryptocurrency.Model;

/**
 * Class to to keep model data
 */
public class WalletDataModel {

    private String wallet_name;
    private int wallet_hours;
    private double wallet_balance;

    public String getSeedValue() {
        return seedValue;
    }

    public void setSeedValue(String seedValue) {
        this.seedValue = seedValue;
    }

    public int getNumberOfAddress() {
        return numberOfAddress;
    }

    public void setNumberOfAddress(int numberOfAddress) {
        this.numberOfAddress = numberOfAddress;
    }

    private String seedValue;
    private int numberOfAddress;

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    private String  walletId;

    public WalletDataModel(String wallet_name, int wallet_hours, double wallet_balance) {

        this.wallet_name = wallet_name;
        this.wallet_hours = wallet_hours;
        this.wallet_balance = wallet_balance;
    }

    public String getWallet_name() {
        return wallet_name;
    }

    public void setWallet_name(String wallet_name) {
        this.wallet_name = wallet_name;
    }

    public int getWallet_hours() {
        return wallet_hours;
    }

    public void setWallet_hours(int wallet_hours) {
        this.wallet_hours = wallet_hours;
    }

    public double getWallet_balance() {
        return wallet_balance;
    }

    public void setWallet_balance(long wallet_balance) {
        this.wallet_balance = wallet_balance;
    }


}
