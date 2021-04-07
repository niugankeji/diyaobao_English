package com.leman.diyaobao.map;

/**
 * Created by moos on 2018/1/11.
 * func:marker的唯一标记，用于处理点击事件
 */

public class MarkerSign {
    private String name;
    private String userid;
    private String url;
    private String address;
    private double lat; //经度
    private double lgt;//纬度

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLgt() {
        return lgt;
    }

    public void setLgt(double lgt) {
        this.lgt = lgt;
    }

}
