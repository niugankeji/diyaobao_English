package com.leman.diyaobao.movement;

import android.Manifest;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.leman.diyaobao.R;
import com.leman.diyaobao.activity.BaseActivity;
import com.leman.diyaobao.okhttp.HttpUrls;
import com.leman.diyaobao.utils.LogUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 创建日期：18/11/21 下午2:42
 * 描述:
 * 作者: zhaoweichuang
 */
public class SportMapActivity extends BaseActivity implements LocationSource, AMapLocationListener, InsideUpdate.UpdateNotify, View.OnClickListener {


    @BindView(R.id.panel_steps)
    ValuePanel panelSteps;
    @BindView(R.id.panel_distance)
    ValuePanel panelDistance;
    @BindView(R.id.panel_calorie)
    ValuePanel panelCalorie;
    @BindView(R.id.panel_time)
    ValuePanel panelTime;
    @BindView(R.id.panel_step_velocity)
    ValuePanel panelStepVelocity;
    @BindView(R.id.panel_velocity)
    ValuePanel panelVelocity;
    @BindView(R.id.tv_left)
    TextView tvLeft;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.area_top)
    LinearLayout areaTop;
    @BindView(R.id.map)
    MapView map;
    @BindView(R.id.map_container)
    RelativeLayout mapContainer;
    @BindView(R.id.rb_1)
    RadioButton rb1;
    @BindView(R.id.rb_2)
    RadioButton rb2;
    @BindView(R.id.area_1)
    LinearLayout area1;
    @BindView(R.id.tv_continue)
    TextView tvContinue;
    @BindView(R.id.tv_end)
    TextView tvEnd;
    @BindView(R.id.area_2)
    LinearLayout area2;
    @BindView(R.id.area_share)
    LinearLayout areaShare;
    @BindView(R.id.tv_show)
    View tvShow;
    private AMap aMap;
    private MapView mapView;
    //以前的定位点
    private LatLng oldLatLng;
    //是否是第一次定位
    private boolean isFirstLatLng = true;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    private boolean startSport;
    private List<Polyline> lines = new ArrayList<>();
    private File mFile;
    private MediaPlayer mediaPlayer;
    private long startTime;

    private int everSteps = -1;

    private TextView title;
    private LinearLayout back;


    private boolean isBind;

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        //定位
        aMap.setLocationSource(this);// 设置定位监听
//        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位 LOCATION_TYPE_LOCATE、跟随 LOCATION_TYPE_MAP_FOLLOW 或地图根据面向方向旋转 LOCATION_TYPE_MAP_ROTATE
//        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        //画线
        // 缩放级别（zoom）：地图缩放级别范围为【4-20级】，值越大地图越详细
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));

    }

    /**
     * 播放音乐
     */
    private boolean playMusic(String file) {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            AssetManager assetManager = getAssets();
            try {
                LogUtil.SHOW("A");
                AssetFileDescriptor assetFileDescriptor = assetManager.openFd(file);
                mediaPlayer.reset();
                //设置媒体播放器的数据资源
                mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
                LogUtil.SHOW("B");
                mediaPlayer.prepare();
                mediaPlayer.start();
                return true;
            } catch (IOException e) {
                LogUtil.SHOW(e.toString());
            }

        }
        return false;

    }

    /**
     * 绘制两个坐标点之间的线段,从以前位置到现在位置
     */
    private void setUpMap(LatLng oldData, LatLng newData) {
        // 绘制一个大地曲线
        lines.add(aMap.addPolyline((new PolylineOptions())
                .add(oldData, newData)
                .geodesic(true).color(Color.GREEN)));

    }


    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
