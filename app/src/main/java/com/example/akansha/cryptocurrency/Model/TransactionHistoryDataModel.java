package com.example.akansha.cryptocurrency.Model;

public class TransactionHistoryDataModel {

    public String getTransaction_type() {
        return transaction_type;
    }

    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate_and_time() {
        return date_and_time;
    }

    public void setDate_and_time(String date_and_time) {
        this.date_and_time = date_and_time;
    }

    public String getTo_address() {
        return to_address;
    }

    public void setTo_address(String to_address) {
        this.to_address = to_address;
    }

    public String getFrom_address() {
        return from_address;
    }

    public void setFrom_address(String from_address) {
        this.from_address = from_address;
    }

    public double getTransaction_coins() {
        return transaction_coins;
    }

    public void setTransaction_coins(double transaction_coins) {
        this.transaction_coins = transaction_coins;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getWallet_name() {
        return wallet_name;
    }

    public void setWallet_name(String wallet_name) {
        this.wallet_name = wallet_name;
    }

    public boolean isTransaction_status() {
        return transaction_status;
    }

    public void setTransaction_status(boolean transaction_status) {
        this.transaction_status = transaction_status;
    }

    private String transaction_type = "";
    private String date = "";
    private String date_and_time = "";
    private String to_address = "";
    private String from_address = "";
    private double transaction_coins = 0.0;
    private long timestamp = 0;
    private String wallet_name = "";
    private boolean transaction_status = false;
    private String address = "";

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
