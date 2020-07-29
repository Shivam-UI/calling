package com.lgt.twowink.Model;

public class HistoryModel {

    public String tbl_package_purches_id;
    public String package_name;
    public String package_type;
    public String package_coins;
    public String payment_status;
    public String transection_id;
    public String time;

    public String getPackage_image() {
        return package_image;
    }

    public void setPackage_image(String package_image) {
        this.package_image = package_image;
    }

    public String package_image;

    public HistoryModel() {
    }

    public HistoryModel(String tbl_package_purches_id, String package_name, String package_type, String package_coins, String payment_status, String transection_id, String time, String package_image) {
        this.tbl_package_purches_id = tbl_package_purches_id;
        this.package_name = package_name;
        this.package_type = package_type;
        this.package_coins = package_coins;
        this.payment_status = payment_status;
        this.transection_id = transection_id;
        this.time = time;
        this.package_image = package_image;
    }

    public String getTbl_package_purches_id() {
        return tbl_package_purches_id;
    }

    public void setTbl_package_purches_id(String tbl_package_purches_id) {
        this.tbl_package_purches_id = tbl_package_purches_id;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getPackage_type() {
        return package_type;
    }

    public void setPackage_type(String package_type) {
        this.package_type = package_type;
    }

    public String getPackage_coins() {
        return package_coins;
    }

    public void setPackage_coins(String package_coins) {
        this.package_coins = package_coins;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getTransection_id() {
        return transection_id;
    }

    public void setTransection_id(String transection_id) {
        this.transection_id = transection_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