//                //定位成功
                LatLng newLatLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                if (isFirstLatLng) {
                    //记录第一次的定位信息
                    oldLatLng = newLatLng;
                    isFirstLatLng = false;
                }
                //位置有变化
                if (oldLatLng != newLatLng) {
                    Log.e("Amap", amapLocation.getLatitude() + "," + amapLocation.getLongitude());
                    if (startSport) {
                        area1.setVisibility(View.GONE);
                        area2.setVisibility(View.VISIBLE);
                        tvContinue.setText("suspend");
                        setUpMap(oldLatLng, newLatLng);
                    }
                    oldLatLng = newLatLng;
                }

            } else {
                String errText = "Locate failed," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
                if (isFirstLatLng) {
                    Toast.makeText(this, "Please open location permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
        InsideUpdate.sendNotify(R.layout.activity_map);

    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            mLocationOption.setOnceLocation(false);
            /**
             * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
             * 注意：只有在高精度模式下的单次定位有效，其他方式无效
             */
            mLocationOption.setGpsFirst(true);
            // 设置发送定位请求的时间间隔,最小值为1000ms,1秒更新一次定位信息
            mLocationOption.setInterval(3000);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        InsideUpdate.removeClientNotify(this);
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        InsideUpdate.sendNotify(R.layout.frg_control);
    }

    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.rb_1:
                radioGroup.setVisibility(View.GONE);
                break;
            case R.id.rb_2:
                radioGroup.setVisibility(View.GONE);
                break;
            //开始运动
            case R.id.tv_left:
                startTime = System.currentTimeMillis();
                if (!startSport) {
                    startSport = true;
                    playMusic("start.mp3");
                    tvLeft.setText("Getting GPS signal");
                    radioGroup.setVisibility(View.GONE);
                    areaTop.setVisibility(View.VISIBLE);
                }
                break;
            //返回
            case R.id.tv_right:
                onBackPressed();
                break;
            case R.id.tv_continue:
                if (startSport) {
                    startSport = false;
                    tvContinue.setText("Continue");
                    playMusic("stop.mp3");

                } else {
                    playMusic("continue.mp3");
                    startSport = true;
                    tvContinue.setText("suspend");
                }
                break;
            //结束
            case R.id.tv_end:
                playMusic("end.mp3");
                area2.setVisibility(View.GONE);
                area1.setVisibility(View.GONE);
                if (mlocationClient != null) {
                    mlocationClient.stopLocation();
                    mlocationClient.onDestroy();
                    mlocationClient = null;
                }
                areaShare.setVisibility(View.VISIBLE);
                break;
            case R.id.area_share:


                AppPermissionUtil.requestPermission(SportMapActivity.this, new AppPermissionUtil.OnPermissionResult() {
                    @Override
                    public void onGranted(int requestCode, List<String> permissions) {
                        if (requestCode == AppPermissionUtil.CODE_WRITE_EXTERNAL_STORAGE) {
                            saveScreenMap();
                            view.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mFile != null) {
                                        AppUtils.showShareDialog(SportMapActivity.this, shareListener);
                                    } else {
                                        ToastUtil.showShortToast(getApplicationContext(), "Screen capture failed");
                                    }
                                }
                            }, 500);
                        }
                    }

                    @Override
                    public void onDenied(int requestCode, List<String> permissions) {
                        if (requestCode == AppPermissionUtil.CODE_WRITE_EXTERNAL_STORAGE) {
                            ToastUtil.showShortToast(getApplicationContext(), "To save the screen capture, you need to authorize the phone to save");
                        }
                    }

                    @Override
                    public void onAlwaysDenied(int requestCode, List<String> permissions) {
                        if (requestCode == AppPermissionUtil.CODE_WRITE_EXTERNAL_STORAGE) {
                            ToastUtil.showShortToast(getApplicationContext(), "To save the screen capture, you need to authorize the phone to save");
                        }

                    }
                }, AppPermissionUtil.CODE_WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);


                break;
            case R.id.tv_show:
