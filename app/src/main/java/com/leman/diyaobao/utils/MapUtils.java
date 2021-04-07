package com.leman.diyaobao.utils;

import android.util.Log;

public class MapUtils {

    private static final double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 根据两点间经纬度坐标（double值），计算两点间距离，
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return 距离：单位为k米
     */
    public static double DistanceOfTwoPoints(double lat1,double lng1,
                                             double lat2,double lng2) {
        Log.e("wzj","lat1+++++++++++++++++++++: "+lat1);
        Log.e("wzj","lng1+++++++++++++++++++++: "+lng1);
        Log.e("wzj","lat2+++++++++++++++++++++: "+lat2);
        Log.e("wzj","lng2+++++++++++++++++++++: "+lng2);
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }


}
