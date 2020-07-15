package com.lgt.twowink.Model;

public class Data {
    private String current_user_id;
    private int icon;
    private String body;
    private String customer_id;

    public Data() {
    }

    public Data(String current_user_id, int icon, String body, String customer_id) {
        this.current_user_id = current_user_id;
        this.icon = icon;
        this.body = body;
        this.customer_id = customer_id;
    }

    public String getCurrent_user_id() {
        return current_user_id;
    }

    public void setCurrent_user_id(String current_user_id) {
        this.current_user_id = current_user_id;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }
}
