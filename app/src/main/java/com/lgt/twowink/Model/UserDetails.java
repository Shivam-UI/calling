package com.lgt.twowink.Model;

public class UserDetails {

    private String user_id,name,user_name,mobile,call_coin,chat_coin,refer_code,user_image;

    public UserDetails() {
    }

    public UserDetails(String user_id, String name, String user_name, String mobile, String call_coin, String chat_coin, String refer_code, String user_image) {
        this.user_id = user_id;
        this.name = name;
        this.user_name = user_name;
        this.mobile = mobile;
        this.call_coin = call_coin;
        this.chat_coin = chat_coin;
        this.refer_code = refer_code;
        this.user_image = user_image;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCall_coin() {
        return call_coin;
    }

    public void setCall_coin(String call_coin) {
        this.call_coin = call_coin;
    }

    public String getChat_coin() {
        return chat_coin;
    }

    public void setChat_coin(String chat_coin) {
        this.chat_coin = chat_coin;
    }

    public String getRefer_code() {
        return refer_code;
    }

    public void setRefer_code(String refer_code) {
        this.refer_code = refer_code;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }
}
