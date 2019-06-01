package com.example.akansha.cryptocurrency.Model;

import java.io.Serializable;

public class WalletNameAndAddressDataModel implements Serializable{

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    private String address = "";
    private String walletName = "";

}
