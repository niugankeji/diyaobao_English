package com.leman.diyaobao.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.baidu.aip.imageclassify.AipImageClassify;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.leman.diyaobao.Constant;
import com.leman.diyaobao.HttpConnection;
import com.leman.diyaobao.R;
import com.leman.diyaobao.adapter.ShiBieAdapter;
import com.leman.diyaobao.dialog.LoadingDialog;
import com.leman.diyaobao.entity.DuoTu;
import com.leman.diyaobao.entity.ShiBie;
import com.leman.diyaobao.entity.ZhiWuData;
import com.leman.diyaobao.map.LocationUtil;
import com.leman.diyaobao.okhttp.HttpUrls;
import com.leman.diyaobao.shibie.util.FileUtil;
import com.leman.diyaobao.utils.ImageUtils;
import com.leman.diyaobao.utils.SPUtils;
import com.leman.diyaobao.utils.jankinDBOpenHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

import static android.content.Context.SENSOR_SERVICE;
import static com.leman.diyaobao.activity.MainActivity.setEnable;
import static com.leman.diyaobao.utils.netUtils.isNetworkConnected;


public class CameraFragment extends Fragment implements CameraBridgeViewBase.CvCameraViewListener2, View.OnClickListener {

    //加速度陀螺仪传感器
    private SensorManager sEnsor = null;
    private double Pitch = 0;
    private double Roll = 0;
    private String httpURL = HttpConnection.httpURL;
    private Mat mRgba = null;
    private Mat mGray = null;
    private int flipCode = 1;
    private boolean NET_CV_Done = true;
    private Mat mSendImg = new Mat();
    private Handler NetWorkThreadHandler = null;
    private static final int MSG_NET_CV = 100;        //在网络线程中完成图像处理，计算完成之后通过http协议向服务器发送数据
    private static final int MSG_UI_DISPLAYLAI = 101;    //显示LAI等计算结果
    private static final int MSG_UI_DISPLAYMSG = 102;    //Toast显示信息
    private static final int MSG_NET_UPTELISTVIEW = 103;
    private String[] FeatureModeStr = {"饱和度", "蓝色波段", "亮度值", "绿度指数"};
    private boolean isCalculateLAI = false;
    private MediaPlayer mMediaPlayer = null;
    private jankinDBOpenHelper Mydb = null;
    private static TextView MsgView = null;
    private JavaCameraView mOpenCvCameraView = null;
    private LoadingDialog loadingDialog;
    private LoadingDialog loadingDialog1;
    private ImageView CameraSwtBtn;
    //  private ImageView CameraActBtn;
    private RelativeLayout LinearLayout1;
    //private LocationSource.OnLocationChangedListener mListener = null;//定位监听器
    private LocationUtil locationUtil;
    private double MyLatitude;
    private double MyLongitude;
    private static ImageView health;
    private static ImageView wuhouqi;
    private static ImageView shibie;
    private static ImageView faihui;
    private static ImageView dai;
    private static ImageView duo;

    private static TextView show;
    private static LinearLayout showLL;
    private TextView name;
    private TextView code;
    private TextView message;
    private TextView info;
    private boolean isShow = false;
    private RecyclerView rv_list;
    private List<ShiBie> mDataList;
    ShiBie item;
    String zenithAngleStr;
    private int j = 0;
    private int k = 0;
    int i = 1;

    float lainumber = 0.0f;

    private boolean isUpLoad = false;
    boolean is_upload = false;
    boolean is_first = true;
    String data_id;
    private int select_number = 10;
    private String moshi = "";

    private boolean is_upload_duotu = false;

    File file;
    String file_url;

    ZhiWuData zhiWuData;


    static {
        System.loadLibrary("opencv_info");
        System.loadLibrary("opencv_java");
        System.loadLibrary("SmartLAI");
    }

