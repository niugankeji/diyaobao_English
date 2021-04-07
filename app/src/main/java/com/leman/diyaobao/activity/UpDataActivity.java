package com.leman.diyaobao.activity;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leman.diyaobao.R;
import com.leman.diyaobao.apkupdate.HProgressDialogUtils;
import com.leman.diyaobao.apkupdate.UpdateAppHttpUtil;
import com.leman.diyaobao.utils.SPUtils;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.service.DownloadService;

import java.io.File;

public class UpDataActivity extends BaseActivity {

    private TextView title;
    private LinearLayout back;

    private TextView top_title;
    private TextView message;
    private TextView updata;

    private boolean isShowDownloadProgress = true;


    @Override
    public int intiLayout() {
        return R.layout.activity_up_data;
    }

    @Override
    public void initView() {
        title = findViewById(R.id.title);
        title.setText("Version introduction");
        back = findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        top_title = findViewById(R.id.top_title);
        message = findViewById(R.id.message);
        updata = findViewById(R.id.updata);


        updata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("wzj","yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy");

                {
                    UpdateAppBean updateAppBean = new UpdateAppBean();

                    //设置 apk 的下载地址
                    updateAppBean.setApkFileUrl(SPUtils.getString("apk_address",""));

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

                    //设置apk 的保存路径
                    updateAppBean.setTargetPath(path);
                    //实现网络接口，只实现下载就可以
                    updateAppBean.setHttpManager(new UpdateAppHttpUtil());

                    UpdateAppManager.download(UpDataActivity.this, updateAppBean, new DownloadService.DownloadCallback() {
                        @Override
                        public void onStart() {
                            HProgressDialogUtils.showHorizontalProgressDialog(UpDataActivity.this, "下载进度", false);

                        }

                        @Override
                        public void onProgress(float progress, long totalSize) {
                            HProgressDialogUtils.setProgress(Math.round(progress * 100));


                        }

                        @Override
                        public void setMax(long totalSize) {

                        }

                        @Override
                        public boolean onFinish(File file) {
                            HProgressDialogUtils.cancel();

                            return true;
                        }

                        @Override
                        public void onError(String msg) {
                            HProgressDialogUtils.cancel();

                        }

                        @Override
                        public boolean onInstallAppAndAppOnForeground(File file) {

                            return false;
                        }
                    });
                }

            }
        });
        top_title.setText("V"+SPUtils.getString("apkcode","")+"版本升级内容");
        message.setText(SPUtils.getString("updata_info",""));

    }

    @Override
    public void initData() {

    }

}
