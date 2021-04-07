package com.leman.diyaobao.entity;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.File;

public class DuoTu extends DataSupport {
    @Column(unique = true)
    private int id;
    private File data_image;
    private String data_lat;
    private String data_lon;
    private String data_day;
    private String data_address;
    private String user_number;
    private String data_duty;
    private String data_lai;
    private String data_model;
    private String data_cost;
    private String data_munsell;
    private String image_url;
    private String data_color;
    private String data_angle;
    private String moshi;
    private DuoTu duotu;
    private String cishu;
    private String name;

    private int zhiwudata_id;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getZhiwudata_id() {
        return zhiwudata_id;
    }

    public void setZhiwudata_id(int zhiwudata_id) {
        this.zhiwudata_id = zhiwudata_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMoshi() {
        return moshi;
    }

    public void setMoshi(String moshi) {
        this.moshi = moshi;
    }

    public DuoTu getDuotu() {
        return duotu;
    }

    public void setDuotu(DuoTu duotu) {
        this.duotu = duotu;
    }



    public String getCishu() {
        return cishu;
    }

    public void setCishu(String cishu) {
        this.cishu = cishu;
    }

    public File getData_image() {
        return data_image;
    }

    public void setData_image(File data_image) {
        this.data_image = data_image;
    }

    public String getData_lat() {
        return data_lat;
    }

    public void setData_lat(String data_lat) {
        this.data_lat = data_lat;
    }

    public String getData_lon() {
        return data_lon;
    }

    public void setData_lon(String data_lon) {
        this.data_lon = data_lon;
    }

    public String getData_day() {
        return data_day;
    }

    public void setData_day(String data_day) {
        this.data_day = data_day;
    }

    public String getData_address() {
        return data_address;
    }

    public void setData_address(String data_address) {
        this.data_address = data_address;
    }

    public String getUser_number() {
        return user_number;
    }

    public void setUser_number(String user_number) {
        this.user_number = user_number;
    }

    public String getData_duty() {
        return data_duty;
    }

    public void setData_duty(String data_duty) {
        this.data_duty = data_duty;
    }

    public String getData_lai() {
        return data_lai;
    }

    public void setData_lai(String data_lai) {
        this.data_lai = data_lai;
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

    public String getData_munsell() {
        return data_munsell;
    }

    public void setData_munsell(String data_munsell) {
        this.data_munsell = data_munsell;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getData_color() {
        return data_color;
    }

    public void setData_color(String data_color) {
        this.data_color = data_color;
    }

    public String getData_angle() {
        return data_angle;
    }

    public void setData_angle(String data_angle) {
        this.data_angle = data_angle;
    }
}
