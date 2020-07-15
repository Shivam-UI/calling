package com.lgt.twowink.Model;

import java.io.Serializable;

public class PackagesModel  {

    private String tbl_package_id,package_name,price,coins,package_color,image;



    public PackagesModel() {
    }


    public PackagesModel(String tbl_package_id, String package_name, String price, String coins, String package_color, String image) {
        this.tbl_package_id = tbl_package_id;
        this.package_name = package_name;
        this.price = price;
        this.coins = coins;
        this.package_color = package_color;
        this.image = image;
    }

    public String getTbl_package_id() {
        return tbl_package_id;
    }

    public void setTbl_package_id(String tbl_package_id) {
        this.tbl_package_id = tbl_package_id;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }

    public String getPackage_color() {
        return package_color;
    }

    public void setPackage_color(String package_color) {
        this.package_color = package_color;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
