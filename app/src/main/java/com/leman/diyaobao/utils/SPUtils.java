package com.leman.diyaobao.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {
    private static SharedPreferences sp = null;
    public static void getSP(Context context){
        if(sp == null){
            sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        }
    }

    public static void putString(String key,String value){
    //    getSP(context);
        sp.edit().putString(key,value).commit();
    }

    public static void putInt(String key,int value){
    //    getSP(context);
        sp.edit().putInt(key,value).commit();
    }

    public static void putBoolean(String key,boolean value){
    //    getSP(context);
        sp.edit().putBoolean(key,value).commit();
    }
    public static void putFloat(String key,float value){
        //    getSP(context);
        sp.edit().putFloat(key,value).commit();
    }

    public static String getString(String key,String defvalue){
    //    getSP(context);
        return sp.getString(key,defvalue);
    }

    public static int getInt(String key,int defValue){
    //    getSP(context);
        return sp.getInt(key,defValue);
    }

    public static boolean getBoolean(String key,boolean defvalue){
    //    getSP(context);
        return sp.getBoolean(key,defvalue);
    }

    public static Float getFloat(String key,float defvalue){
        //    getSP(context);
        return sp.getFloat(key,defvalue);
    }


}
