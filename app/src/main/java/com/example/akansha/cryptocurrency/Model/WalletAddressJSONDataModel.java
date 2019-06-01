package com.example.akansha.cryptocurrency.Model;

public class WalletAddressJSONDataModel {


    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getAddressInfoJson() {
        return addressInfoJson;
    }

    public void setAddressInfoJson(String addressInfoJson) {
        this.addressInfoJson = addressInfoJson;
    }

    private String walletName = "";
    private String addressInfoJson = "";

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

    private String seedValue = "";
    private int numberOfAddress = 0;

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    private String walletId;

}
