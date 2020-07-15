package com.lgt.twowink.Model;

public class ChatModel {
    private String from;
    private String message;
    private int messageType;
            private String messageid;
            private String user_image;
            private String user_name;
                    private String seen;
    private long time;


    public ChatModel() {
    }

    public ChatModel(String from, String message, int messageType, String messageid, String user_image, String user_name, String seen, long time) {
        this.from = from;
        this.message = message;
        this.messageType = messageType;
        this.messageid = messageid;
        this.user_image = user_image;
        this.user_name = user_name;
        this.seen = seen;
        this.time = time;
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

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
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

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
