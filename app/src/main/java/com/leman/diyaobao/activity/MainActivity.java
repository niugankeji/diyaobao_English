package com.leman.diyaobao.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.leman.diyaobao.Constant;
import com.leman.diyaobao.R;
import com.leman.diyaobao.fragment.CameraFragment;
import com.leman.diyaobao.fragment.DataFragment;
import com.leman.diyaobao.fragment.FoundFragment2;
import com.leman.diyaobao.fragment.MeFragment;
import com.leman.diyaobao.fragment.SettingFragment;
import com.leman.diyaobao.myview.EndDialog;
import com.leman.diyaobao.okhttp.HttpUrls;
import com.leman.diyaobao.step.service.StepService;
import com.leman.diyaobao.utils.FragmentUtils;
import com.leman.diyaobao.utils.GetPermissions;
import com.leman.diyaobao.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;


import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import okhttp3.Call;

import static com.leman.diyaobao.fragment.CameraFragment.ShowMessage;
import static com.leman.diyaobao.utils.ActivityUtils.setChildState;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private CameraFragment cameraFragment;
    private DataFragment dataFragment;
    private FoundFragment2 foundFragment;
    private MeFragment meFragment;
    private SettingFragment settingFragment;


    protected static ImageView imgProtruding;

    private LinearLayout data;//数据
    private LinearLayout found;//发现
    private static LinearLayout scanning;//扫描
    private LinearLayout me;//我
    private LinearLayout settingL;//设置

    //剪切板管理工具类
    private ClipboardManager mClipboardManager;
    //剪切板Data对象
    private ClipData mClipData;

    GetPermissions getPermissions = new GetPermissions(this);
    String[] permissions = new String[]{Manifest.permission.CAMERA};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {

            getPermissions.getPression(new GetPermissions.PermissionCallback() {
                @Override
                public void success() {
                    if (cameraFragment == null) {
                        cameraFragment = new CameraFragment();
                    }
                    FragmentUtils.replceFragment(MainActivity.this, R.id.fl, cameraFragment);

                }
            });
            getPermissions.getpermissions(permissions);

            if (getResources().getString(R.string.app_name).equals("地遥宝(试用版)")) {
                if (isDateOneBigger(getTime(), SPUtils.getString(Constant.USEENDTIME, "30"))) {
                    //弹出对话框
                    new EndDialog(MainActivity.this, R.style.dialog, "您的试用时间已经结束，如需继续使用请联系:13488753990", new EndDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm, String content) {
                            if (confirm) {
                                mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                //创建一个新的文本clip对象
                                mClipData = ClipData.newPlainText("Simple test", content.split(":")[1]);
                                //把clip对象放在剪贴板中
                                mClipboardManager.setPrimaryClip(mClipData);
                                Toast.makeText(getApplicationContext(), content.split(":")[1] + "复制成功！",
                                        Toast.LENGTH_SHORT).show();
                                Log.e("wzj", "======================： " + content.split(":")[1]);
                            }

                        }

                    }).setTitle("提示").show();
                }
            }

        }
    }

    @Override
    public int intiLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {


        dataFragment = new DataFragment();
        foundFragment = new FoundFragment2();
        meFragment = new MeFragment();
        settingFragment = new SettingFragment();

        imgProtruding = (ImageView) findViewById(R.id.img_protruding);

        data = findViewById(R.id.data);
        data.setOnClickListener(this);
        found = findViewById(R.id.found);
        found.setOnClickListener(this);
        scanning = findViewById(R.id.scanning);
        scanning.setOnClickListener(this);
        me = findViewById(R.id.me);
        me.setOnClickListener(this);
        settingL = findViewById(R.id.settingL);
        settingL.setOnClickListener(this);

    }

    @Override
    public void initData() {

        SPUtils.putBoolean("isLogin", true);

        startService(new Intent(MainActivity.this, StepService.class));

        /**
         * 这个图片把radiobutton 挡住了一部分，不方便点击，所以加了一个图片的点击事件
         */
        imgProtruding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getPermissions.getPression(new GetPermissions.PermissionCallback() {
                    @Override
                    public void success() {
                        scanning.performClick();
                    }
                });
                getPermissions.getpermissions(permissions);
            }
        });


        OkHttpUtils
                .get()
                .url(HttpUrls.GETTOTALNUMBER)
                .addParams("user_id", SPUtils.getString(Constant.USERID,""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("wzj", "刷新失败---------------------： " + id);
                        Log.e("wzj", "刷新失败---------------------： " + e.getMessage());

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("wzj", "刷新成功---------------------: " + response);
                        //    setData(true, response.body().);
                        try {
                            JSONObject jsonObject = new JSONObject(response); //totalIntegral
                            SPUtils.putString(Constant.JIFEN,jsonObject.optString("total"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:

                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        //判断是否勾选禁止后不再询问
                        boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                        if (showRequestPermission) {//
                            Log.e("tag", "拒绝11111*****************");
                            return;
                        } else {

                        }
                    }
                }
                scanning.performClick();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //数据
            case R.id.data:
                cameraFragment = null;
                FragmentUtils.replceFragment(MainActivity.this, R.id.fl, dataFragment);
                imgProtruding.setEnabled(true);
                setChildState(data, false);
                setChildState(found, true);
                setChildState(scanning, true);
                setChildState(me, true);
                setChildState(settingL, true);

                break;
            //发现
            case R.id.found:
                cameraFragment = null;
                FragmentUtils.replceFragment(MainActivity.this, R.id.fl, foundFragment);
                imgProtruding.setEnabled(true);
                setChildState(data, true);
                setChildState(found, false);
                setChildState(scanning, true);
                setChildState(me, true);
                setChildState(settingL, true);

                break;
            //扫描
            case R.id.scanning:
                cameraFragment = new CameraFragment();
                ShowMessage(MainActivity.this);
                FragmentUtils.replceFragment(MainActivity.this, R.id.fl, cameraFragment);
                imgProtruding.setEnabled(false);
                setChildState(data, true);
                setChildState(found, true);
                setChildState(scanning, false);
                setChildState(me, true);
                setChildState(settingL, true);
                break;
            //我
            case R.id.me:
                cameraFragment = null;
                FragmentUtils.replceFragment(MainActivity.this, R.id.fl, meFragment);
                imgProtruding.setEnabled(true);
                setChildState(data, true);
                setChildState(found, true);
                setChildState(scanning, true);
                setChildState(me, false);
                setChildState(settingL, true);
                break;
            case R.id.settingL:
                cameraFragment = null;
                FragmentUtils.replceFragment(MainActivity.this, R.id.fl, settingFragment);
                imgProtruding.setEnabled(true);
                setChildState(data, true);
                setChildState(found, true);
                setChildState(scanning, true);
                setChildState(me, true);
                setChildState(settingL, false);
                break;
//            case R.id.img_protruding:
//
//                break;
        }
    }


    //退出按钮
    private long ExitTime = 0;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (System.currentTimeMillis() - ExitTime > 2000) {
                ExitTime = System.currentTimeMillis();
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_LONG).show();
            } else {

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                finish();
                System.exit(0);
            }
        }
        return false;
    }


    public static String getTime() {
        String str;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        str = formatter.format(curDate);
        return str;
    }

    public static boolean isDateOneBigger(String str1, String str2) {
        boolean isBigger = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date dt1 = null;
        java.util.Date dt2 = null;
        try {
            dt1 = sdf.parse(str1);
            dt2 = sdf.parse(str2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dt1.getTime() > dt2.getTime()) {
            isBigger = true;
        } else if (dt1.getTime() < dt2.getTime()) {
            isBigger = false;
        }
        return isBigger;
    }

    public static void setEnable() {
        scanning.setEnabled(true);
        imgProtruding.setEnabled(true);
    }

}
