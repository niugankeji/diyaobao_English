package com.leman.diyaobao.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.leman.diyaobao.R;
import com.leman.diyaobao.apkupdate.HProgressDialogUtils;
import com.leman.diyaobao.apkupdate.UpdateAppHttpUtil;
import com.leman.diyaobao.dialog.ApkDialog;
import com.leman.diyaobao.okhttp.HttpUrls;
import com.leman.diyaobao.utils.GetPermissions;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.service.DownloadService;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


import static com.leman.diyaobao.okhttp.HttpUrls.HOMEADDRESS;
import static com.leman.diyaobao.utils.netUtils.isNetworkConnected;
import static com.vector.update_app.utils.AppUpdateUtils.getVersionName;

public class WelcomeActivity extends AppCompatActivity {

    GetPermissions getPermissions = new GetPermissions(this);
    String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x????????????????????????????????????????????????????????????????????????????????????
                Window window = getWindow();
                View decorView = window.getDecorView();
                //?????? flag ??????????????????????????????????????????????????????????????????????????????
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //????????????????????????????????????
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
//                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }

        getPermissions.getPression(new GetPermissions.PermissionCallback() {
            @Override
            public void success() {
                if (isNetworkConnected(WelcomeActivity.this)) {
                    updata();
                } else {
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                }
            }
        });
        getPermissions.getpermissions(permissions);


    }


    private void updata() {
        //????????????
        OkHttpUtils
                .get()
                .url(HttpUrls.GETAPKINFO)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.e("wzj", "xxxxxxxxxxxxxxxxxxxxxx3333333333: " + e.getMessage());
                        Toast.makeText(WelcomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("wzj", "xxxxxxxxxxxxxxxxxxxxxx: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray array = jsonObject.optJSONArray("apkcode");
                            String apkcode = array.getJSONObject(0).optString("new_version");
                            String updata_info = array.getJSONObject(0).optString("update_log");
                            final String apk_address = HOMEADDRESS + array.getJSONObject(0).optString("apk_file_url");
                            Log.e("wzj", "address++++++++++++++: " + apk_address);
                            if (getVersionName(WelcomeActivity.this).equals(apkcode)) {
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        try {
                                            Thread.sleep(3000);//??????3???
                                            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                                            finish();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                            } else {
                                new ApkDialog(WelcomeActivity.this, R.style.dialog, "???????????????" + apkcode + "?????????", updata_info, new ApkDialog.OnCloseListener() {
                                    @Override
                                    public void onClick() {
                                        UpdateAppBean updateAppBean = new UpdateAppBean();

                                        //?????? apk ???????????????
                                        updateAppBean.setApkFileUrl(apk_address);

                                        String path = "";
                                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
                                            try {
                                                path = getExternalCacheDir().getAbsolutePath();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            if (TextUtils.isEmpty(path)) {
                                                path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                                            }
                                        } else {
                                            path = getCacheDir().getAbsolutePath();
                                        }

                                        //??????apk ???????????????
                                        updateAppBean.setTargetPath(path);
                                        //?????????????????????????????????????????????
                                        updateAppBean.setHttpManager(new UpdateAppHttpUtil());

                                        UpdateAppManager.download(WelcomeActivity.this, updateAppBean, new DownloadService.DownloadCallback() {
                                            @Override
                                            public void onStart() {
                                                HProgressDialogUtils.showHorizontalProgressDialog(WelcomeActivity.this, "????????????", false);
                                                Log.d("wzj", "onStart() called");
                                            }

                                            @Override
                                            public void onProgress(float progress, long totalSize) {
                                                HProgressDialogUtils.setProgress(Math.round(progress * 100));
                                                Log.d("wzj", "onProgress() called with: progress = [" + progress + "], totalSize = [" + totalSize + "]");

                                            }

                                            @Override
                                            public void setMax(long totalSize) {
                                                Log.d("wzj", "setMax() called with: totalSize = [" + totalSize + "]");
                                            }

                                            @Override
                                            public boolean onFinish(File file) {
                                                HProgressDialogUtils.cancel();
                                                Log.d("wzj", "onFinish() called with: file = [" + file.getAbsolutePath() + "]");
                                                return true;
                                            }

                                            @Override
                                            public void onError(String msg) {
                                                HProgressDialogUtils.cancel();
                                                Log.e("wzj", "onError() called with: msg = [" + msg + "]");
                                            }

                                            @Override
                                            public boolean onInstallAppAndAppOnForeground(File file) {
                                                Log.d("wzj", "onInstallAppAndAppOnForeground() called with: file = [" + file + "]");
                                                return false;
                                            }
                                        });
                                    }
                                }).show();

                            }

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
                    //???????????????????????????????????????
                    boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                    if (showRequestPermission) {//
                        Log.e("tag", "??????11111*****************");
                        return;
                    } else {

                    }
                }
            }
                updata();
                break;
            default:
                break;
        }
    }

}