    //陀螺仪角加速度计监听实现
    final SensorEventListener myAccelerometerListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent arg0) {
            // TODO Auto-generated method stub
            if (arg0.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float X_lateral = arg0.values[0];
                float Y_longitudinal = arg0.values[1];
                float Z_vertical = arg0.values[2];
                Pitch = Math.atan2(X_lateral, Z_vertical) * 180 / Math.PI;
                Roll = Math.atan2(Y_longitudinal, Z_vertical) * 180 / Math.PI;
//				Log.i("czxLiuYang","\n 翻滚角度: "+Pitch);
//				Log.i("czxLiuYang","\n 俯仰角度: "+Roll);
//				String tmp = String.valueOf(Pitch);
//				if (tmp.length() > 5) {
//					tmp = tmp.substring(0, 5);
//				}
//				String displayStr = "翻滚:" + tmp + "°\t\t\t\t俯仰:";
//				tmp = String.valueOf(Roll);
//				if (tmp.length() > 5) {
//					tmp = tmp.substring(0, 5);
//				}
//				displayStr += tmp + "°";
            }
        }

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
            // TODO Auto-generated method stub

        }

    };


    private AipImageClassify aipImageClassify;
    public static final String APP_ID = "16357611";
    public static final String API_KEY = "DXbO1LHACWxVAKfQzF0BlHqc";
    public static final String SECRET_KEY = "AjMc2TEqfG4DELrFjpwGg9Fir65pjaVA";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        MsgView = view.findViewById(R.id.MsgView);
        MsgView.setVisibility(View.GONE);
        Mydb = new jankinDBOpenHelper(getContext());

        showLL = view.findViewById(R.id.showLL);
        name = view.findViewById(R.id.name);
        code = view.findViewById(R.id.code);
        message = view.findViewById(R.id.message);

        health = view.findViewById(R.id.health);
        wuhouqi = view.findViewById(R.id.wuhouqi);
        shibie = view.findViewById(R.id.shibie);
        faihui = view.findViewById(R.id.faihui);

        show = view.findViewById(R.id.show);
        dai = view.findViewById(R.id.dai);
        duo = view.findViewById(R.id.duo);

        rv_list = view.findViewById(R.id.rv_list);
        rv_list.setLayoutManager(new LinearLayoutManager(getContext()));

        info = view.findViewById(R.id.info);

        //植被健康上报
        health.setVisibility(View.GONE);
        health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.setText("Developing");
                setEnable();
                isUpLoad = false;
                info.setVisibility(View.GONE);
                showLL.setVisibility(View.GONE);
                MsgView.setVisibility(View.GONE);
                Glide.with(getContext()).load(R.mipmap.dai).crossFade().into(dai);
                Glide.with(getContext()).load(R.mipmap.duo).crossFade().into(duo);
                Glide.with(getContext()).load(R.mipmap.health_1).crossFade().into(health);
                Glide.with(getContext()).load(R.mipmap.wuhouqi).crossFade().into(wuhouqi);
                Glide.with(getContext()).load(R.mipmap.shibie).crossFade().into(shibie);
            }
        });
        //植被物候期识别
        wuhouqi.setVisibility(View.GONE);
        wuhouqi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.setText("Developing");
                setEnable();
                info.setVisibility(View.GONE);
                showLL.setVisibility(View.GONE);
                MsgView.setVisibility(View.GONE);
                isUpLoad = false;
                Glide.with(getContext()).load(R.mipmap.dai).crossFade().into(dai);
                Glide.with(getContext()).load(R.mipmap.duo).crossFade().into(duo);
                Glide.with(getContext()).load(R.mipmap.health).crossFade().into(health);
                Glide.with(getContext()).load(R.mipmap.wuhouqi_1).crossFade().into(wuhouqi);
                Glide.with(getContext()).load(R.mipmap.shibie).crossFade().into(shibie);
            }
        });
        //植物类型识别
        shibie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEnable();
                Glide.with(getContext()).load(R.mipmap.health).crossFade().into(health);
                Glide.with(getContext()).load(R.mipmap.wuhouqi).crossFade().into(wuhouqi);
                Glide.with(getContext()).load(R.mipmap.dai).crossFade().into(dai);
                Glide.with(getContext()).load(R.mipmap.duo).crossFade().into(duo);
                Glide.with(getContext()).load(R.mipmap.shibie_1).crossFade().into(shibie);
                show.setText("");
                isShow = true;
                isUpLoad = false;
                mDataList = new ArrayList<>();
                MsgView.setVisibility(View.GONE);
                showLL.setVisibility(View.VISIBLE);

            }
        });
        //返回
        faihui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(getContext()).load(R.mipmap.health).crossFade().into(health);
                Glide.with(getContext()).load(R.mipmap.wuhouqi).crossFade().into(wuhouqi);
                Glide.with(getContext()).load(R.mipmap.shibie).crossFade().into(shibie);
                Glide.with(getContext()).load(R.mipmap.dai).crossFade().into(dai);
                Glide.with(getContext()).load(R.mipmap.duo).crossFade().into(duo);
                show.setText("");
                info.setVisibility(View.GONE);
                mDataList = new ArrayList<>();
                MsgView.setVisibility(View.GONE);
                showLL.setVisibility(View.GONE);

                isUpLoad = false;
            }
        });

        //单次模式
        dai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(getContext()).load(R.mipmap.health).crossFade().into(health);
                Glide.with(getContext()).load(R.mipmap.wuhouqi).crossFade().into(wuhouqi);
                Glide.with(getContext()).load(R.mipmap.shibie).crossFade().into(shibie);
                Glide.with(getContext()).load(R.mipmap.dai1).crossFade().into(dai);
                Glide.with(getContext()).load(R.mipmap.duo).crossFade().into(duo);
                isUpLoad = true;
                MsgView.setVisibility(View.VISIBLE);
                showLL.setVisibility(View.GONE);
                info.setVisibility(View.GONE);
                moshi = "dai";
            }
        });
        //多次模式
        duo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(getContext()).load(R.mipmap.health).crossFade().into(health);
                Glide.with(getContext()).load(R.mipmap.wuhouqi).crossFade().into(wuhouqi);
                Glide.with(getContext()).load(R.mipmap.shibie).crossFade().into(shibie);
                Glide.with(getContext()).load(R.mipmap.dai).crossFade().into(dai);
                Glide.with(getContext()).load(R.mipmap.duo1).crossFade().into(duo);
                isUpLoad = true;
                MsgView.setVisibility(View.VISIBLE);
                showLL.setVisibility(View.GONE);
                info.setVisibility(View.GONE);
                moshi = "duo";
            }
        });
        mOpenCvCameraView = view.findViewById(R.id.CameraView);
        mOpenCvCameraView.setCvCameraViewListener(this);
//        mOpenCvCameraView.setTouchFocusEnable();
//        mOpenCvCameraView.enableView();                                                                  

        CameraSwtBtn = view.findViewById(R.id.CameraSwtBtn);
        CameraSwtBtn.setOnClickListener(this);

