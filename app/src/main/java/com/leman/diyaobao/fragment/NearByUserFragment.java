package com.leman.diyaobao.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.leman.diyaobao.Constant;
import com.leman.diyaobao.R;
import com.leman.diyaobao.map.LocationUtil;
import com.leman.diyaobao.map.MarkerSign;
import com.leman.diyaobao.map.MarkerUtils;
import com.leman.diyaobao.okhttp.HttpUrls;
import com.leman.diyaobao.dialog.CommomDialog;
import com.leman.diyaobao.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


public class NearByUserFragment extends Fragment implements LocationSource {
    private View view;

    private MapView mapView;
    private AMap aMap;
    private LocationSource.OnLocationChangedListener mListener = null;//定位监听器
    private LocationUtil locationUtil;

    MyLocationStyle myLocationStyle;

    List<MarkerSign> markerSignList = new ArrayList<>();
    MarkerSign markerSign;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.nearby_user_layout, container, false);
        mapView = view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        init();

        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getObject().getClass().equals(MarkerSign.class)) {
                    //是自定义marker
                    final MarkerSign sign = (MarkerSign) marker.getObject();

                    new CommomDialog(getActivity(), R.style.dialog, "Would you request to view " + sign.getName() + "'s data？", new CommomDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm) {
                                OkHttpUtils
                                        .post()
                                        .url(HttpUrls.APPLYSEEDATA)
                                        .addParams("from_user", SPUtils.getString(Constant.USERID, ""))
                                        .addParams("to_user", sign.getUserid())
                                        .build()
                                        .execute(new StringCallback() {
                                            @Override
                                            public void onError(Call call, Exception e, int id) {
                                                if (getActivity() != null){
                                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onResponse(String response, int id) {
                                                Log.e("wzj", "0000000000000: " + response);
                                                try {
                                                    JSONObject jsonObject = new JSONObject(response);
                                                    String message = jsonObject.optString("message");
                                                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }

                                        });
                            }


                            dialog.dismiss();
                        }

                    }).setTitle("Tips").show();

                }
                return false;
            }
        });

        return view;
    }

    private void init() {

        myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);

        if (aMap == null) {
            aMap = mapView.getMap();
        }
        setLocationCallBack();
        //设置定位监听
        aMap.setLocationSource(this);
        //设置缩放级别
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        //显示定位层并可触发，默认false
        aMap.setMyLocationEnabled(true);

        OkHttpUtils
                .get()
                .url(HttpUrls.GETNEARUSER)
                .addParams("user_address", SPUtils.getString(Constant.ERAEADDESS, ""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("wzj", "0000000000000: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.optString("message");
                            if (message.equals("Request successful！")) {
                                JSONArray array = jsonObject.optJSONArray("info");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject user = array.getJSONObject(i);
                                    //    if (!user.optString("user_phone").equals(SPUtils.getString(Constant.PHONNUMBER,""))){
                                    markerSign = new MarkerSign();
                                    markerSign.setLat(Double.parseDouble(user.optString("user_lat")));
                                    markerSign.setLgt(Double.parseDouble(user.optString("user_lon")));
                                    markerSign.setName(user.optString("uesr_name"));
                                    markerSign.setUserid(user.optString("user_number"));
                                    markerSign.setUrl(HttpUrls.IMAGE + user.getString("uesr_avatar"));
                                    markerSignList.add(markerSign);
                                    //    }
                                }
                                MarkerUtils.addCustomMarkersToMap(aMap, getContext(), markerSignList);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                });
    }

    private void setLocationCallBack() {

        locationUtil = new LocationUtil();
        locationUtil.setLocationCallBack(new LocationUtil.ILocationCallBack() {
            @Override
            public void callBack(String area, String str, double lat, double lgt, AMapLocation aMapLocation) {
                //根据获取的经纬度，将地图移动到定位位置
                Log.e("wzj","定位+++++++++++++++++： "+lat+"++++++++++++++: "+lgt);
                LocationUtil.stopLocation();
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(lat, lgt)));
                mListener.onLocationChanged(aMapLocation);
                //添加定位图标
//                aMap.addMarker(locationUtil.getMarkerOption(str, lat, lgt, R.mipmap.ico_markerm));
//                LocationUtil.stopLocation();

//                for (int i = 1; i < 5; i++) {
//                    markerSign = new MarkerSign();
//                    markerSign.setLat(lat + i - 1);
//                    markerSign.setLgt(lgt + i - 1);
//                    markerSign.setName("第" + i + "个");
//                    markerSignList.add(markerSign);
//                }
//                MarkerUtils.addCustomMarkersToMap(aMap, getContext(), markerSignList);

            }
        });
    }

    //定位激活回调
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        Log.e("wzj","定位激活回调+++++++++++++++++++++++++++++++");
        mListener = onLocationChangedListener;
        locationUtil.startLocate(getContext());
    }

    @Override
    public void deactivate() {
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        //暂停地图的绘制
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //销毁地图
        mapView.onDestroy();
        LocationUtil.stopLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        //重新绘制加载地图
        Log.e("wzj","重新绘制加载地图+++++++++++++++++++++++++++++++");
        mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
