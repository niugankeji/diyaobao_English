package com.leman.diyaobao.entity;

public class HowSeeMeItem {

    private String id;
    private String imageUrl;
    private String from_user;
    private String to_user;
    private String State;

    public String getFrom_user() {
        return from_user;
    }

    public void setFrom_user(String from_user) {
        this.from_user = from_user;
    }

    public String getTo_user() {
        return to_user;
    }

    public void setTo_user(String to_user) {
        this.to_user = to_user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }
}