//        CameraActBtn = view.findViewById(R.id.CameraActBtn);
//        CameraActBtn.setOnClickListener(this);

        LinearLayout1 = view.findViewById(R.id.LinearLayout1);
        mOpenCvCameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show.setText("");
                showLL.setVisibility(View.GONE);
                info.setVisibility(View.GONE);
                if (isCalculateLAI & isUpLoad) {
                    Toast.makeText(getActivity(), "upload，please wait...", Toast.LENGTH_SHORT).show();
                } else {
                    if (moshi.equals("duo")) {
                        if (is_first) {
                            if (isNetworkConnected(getContext())) {
                                OkHttpUtils
                                        .post()
                                        .url(HttpUrls.UPLOADDATA)
                                        .addParams("data_lat", "" + MyLatitude)
                                        .addParams("data_lon", "" + MyLongitude)
                                        .addParams("data_day", getTime())
                                        .addParams("data_address", SPUtils.getString(Constant.ERAEADDESS, ""))
                                        .addParams("user_number", SPUtils.getString(Constant.USERID, ""))
                                        .addParams("moshi", "duo")
                                        .addParams("cishu", "0")
                                        .addParams("data_name", SPUtils.getString(Constant.QUADRAT, "JanKin"))
                                        .build()
                                        .execute(new StringCallback() {
                                            @Override
                                            public void onError(Call call, Exception e, int id) {

                                            }

                                            @Override
                                            public void onResponse(String response, int id) {
                                                try {
                                                    JSONObject object = new JSONObject(response);
                                                    data_id = object.optString("id");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                is_upload = true;
                                                is_first = false;
                                                isCalculateLAI = true;
                                                isUpLoad = true;
                                                Log.e("wzj", "返回结果222222222222222222222222222： " + response);
                                            }
                                        });
                            } else {
                                is_upload = true;
                                is_first = false;
                                isCalculateLAI = true;
                                isUpLoad = true;
                            }

                        } else {
                            isCalculateLAI = true;
                        }

                    } else if (moshi.equals("dai")) {
                        isCalculateLAI = true;
                    }
                }
            }
        });


        NetWorkThreadStart();


        //传感器初始化
        sEnsor = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        int sensorType = Sensor.TYPE_ACCELEROMETER;
        sEnsor.registerListener(myAccelerometerListener, sEnsor.getDefaultSensor(sensorType), SensorManager.SENSOR_DELAY_NORMAL);

        init();

        if (SPUtils.getBoolean("isShow", true)) {
            //出现在附近人中
        }

        select_number = SPUtils.getInt(Constant.SELECTNUMBER, select_number);

        return view;
    }


    private void init() {
        setLocationCallBack();
        locationUtil.startLocate(getContext());
    }

    private void setLocationCallBack() {
        locationUtil = new LocationUtil();
        locationUtil.setLocationCallBack(new LocationUtil.ILocationCallBack() {
            @Override
            public void callBack(String eare, String str, double lat, double lgt, AMapLocation aMapLocation) {
                SPUtils.putString(Constant.ADDESS, str);
                SPUtils.putString(Constant.ERAEADDESS, eare);
                MyLatitude = lat;
                MyLongitude = lgt;
                //上传用户位置

                if (isNetworkConnected(getContext())) {
                    OkHttpUtils
                            .post()
                            .url(HttpUrls.UPDOLADUSERADDRESS)
                            .addParams("user_number", SPUtils.getString(Constant.USERID, ""))
                            .addParams("user_lon", MyLongitude + "")
                            .addParams("user_lat", MyLatitude + "")
                            .addParams("user_address", SPUtils.getString(Constant.ERAEADDESS, ""))
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

                                }
                            });
                }
            }
        });
    }


    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        // TODO Auto-generated method stub


        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        Core.transpose(mRgba, mRgba);
        Core.flip(mRgba, mRgba, flipCode);

        if (NET_CV_Done) {
            NET_CV_Done = false;
            Imgproc.cvtColor(mRgba, mSendImg, Imgproc.COLOR_RGBA2RGB);
            NetWorkThreadHandler.obtainMessage(MSG_NET_CV).sendToTarget();

        }

        return mRgba;
    }

    //网络线程启动
    public void NetWorkThreadStart() {
        if (!NetWorkThread.isAlive()) {
            NetWorkThread.start();
        }
    }

    //网络线程
    private Thread NetWorkThread = new Thread(new Runnable() {
        @SuppressLint("HandlerLeak")
        @Override
        public void run() {
            // TODO Auto-generated method stub
            Looper.prepare();
            NetWorkThreadHandler = new Handler() {
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case MSG_NET_CV:    //在这里完成图像处理算法，然后将结果提交到服务器
                            double zenithAngle = 180 - Roll;
                            if (zenithAngle < 90) {

                            } else if (zenithAngle < 180) {
                                zenithAngle = 180 - zenithAngle;
                            } else if (zenithAngle < 270) {
                                zenithAngle = zenithAngle - 180;
                            } else {
                                zenithAngle = 360 - zenithAngle;
                            }

                            int max = mSendImg.height();
                            if (max < mSendImg.width()) {
                                max = mSendImg.width();
                            }
                            float scale = 960.0f / max;
                            Size dsize = new Size(mSendImg.cols() * scale, mSendImg.rows() * scale);
                            Imgproc.resize(mSendImg, mSendImg, dsize, 0, 0, Imgproc.INTER_LINEAR);
                            final int[] num = new int[4];
                            final String munsell_color = Process(mSendImg.nativeObj, SPUtils.getInt("FeatureMode", 2), num);
                            double duty = 1.0 - ((double) num[0]) / (mSendImg.cols() * mSendImg.rows());
                            //textView.setText("植被比例是：" + (duty*100+"").substring(0, 5) + "%");
                            if (duty <= 0)
                                duty = Double.MIN_VALUE;
                            double LAI = -Math.log(duty) * Math.cos(zenithAngle * Math.PI / 180.0) / SPUtils.getFloat("leafArea", 0.5f);
                            //textView.append("\n叶面积指数LAI：" + (LAI+"").substring(0, 7));

                            zenithAngleStr = "" + zenithAngle;
                            if (zenithAngleStr.length() > 5) {
                                zenithAngleStr = zenithAngleStr.substring(0, 5);
                            }
                            String LAIStr = "" + LAI;
                            if (LAIStr.length() > 5) {
                                LAIStr = LAIStr.substring(0, 5);
                            }
                            String color = "\nColor:" + num[1] + "|" + num[2] + "|" + num[3] + "\nMunsell:" + munsell_color;
                            String Infostr = "Zenith:" + zenithAngleStr + "\nLAI:" + LAIStr + color;
                            UIHandler.obtainMessage(MSG_UI_DISPLAYLAI, Infostr).sendToTarget();
                            if (isCalculateLAI & isUpLoad)    //保存本次的计算结果
                            {
                                AudioMsgPlay(R.raw.ding);
                                if (storageImg(mSendImg)) {

                                    if (moshi.equals("dai")) {
                                        Log.e("wzj", "样方名称+++++++++++++++++++++： " + SPUtils.getString(Constant.QUADRAT, "JanKin"));
                                        if (isNetworkConnected(getActivity())) {
                                            try {
                                                final String finalLAIStr = LAIStr;
                                                final double finalDuty = duty;
                                                OkHttpUtils
                                                        .post()
                                                        .url(HttpUrls.UPLOADDATA)
                                                        .addFile("data_image", "messenger_01.png", saveFile(mCacheBitmap, CurrentTime))
                                                        .addParams("data_lat", "" + MyLatitude)
                                                        .addParams("data_lon", "" + MyLongitude)
                                                        .addParams("data_day", getTime())
                                                        .addParams("data_address", SPUtils.getString(Constant.ERAEADDESS, ""))
                                                        .addParams("user_number", SPUtils.getString(Constant.USERID, ""))
                                                        .addParams("data_duty", "" + duty)
                                                        .addParams("data_lai", LAIStr)
                                                        .addParams("data_model", FeatureModeStr[SPUtils.getInt(Constant.FEATUREMODE, 2) - 1])
                                                        .addParams("data_cost", "" + SPUtils.getFloat(Constant.DATAVALUES, (float) 2))
                                                        .addParams("data_munsell", munsell_color)
                                                        .addParams("data_color", num[1] + "|" + num[2] + "|" + num[3])
                                                        .addParams("data_angle", zenithAngleStr)
                                                        .addParams("moshi", "dai")
                                                        .addParams("data_name", SPUtils.getString(Constant.QUADRAT, "JanKin"))
                                                        .build()
                                                        .execute(new StringCallback() {
                                                            @Override
                                                            public void onError(Call call, Exception e, int id) {
                                                                if (getActivity() != null) {
                                                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                                Log.e("wzj", "样方名称+++++++++++++++++++++： " + e.getMessage());

                                                            }

                                                            @Override
                                                            public void onResponse(String response, int id) {
                                                                try {
                                                                    JSONObject jsonObject = new JSONObject(response);
                                                                    String message = jsonObject.optString("message");
                                                                    if (message.equals("Upload successfully！")) {
                                                                    } else {
                                                                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                                                    }
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }

                                                        });
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }


                                        } else {
                                            //保存到数据库
                                            ZhiWuData data = new ZhiWuData();
                                            try {
                                                data.setData_image(saveFile(mCacheBitmap, CurrentTime));
                                                data.setData_lat("" + MyLatitude);
                                                data.setData_lon("" + MyLongitude);
                                                data.setData_day(getTime());
                                                data.setData_address(SPUtils.getString(Constant.ERAEADDESS, ""));
                                                data.setUser_number(SPUtils.getString(Constant.USERID, ""));
                                                data.setData_duty("" + duty);
                                                data.setData_lai(LAIStr);
                                                data.setData_model(FeatureModeStr[SPUtils.getInt(Constant.FEATUREMODE, 2) - 1]);
                                                data.setData_cost("" + SPUtils.getFloat(Constant.DATAVALUES, (float) 2));
                                                data.setData_munsell(munsell_color);
                                                data.setImage_url(StringFile(mCacheBitmap, CurrentTime));
                                                data.setData_color(num[1] + "|" + num[2] + "|" + num[3]);
                                                data.setData_angle(zenithAngleStr);
                                                data.setMoshi("dai");
                                                data.setName(SPUtils.getString(Constant.QUADRAT, "JanKin"));
                                                data.save();

                                                Log.e("wzj", "保存数据库成功++++++++++++++");

                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                        }

                                    }
                                    if (moshi.equals("duo")) {
                                        Log.e("wzj", "iiiiiii=================: " + i);
                                        Log.e("wzj", "SELECTNUMBER=================: " + SPUtils.getInt(Constant.SELECTNUMBER, 10));

                                        if (!isNetworkConnected(getActivity())) {
                                            zhiWuData = new ZhiWuData();
                                            List<ZhiWuData> list = DataSupport.findAll(ZhiWuData.class);
                                            zhiWuData.setZhi_id(list.size() + 1);
                                            Log.e("wzj", "xxxxxxxxxx*******************iiiiiii=================: " + zhiWuData.getZhi_id());
                                        }


                                        if (i <= SPUtils.getInt(Constant.SELECTNUMBER, 10)) {
                                            try {
                                                file = saveFile(mCacheBitmap, CurrentTime);
                                                file_url = StringFile(mCacheBitmap, CurrentTime);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            if (isNetworkConnected(getActivity()) & is_upload) {
                                                try {
                                                    final String finalLAIStr = LAIStr;
                                                    final double finalDuty = duty;
                                                    lainumber = lainumber + Float.parseFloat(LAIStr);
                                                    OkHttpUtils
                                                            .post()
                                                            .url(HttpUrls.UPLOADDATA)
                                                            .addFile("data_image", "messenger_01.png", saveFile(mCacheBitmap, CurrentTime))
                                                            .addParams("data_lat", "" + MyLatitude)
                                                            .addParams("data_lon", "" + MyLongitude)
                                                            .addParams("data_day", getTime())
                                                            .addParams("data_address", SPUtils.getString(Constant.ERAEADDESS, ""))
                                                            .addParams("user_number", SPUtils.getString(Constant.USERID, ""))
                                                            .addParams("data_duty", "" + duty)
                                                            .addParams("data_lai", LAIStr)
                                                            .addParams("data_model", FeatureModeStr[SPUtils.getInt(Constant.FEATUREMODE, 2) - 1])
                                                            .addParams("data_cost", "" + SPUtils.getFloat(Constant.DATAVALUES, (float) 2))
                                                            .addParams("data_munsell", munsell_color)
                                                            .addParams("data_color", num[1] + "|" + num[2] + "|" + num[3])
                                                            .addParams("data_angle", zenithAngleStr)
                                                            .addParams("moshi", "duo")
                                                            .addParams("id", data_id)
                                                            .addParams("data_name", SPUtils.getString(Constant.QUADRAT, "JanKin"))
                                                            .addParams("cishu", i + "")
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

                                                                    try {
                                                                        JSONObject jsonObject = new JSONObject(response);
                                                                        String message = jsonObject.optString("message");
                                                                        if (message.equals("Upload successful！")) {
                                                                            Toast.makeText(getContext(), "第" + i + "次上传成功", Toast.LENGTH_LONG).show();
                                                                            isUpLoad = true;
                                                                            i++;
                                                                            Log.e("wzj", "提交成功=================: " + (i - 1));
                                                                        } else {
                                                                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                                                        }
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }

                                                            });
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }


                                            } else {
                                                //保存到数据库
                                                lainumber = lainumber + Float.parseFloat(LAIStr);
                                                try {
                                                    DuoTu data = new DuoTu();
                                                    data.setData_image(saveFile(mCacheBitmap, CurrentTime));
                                                    data.setData_lat("" + MyLatitude);
                                                    data.setData_lon("" + MyLongitude);
                                                    data.setData_day(getTime());
                                                    data.setData_address(SPUtils.getString(Constant.ERAEADDESS, ""));
                                                    data.setUser_number(SPUtils.getString(Constant.USERID, ""));
                                                    data.setData_duty("" + duty);
                                                    data.setData_lai(LAIStr);
                                                    data.setData_model(FeatureModeStr[SPUtils.getInt(Constant.FEATUREMODE, 2) - 1]);
                                                    data.setData_cost("" + SPUtils.getFloat(Constant.DATAVALUES, (float) 2));
                                                    data.setData_munsell(munsell_color);
                                                    data.setImage_url(StringFile(mCacheBitmap, CurrentTime));
                                                    data.setData_color(num[1] + "|" + num[2] + "|" + num[3]);
                                                    data.setData_angle(zenithAngleStr);
                                                    data.setMoshi("duo");
                                                    data.setCishu(i + "");
                                                    data.setName(SPUtils.getString(Constant.QUADRAT, "JanKin"));
                                                    data.setZhiwudata_id(zhiWuData.getZhi_id());
                                                    data.save();
                                                    Log.e("wzj", "id**************************:  " + zhiWuData.getZhi_id());
                                                    zhiWuData.getDuotu().add(data);
                                                    Toast.makeText(getContext(), "第" + i + "次保存成功", Toast.LENGTH_SHORT).show();
                                                    Log.e("wzj", "保存数据库成功++++++++++++++:   第 " + i + "次保存成功");
                                                    isUpLoad = true;
                                                    i++;


                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                            }

                                        } else {
                                            Log.e("wzj", "数据id ================此次连续测量结束： " + data_id);
                                            Toast.makeText(getContext(), "此次连续测量结束", Toast.LENGTH_SHORT).show();
                                            isCalculateLAI = false;
                                            is_upload = false;
                                            isUpLoad = false;
                                            is_first = true;
                                            i = 1;
                                            float lai = lainumber / SPUtils.getInt(Constant.SELECTNUMBER, 10);
                                            //上传平均数据=====================================================================================
                                            if (isNetworkConnected(getActivity())) {
                                                //上传到服务器

                                                OkHttpUtils
                                                        .post()
                                                        .url(HttpUrls.UPLOADDATA)
                                                        .addParams("id", data_id)
                                                        .addParams("cishu", "last")
                                                        .addParams("data_ping_lai", lai + "")
                                                        .addParams("moshi", "duo")
                                                        .addParams("user_number", SPUtils.getString(Constant.USERID, ""))
                                                        .addFile("data_image", "messenger_01.png", file)
                                                        .build()
                                                        .execute(new StringCallback() {
                                                            @Override
                                                            public void onError(Call call, Exception e, int id) {
                                                                Log.e("wzj", "返回结果222222222222222222： " + e.getMessage());
                                                            }

                                                            @Override
                                                            public void onResponse(String response, int id) {
                                                                try {
                                                                    JSONObject object = new JSONObject(response);
                                                                    data_id = object.optString("id");
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                                Log.e("wzj", "返回结果11111111111111111111： " + response);
                                                            }
                                                        });
                                            } else {
                                                //保存到数据库
                                                Log.e("wzj", "数据id ================： " + data_id);
                                                Log.e("wzj", "数据data_ping_lai ================： " + lai);
                                                Log.e("wzj", "数据data_image ================： " + file_url);
                                                zhiWuData.setCishu("last");
                                                zhiWuData.setMoshi("duo");
                                                zhiWuData.setData_ping_lai(lai + "");
                                                zhiWuData.setUser_number(SPUtils.getString(Constant.USERID, ""));
                                                zhiWuData.setImage_url(file_url);
                                                zhiWuData.setData_lon(MyLongitude + "");
                                                zhiWuData.setData_lat(MyLatitude + "");
                                                zhiWuData.setData_day(getTime());
                                                zhiWuData.setData_address(SPUtils.getString(Constant.ERAEADDESS, ""));
                                                zhiWuData.save();
                                                Log.e("wzj", "idxxxxxxxx**************************:  " + zhiWuData.getZhi_id());
                                                Log.e("wzj", "此次连续测量结束 ================： ");
                                                //    Toast.makeText(getContext(), "此次连续测量结束", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    }

                                } else {
                                    UIHandler.obtainMessage(MSG_UI_DISPLAYMSG, "保存数据失败").sendToTarget();
                                }
                                isCalculateLAI = false;
                            }

                            if (isShow) {
                                if (storageImg(mSendImg)) {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                                    CurrentTime = sdf.format(new Date(System.currentTimeMillis()));
                                    String filePath = null;
                                    try {
                                        filePath = StringFile(mCacheBitmap, CurrentTime);
                                        byte[] imgData = FileUtil.readFileByBytes(filePath);
                                        aipImageClassify = new AipImageClassify(APP_ID, API_KEY, SECRET_KEY);
                                        aipImageClassify.setConnectionTimeoutInMillis(2000);
                                        aipImageClassify.setSocketTimeoutInMillis(6000);
                                        HashMap<String, String> options = new HashMap<String, String>();
                                        options.put("baike_num", "5");
                                        JSONObject res = aipImageClassify.plantDetect(imgData, options);
                                        try {
                                            Log.e("wzj", "+++++++++++++++++++++++:  " + res.toString(2));
                                            Message message = handler.obtainMessage();
                                            message.what = 0;
                                            message.obj = res;
                                            handler.sendMessage(message);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }


                                } else {
                                    UIHandler.obtainMessage(MSG_UI_DISPLAYMSG, "数据获取失败").sendToTarget();
                                }
                                isShow = false;
                            }

                            mSendImg.release();
                            NET_CV_Done = true;
                            break;

                        default:

                            break;
                    }
                }
            };
            Looper.loop();
        }
    });

    @SuppressLint("HandlerLeak")
    private Handler UIHandler = new Handler() {
        public void handleMessage(Message msg) {
            //此方法在ui线程运行
            switch (msg.what) {

                case MSG_UI_DISPLAYLAI:
                    MsgView.setText((String) msg.obj);
                    break;
                case MSG_UI_DISPLAYMSG:
                    Toast.makeText(getContext(), (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(getContext(), "未知触发", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    private Bitmap mCacheBitmap = null;
    private String fileName = null;
    private String CurrentTime = null;
    private String SampleFolder = "北师大";

    //存储照片
    public boolean storageImg(Mat src) {

        if (mCacheBitmap == null) {
            mCacheBitmap = Bitmap.createBitmap(src.width(), src.height(), Bitmap.Config.ARGB_8888);
        } else if (mCacheBitmap.getWidth() != src.width() || mCacheBitmap.getHeight() != src.height()) {
            mCacheBitmap.recycle();
            mCacheBitmap = Bitmap.createBitmap(src.width(), src.height(), Bitmap.Config.ARGB_8888);
        }
        Utils.matToBitmap(src, mCacheBitmap);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        CurrentTime = sdf.format(new Date(System.currentTimeMillis()));
        fileName = HttpConnection.saveBitmap(mCacheBitmap, "LAISmart/" + SampleFolder + "/" + CurrentTime + ".jpg");

        if (fileName == null)
            return false;
        else
            return true;
    }


    private int previousAudioId = -1;

    private void AudioMsgPlay(int AudioId) {
        if (AudioId < 0) {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            previousAudioId = AudioId;
            return;
        }

        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(getActivity(), AudioId);
            mMediaPlayer.start();
        } else {
            if (previousAudioId == AudioId) {
                if (!mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
                }
            } else {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
                mMediaPlayer.release();
                mMediaPlayer = MediaPlayer.create(getActivity(), AudioId);
                mMediaPlayer.start();
            }
        }
        previousAudioId = AudioId;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.CameraSwtBtn:
                mOpenCvCameraView.disableView();
                if (mOpenCvCameraView.getCameraIndex() == JavaCameraView.CAMERA_ID_BACK) {
                    flipCode = -1;
                    mOpenCvCameraView.setCameraIndex(JavaCameraView.CAMERA_ID_FRONT);
                } else if (mOpenCvCameraView.getCameraIndex() == JavaCameraView.CAMERA_ID_FRONT) {
                    flipCode = 1;
                    mOpenCvCameraView.setCameraIndex(JavaCameraView.CAMERA_ID_BACK);
                } else {

                }
                mOpenCvCameraView.enableView();
                break;
            case R.id.LinearLayout1:

                break;
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        sEnsor.unregisterListener(myAccelerometerListener);
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
        LocationUtil.stopLocation();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        sEnsor.unregisterListener(myAccelerometerListener);

    }


    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        sEnsor.registerListener(myAccelerometerListener, sEnsor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);


        //把数据库中的数据上传到服务器中
        if (isNetworkConnected(getActivity())) {
            final List<ZhiWuData> list = DataSupport.findAll(ZhiWuData.class);

            if (list.size() > 0) {
                loadingDialog1 = new LoadingDialog(getContext(), "正在备份数据到服务器...", R.mipmap.ic_dialog_loading);
                loadingDialog1.show();
                upLoad(list, 0);
            }

        }
    }

    private void uploadduotu(final List<DuoTu> list, int i, final String data_id, final List<ZhiWuData> zhiWuDataList) {

        k = i;
        OkHttpUtils
                .post()
                .url(HttpUrls.UPLOADDATA)
                .addFile("data_image", "messenger_01.png", ImageUtils.getimage(list.get(k).getImage_url()))
                .addParams("data_lat", list.get(k).getData_lat())
                .addParams("data_lon", list.get(k).getData_lon())
                .addParams("data_day", list.get(k).getData_day())
                .addParams("data_address", list.get(k).getData_address())
                .addParams("user_number", list.get(k).getUser_number())
                .addParams("data_duty", list.get(k).getData_duty())
                .addParams("data_lai", list.get(k).getData_lai())
                .addParams("data_model", list.get(k).getData_model())
                .addParams("data_cost", list.get(k).getData_cost())
                .addParams("data_munsell", list.get(k).getData_munsell())
                .addParams("data_color", list.get(k).getData_color())
                .addParams("data_angle", list.get(k).getData_angle())
                .addParams("moshi", "duo")
                .addParams("id", data_id)
                .addParams("cishu", k + "")
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

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.optString("message");
                            if (message.equals("Upload successful！")) {
                                Log.e("wzj", "11111111111&&&&&&&&&&&&&&&&备份到数据库content成功====================： " + k);
                                Log.e("wzj", "22222222222&&&&&&&&&&&&&&&&备份到数据库content成功====================： " + list.size());
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(1000);
                                            Looper.prepare();
                                            if (k < list.size()) {
                                                uploadduotu(list, k, data_id, zhiWuDataList);
                                            } else {
                                                upLoad(zhiWuDataList, j);
                                                DataSupport.deleteAll(DuoTu.class, "zhiwudata_id = ?", String.valueOf(list.get(0).getZhiwudata_id()));
                                                DataSupport.deleteAll(ZhiWuData.class, "zhi_id = ?", String.valueOf(list.get(0).getZhiwudata_id()));
                                            }
                                            Looper.loop();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                                k++;
                            } else {
                                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });
    }

    private void upLoad(final List<ZhiWuData> list, int i) {

        j = i;
        Log.e("wzj", "ssssss备份到数据库content+++++++++++++++++++++++++++++++++++++++++++++++++++++++： " + j);
        Log.e("wzj", "ssssss备份到数据库content+++++++++++++++++++++++++++++++++++++++++++++++++++++++： " + list.size());
        if (j == list.size()) {
            loadingDialog1.dismiss();
            return;
        }
        Log.e("wzj", "ssssss备份到数据库content+++++++++++++++++++++++++++++++++++++++++++++++++++++++： " + list.get(j).getMoshi());
        if (list.get(j).getMoshi().equals("dai")) {
            OkHttpUtils
                    .post()
                    .url(HttpUrls.UPLOADDATA)
                    .addFile("data_image", "messenger_01.png", ImageUtils.getimage(list.get(j).getImage_url()))
                    .addParams("data_lat", list.get(j).getData_lat())
                    .addParams("data_lon", list.get(j).getData_lon())
                    .addParams("data_day", list.get(j).getData_day())
                    .addParams("data_address", list.get(j).getData_address())
                    .addParams("user_number", list.get(j).getUser_number())
                    .addParams("data_duty", list.get(j).getData_duty())
                    .addParams("data_lai", list.get(j).getData_lai())
                    .addParams("data_model", list.get(j).getData_model())
                    .addParams("data_cost", list.get(j).getData_cost())
                    .addParams("data_munsell", list.get(j).getData_munsell())
                    .addParams("data_color", list.get(j).getData_color())
                    .addParams("data_angle", list.get(j).getData_angle())
                    .addParams("data_name", list.get(j).getName())
                    .addParams("moshi", "dai")
                    .build()
                    .execute(new StringCallback() {

                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Log.e("wzj", "备份到数据库content失败====================： " + e.getMessage());
                        }

                        @Override
                        public void onResponse(String response, int id) {

                            DataSupport.deleteAll(ZhiWuData.class, "image_url = ?", list.get(j).getImage_url());
                            if (DataSupport.findAll(ZhiWuData.class).size() == 0) {
                                loadingDialog1.dismiss();
                                return;
                            }
                            Log.e("wzj", "备份到数据库content成功====================： " + list.get(j).getImage_url());
                            j++;
                            Log.e("wzj", "备份到数据库content成功====================： " + j);
                            Log.e("wzj", "备份到数据库content成功====================： " + list.size());
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(1000);
                                        Looper.prepare();
                                        if (j < list.size()) {
                                            upLoad(list, j);
                                        } else {

                                        }
                                        Looper.loop();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();


                        }

                    });
        } else if (list.get(j).getMoshi().equals("duo")) {
            Log.e("wzj", "图片-------------------，： " + list.get(j).getZhi_id());
            OkHttpUtils
                    .post()
                    .url(HttpUrls.UPLOADDATA)
                    .addParams("data_lat", list.get(j).getData_lat())
                    .addParams("data_lon", list.get(j).getData_lon())
                    .addParams("data_day", list.get(j).getData_day())
                    .addParams("data_name", list.get(j).getName())
                    .addParams("data_address", list.get(j).getData_address())
                    .addParams("user_number", list.get(j).getUser_number())
                    .addParams("moshi", "beidi")
                    .addParams("data_ping_lai", list.get(j).getData_ping_lai())
                    .addFile("data_image", "messenger_01.png", ImageUtils.getimage(list.get(j).getImage_url()))
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(String response, int id) {
                            try {
                                JSONObject object = new JSONObject(response);
                                data_id = object.optString("id");
                                //is_upload_duotu;
                                Log.e("wzj", "1111111++++++主主主主主主主+++++++备份到数据库content成功====================： " + j);
                                Log.e("wzj", "2222222++++++++主主主主主主主++++++备份到数据库content成功====================： " + list.size());

                                if (j < list.size()) {
                                    List<DuoTu> list1 = DataSupport.where("zhiwudata_id=?", String.valueOf(list.get(j).getZhi_id())).find(DuoTu.class);
                                    uploadduotu(list1, j, data_id, list);
                                } else {

                                }
                                j++;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.e("wzj", "返回结果88888888888888： " + response);
                        }
                    });
        }


    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        mOpenCvCameraView.enableView();
    }


    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        mOpenCvCameraView.disableView();
    }


    public static native String Process(long matAddrRgba, int FeatureMode, int[] numOfbackground);

    public static File saveFile(Bitmap bm, String CurrentTime) throws IOException {
        String fileDir = Environment.getExternalStorageDirectory().getPath();
        String path = fileDir + "/diyaobao/";
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File myCaptureFile = new File(path + CurrentTime + ".jpg");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        File imageFiale = ImageUtils.getimage(myCaptureFile.getAbsolutePath());
        return imageFiale;
    }


    public String StringFile(Bitmap bm, String CurrentTime) throws IOException {
        String fileDir = Environment.getExternalStorageDirectory().getPath();
        String path = fileDir + "/diyaobao/";
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }

        File myCaptureFile = new File(path + CurrentTime + ".jpg");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        return myCaptureFile.getAbsolutePath();

    }

    public static String getTime() {
        String str;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        str = formatter.format(curDate);
        return str;
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    JSONObject jsonObject = null;
                    jsonObject = (JSONObject) msg.obj;
                    Log.e("wzj", "xxxxxxxxxxxxxxxxxx: " + jsonObject);
                    JSONArray jsonArray = jsonObject.optJSONArray("result");
                    item = new ShiBie();
                    JSONObject object = jsonArray.optJSONObject(0);
                    item.setName(object.optString("name"));
                    item.setScore(object.optString("score"));
                    final JSONObject object1 = object.optJSONObject("baike_info");
                    mDataList.add(item);
                    item = new ShiBie();
                    item.setName("Click to see details");
                    item.setScore("123");
                    mDataList.add(item);

                    //植物识别详情
                    ShiBieAdapter homeAdapter = new ShiBieAdapter(R.layout.shibie_item, mDataList);
                    homeAdapter.openLoadAnimation();
                    rv_list.setAdapter(homeAdapter);

                    homeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            Log.e("wzj", "dddddddddddddddddddddddddddd: " + object1.optString("description"));
                            info.setText(object1.optString("description"));
                            info.setVisibility(View.VISIBLE);
                        }
                    });
                    break;
            }
        }
    };

    public static void ShowMessage(Context context) {
        MsgView.setVisibility(View.VISIBLE);
        showLL.setVisibility(View.GONE);
        show.setText("");
        Glide.with(context).load(R.mipmap.shibie).crossFade().into(shibie);
        Glide.with(context).load(R.mipmap.health).crossFade().into(health);
        Glide.with(context).load(R.mipmap.wuhouqi).crossFade().into(wuhouqi);
    }

}


