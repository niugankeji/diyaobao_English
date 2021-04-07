package com.leman.diyaobao.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.bumptech.glide.Glide;
import com.leman.diyaobao.Constant;
import com.leman.diyaobao.R;
import com.leman.diyaobao.adapter.ImageAdapter;
import com.leman.diyaobao.map.LocationUtil;
import com.leman.diyaobao.map.MarkerSign;
import com.leman.diyaobao.map.MarkerUtils;
import com.leman.diyaobao.okhttp.HttpUrls;
import com.leman.diyaobao.dialog.CommomDialog;
import com.leman.diyaobao.utils.RecycleGridDivider;
import com.leman.diyaobao.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

import static com.leman.diyaobao.okhttp.HttpUrls.HOMEADDRESS;
import static com.leman.diyaobao.utils.MapUtils.DistanceOfTwoPoints;


public class NearByUserFragment1 extends Fragment implements LocationSource {
    private View view;

    private MapView mapView;
    private AMap aMap;
    private OnLocationChangedListener mListener = null;//定位监听器
    private LocationUtil locationUtil;

    MyLocationStyle myLocationStyle;

    List<MarkerSign> markerSignList = new ArrayList<>();
    MarkerSign markerSign;

    private LinearLayout dataLin;
    private LinearLayout headLin;

    private CircleImageView civ_avatar;
    private TextView show;
    private String to_user;
    private String user_name;
    private TextView address;
    private RecyclerView recycler_view;
    private TextView juli;

    private List<String> urlList;
    ImageAdapter adapter;

    double mlat;
    double mlgt;

    private LinearLayout search_LL;
    private EditText search_ET;
    private TextView search_TV;

    private TextView myaddress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_found1, container, false);
        mapView = view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        juli = view.findViewById(R.id.juli);

        civ_avatar = view.findViewById(R.id.civ_avatar);
        show = view.findViewById(R.id.show);
        address = view.findViewById(R.id.address);
        recycler_view = view.findViewById(R.id.recycler_view);
        //纵向线性布局
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),3);
        recycler_view.addItemDecoration(new RecycleGridDivider());
        recycler_view.setLayoutManager(layoutManager);

        dataLin = view.findViewById(R.id.dataLin);
        headLin = view.findViewById(R.id.headLin);
        headLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataLin.setVisibility(View.GONE);
            }
        });

        search_LL = view.findViewById(R.id.search_LL);
        search_ET = view.findViewById(R.id.search_ET);
        search_TV = view.findViewById(R.id.search_TV);

        myaddress = view.findViewById(R.id.myaddress);


        FoundFragment2.setListener(new FoundFragment2.onListener() {
            @Override
            public void OnListener() {
                if (search_LL.getVisibility() == View.GONE){
                    search_LL.setVisibility(View.VISIBLE);
                    search_TV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            search();
                        }
                    });
                }else {
                    search_LL.setVisibility(View.GONE);
                }
            }
        });

        //申请查看数据
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Log.e("wzj","积分++++++++++++++++++++++++： "+SPUtils.getString(Constant.JIFEN,"0"));
                if (Double.parseDouble(SPUtils.getString(Constant.JIFEN,"0")) <= 0.0){
                    Toast.makeText(getContext(),"您没有可用积分，无法申请查看对方数据！",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (to_user.equals(SPUtils.getString(Constant.USERID, ""))){
                    Toast.makeText(getContext(),"这是您自己的账号",Toast.LENGTH_SHORT).show();
                    return;
                }
                new CommomDialog(getActivity(), R.style.dialog, "是否申请查看" + user_name + "的数据？", new CommomDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm) {

                            OkHttpUtils
                                    .post()
                                    .url(HttpUrls.APPLYSEEDATA)
                                    .addParams("from_user", SPUtils.getString(Constant.USERID, ""))
                                    .addParams("to_user", to_user)
                                    .build()
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onError(Call call, Exception e, int id) {
                                            if (getActivity() != null) {
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

                }).setTitle("提示").show();
            }
        });

        init();

        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getObject().getClass().equals(MarkerSign.class)) {
                    //是自定义marker
                    final MarkerSign sign = (MarkerSign) marker.getObject();

                    dataLin.setVisibility(View.VISIBLE);
                    Glide.with(getActivity()).load(sign.getUrl()).into(civ_avatar);
                    to_user = sign.getUserid();
                    user_name = sign.getName();
                    address.setText(sign.getAddress());

                    OkHttpUtils
                            .get()
                            .url(HttpUrls.GETDATASIX)
                            .addParams("user_number", sign.getUserid())
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String message = jsonObject.optString("message");
                                        JSONArray array = null;
                                        if (message.equals("请求成功！")) {
                                            array = jsonObject.optJSONArray("list");
                                            urlList = new ArrayList<>();
                                            for (int i = 0; i < array.length(); i++) {
                                                JSONObject user = array.getJSONObject(i);
                                                urlList.add(HOMEADDRESS+user.optString("data_image"));
                                            }
                                            adapter = new ImageAdapter(getContext(),urlList);
                                            recycler_view.setAdapter(adapter);
                                        }
//                                        float[] results=new float[1];
//                                        Location.distanceBetween(sign.getLat(),sign.getLgt(),mlat, mlgt,results);
//                                        juli.setText("距离"+results[0]+"km");
                                         juli.setText("距离"+DistanceOfTwoPoints(sign.getLat(),sign.getLgt(),mlat,mlgt)+"km");

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                            });

                }
                return false;
            }
        });

        return view;
    }

    private void search(){
        aMap.clear();
        markerSignList= new ArrayList<>();
        OkHttpUtils
                .get()
                .url(HttpUrls.GETSEARCHUSER)
                .addParams("sarch_key", search_ET.getText().toString())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        Log.e("wzj", "搜索++++++++++++++++++++++++++++: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.optString("message");
                            if (message.equals("请求成功！")) {
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
                                if (markerSignList.size()>0){
                                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(markerSignList.get(0).getLat(), markerSignList.get(0).getLgt())));
                                }
                                MarkerUtils.addCustomMarkersToMap(aMap, getContext(), markerSignList);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                });
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
                            if (message.equals("请求成功！")) {
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
                mlat = lat;
                mlgt = lgt;
                myaddress.setText(aMapLocation.getProvince()+"."+aMapLocation.getCity()+"."+aMapLocation.getDistrict());
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
