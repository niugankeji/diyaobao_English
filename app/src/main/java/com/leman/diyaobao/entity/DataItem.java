package com.leman.diyaobao.entity;

public class DataItem {
    private int id;
    private String data_lai;
    private String data_address;
    private String data_munsell;
    private String data_lon;
    private String data_lat;
    private String data_image;
    private String data_day;
    private String data_duty;
    private String data_model;
    private String data_cost;
    private String data_moshi;
    private String data_ping_lai;
    private String name;


    public boolean isSelect;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData_ping_lai() {
        return data_ping_lai;
    }

    public void setData_ping_lai(String data_ping_lai) {
        this.data_ping_lai = data_ping_lai;
    }

    public String getData_moshi() {
        return data_moshi;
    }

    public void setData_moshi(String data_moshi) {
        this.data_moshi = data_moshi;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData_lai() {
        return data_lai;
    }

    public void setData_lai(String data_lai) {
        this.data_lai = data_lai;
    }

    public String getData_address() {
        return data_address;
    }

    public void setData_address(String data_address) {
        this.data_address = data_address;
    }

    public String getData_munsell() {
        return data_munsell;
    }

    public void setData_munsell(String data_munsell) {
        this.data_munsell = data_munsell;
    }

    public String getData_lon() {
        return data_lon;
    }

    public void setData_lon(String data_lon) {
        this.data_lon = data_lon;
    }

    public String getData_lat() {
        return data_lat;
    }

    public void setData_lat(String data_lat) {
        this.data_lat = data_lat;
    }

    public String getData_image() {
        return data_image;
    }

    public void setData_image(String data_image) {
        this.data_image = data_image;
    }

    public String getData_day() {
        return data_day;
    }

    public void setData_day(String data_day) {
        this.data_day = data_day;
    }

    public String getData_duty() {
        return data_duty;
    }

    public void setData_duty(String data_duty) {
        this.data_duty = data_duty;
    }

    public String getData_model() {
        return data_model;
    }

    public void setData_model(String data_model) {
        this.data_model = data_model;
    }

    public String getData_cost() {
        return data_cost;
    }

    public void setData_cost(String data_cost) {
        this.data_cost = data_cost;
    }
}
