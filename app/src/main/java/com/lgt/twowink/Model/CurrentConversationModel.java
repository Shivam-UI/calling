package com.lgt.twowink.Model;

public class CurrentConversationModel {
    private String from,message,seen,time,user_image,user_name;
    private int messageType;

    public CurrentConversationModel() {
    }

    public CurrentConversationModel(String from, String message, String seen, String time, String user_image, String user_name, int messageType) {
        this.from = from;
        this.message = message;
        this.seen = seen;
        this.time = time;
        this.user_image = user_image;
        this.user_name = user_name;
        this.messageType = messageType;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }
}