//                if (TextUtils.equals(tvShow.getText().toString(), "隐藏地图")) {
//                    mapView.setVisibility(View.GONE);
//                    tvShow.setText("展示地图");
//                } else {
//                    mapView.setVisibility(View.VISIBLE);
//                    tvShow.setText("隐藏地图");
//                }
                if (mapView.getVisibility() == View.VISIBLE) {
                    mapView.setAlpha(1);
                } else {
                    mapView.setAlpha(0);

                }
                break;
        }
    }

    String content = "[Link]Healthy is fashionable, join the future walk together, record our daily walk！";
    String tile = "Walking in the future";
    View.OnClickListener shareListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lay_chat_friend:
                    sendThree(tile, content, SHARE_MEDIA.WEIXIN);
                    break;
                case R.id.lay_qq:
                    sendThree(tile, content, SHARE_MEDIA.QQ);
                    break;
                case R.id.lay_sina:
                    sendThree(tile, content, SHARE_MEDIA.SINA);
                    break;
                case R.id.lay_chat_circle:
                    sendThree(tile, content, SHARE_MEDIA.WEIXIN_CIRCLE);
                    break;
                case R.id.lay_qq_zone:
                    sendThree(tile, content, SHARE_MEDIA.QZONE);
                    break;
                case R.id.lay_link:
                    break;
            }
        }
    };

    public void sendThree(String title, String description, SHARE_MEDIA media) {
        UMImage umImage = new UMImage(SportMapActivity.this, mFile);
        new ShareAction(this)
                .withText(description)//内容
                .withMedia(umImage)
                .setPlatform(media)
                .setCallback(umShareListener)
                .share();
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            ToastUtil.showShortToast(getApplicationContext(), "Share success");
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            ToastUtil.showShortToast(getApplicationContext(), "Share failure");

        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            ToastUtil.showShortToast(getApplicationContext(), "Share cancel");
        }
    };

    private void initSportValue(int steps) {
        long endTime = System.currentTimeMillis();
        long seconds = (endTime - startTime) / 1000;
        if (steps > 0) {
            long time = seconds / 60;
            float stepVelocity = steps * 1.0f / time;//步频
            float velocity = steps * 60 / 1000.000f / time;//速度
            float calorie = 0.8f * 60.000f * steps / 1000;//体重（kg）* 距离（km）* 运动系数（k）
            panelSteps.setPanelValue(steps + "");
            //运动时间分钟
            HttpUrls.TIME = format(time);
            panelTime.setPanelValue(HttpUrls.TIME);
            //运动步频
            HttpUrls.BUPIN = format(stepVelocity);
            panelStepVelocity.setPanelValue(HttpUrls.BUPIN);
            //运动速度
            HttpUrls.SPEED = format(velocity);
            panelVelocity.setPanelValue(HttpUrls.SPEED);
            //消耗的卡路里
            HttpUrls.KALULI = format(calorie);
            panelCalorie.setPanelValue(HttpUrls.KALULI);
            //运动的公里
            HttpUrls.DISTANCE = format(steps / 1000.000f);
            panelDistance.setPanelValue(HttpUrls.DISTANCE);

            InsideUpdate.sendNotify(R.layout.frg_control);
        }
    }

    private String format(float value) {

        if (Double.isInfinite(value) && value < 0.0) {
            return "0.001";
        }
        if (Double.NEGATIVE_INFINITY == value) {
            return "0.001";
        }


        if (value < 0.001) {
            return String.valueOf("0.001");
        }


        String temp = String.valueOf(value);


        if (TextUtils.equals(temp, "Infinity")) {
            return "0.001";
        }


        if (temp.contains(".")) {
            String[] arrs = temp.split(".");
            if (arrs.length >= 2) {
                return arrs[0] + "." + (arrs[1].length() > 3 ? arrs[1].substring(0, 3) : arrs[1]);
            } else {
                return temp;
            }
        } else {
            return temp;
        }
    }

    private void saveScreenMap() {
        aMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {
            @Override
            public void onMapScreenShot(Bitmap bitmap) {

            }

            @Override
            public void onMapScreenShot(Bitmap bitmap, int status) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                if (null == bitmap) {
                    return;
                }

                bitmap = getMapAndViewScreenShot(bitmap, mapContainer, mapView, areaTop);
                try {
                    String fileName =
                            Environment.getExternalStorageDirectory() + "/aiwalk_"
                                    + sdf.format(new Date()) + ".png";
                    File file = new File(fileName);

                    FileOutputStream fos = new FileOutputStream(file);
                    boolean b = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    try {
                        fos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    StringBuffer buffer = new StringBuffer();
                    if (b)
                        buffer.append("Screen capture successful ");

                    else {
                        buffer.append("Screen capture failed ，wait a second and try again！");
                    }
//                    if (status != 0)
//                        buffer.append("地图渲染完成，截屏无网格");
//                    else {
//                        buffer.append("地图未渲染完成，截屏有网格");
//                    }
//                    ToastUtil.show(getApplicationContext(), buffer.toString());
                    mFile = file;

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    public static Bitmap getMapAndViewScreenShot(Bitmap bitmap, ViewGroup viewContainer, MapView mapView, View... views) {
        int width = viewContainer.getWidth();
        int height = viewContainer.getHeight();
        final Bitmap screenBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(screenBitmap);
        canvas.drawBitmap(bitmap, mapView.getLeft(), mapView.getTop(), null);
        for (View view : views) {
            view.setDrawingCacheEnabled(true);
            canvas.drawBitmap(view.getDrawingCache(), view.getLeft(), view.getTop(), null);
        }

        return screenBitmap;
    }

    /**
     * @param action 更新指令
     * @param value  回传值 可变参数可以不传值
     */
    @Override
    public void updateUi(int action, Object... value) {
        switch (action) {
            case R.layout.activity_amap:
                if (everSteps <= 0) {
                    everSteps = HttpUrls.STEPMS;
                } else {
                    if (startSport) {
                        initSportValue(HttpUrls.STEPMS - everSteps);
                    }
                }
                break;
        }
    }

    @Override
    public int intiLayout() {
        return R.layout.activity_amap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //    setContentView(R.layout.activity_amap);
        ButterKnife.bind(this);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        title = findViewById(R.id.title);
        title.setText("Sports map");
        back = findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvRight = findViewById(R.id.tv_right);
        tvRight.setOnClickListener(this);
        rb1 = findViewById(R.id.rb_1);
        rb1.setOnClickListener(this);
        rb2 = findViewById(R.id.rb_2);
        rb2.setOnClickListener(this);
        tvLeft = findViewById(R.id.tv_left);
        tvLeft.setOnClickListener(this);
        tvContinue = findViewById(R.id.tv_continue);
        tvContinue.setOnClickListener(this);
        tvEnd = findViewById(R.id.tv_end);
        tvEnd.setOnClickListener(this);
        areaShare = findViewById(R.id.area_share);
        areaShare.setOnClickListener(this);
        tvShow = findViewById(R.id.tv_show);
        tvShow.setOnClickListener(this);
        radioGroup = findViewById(R.id.radioGroup);
        areaTop = findViewById(R.id.area_top);

        init();
        mediaPlayer = new MediaPlayer();
        InsideUpdate.addClientNotify(this);

    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }
}
