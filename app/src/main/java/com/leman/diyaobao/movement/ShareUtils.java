package com.leman.diyaobao.movement;

import android.content.Context;
import android.content.SharedPreferences;

import com.leman.diyaobao.MyApp;

/**
 * sharedPreferences封装
 */
public class ShareUtils {

    public static final String NAME = "config";

    //定义String,int,Boolean类型
    //键值对存储
    public static void putString(Context mContext, String key, String value) {
        SharedPreferences sp = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    //键 默认值存储
    public static String getString(Context mContext, String key, String defValue) {
        SharedPreferences sp = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }


    //键值对存储
    public static void putInt(Context mContext, String key, int value) {
        SharedPreferences sp = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }

    //键 默认值存储
    public static int getInt(Context mContext, String key, int defValue) {
        SharedPreferences sp = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }

    public static int getInt(String key) {
        SharedPreferences sp = MyApp.getContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, 0);
    }


    //键值对存储
    public static void putBoolean(Context mContext, String key, Boolean value) {
        SharedPreferences sp = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    //键 默认值存储
    public static Boolean getBoolean(Context mContext, String key, Boolean defValue) {
        SharedPreferences sp = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    //定义删除功能
    //删除 单个
    public static void deleShare(Context mContext, String key) {
        SharedPreferences sp = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        sp.edit().remove(key).commit();
    }

    //删除全部
    public static void deleAll(Context mContext, String key) {
        SharedPreferences sp = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        sp.edit().clear().commit();
    }


}
