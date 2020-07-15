package com.lgt.twowink.Model;

public class CurrentCallsModel {
    private String user_name,user_image,last_msg,online_offline;

    public CurrentCallsModel() {
    }

    public CurrentCallsModel(String user_name, String user_image, String last_msg, String online_offline) {
        this.user_name = user_name;
        this.user_image = user_image;
        this.last_msg = last_msg;
        this.online_offline = online_offline;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getLast_msg() {
        return last_msg;
    }

    public void setLast_msg(String last_msg) {
        this.last_msg = last_msg;
    }

    public String getOnline_offline() {
        return online_offline;
    }

    public void setOnline_offline(String online_offline) {
        this.online_offline = online_offline;
    }
}
