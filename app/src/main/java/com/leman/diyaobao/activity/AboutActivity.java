package com.leman.diyaobao.activity;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leman.diyaobao.R;
import com.leman.diyaobao.okhttp.HttpUrls;
import com.leman.diyaobao.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

import static com.leman.diyaobao.okhttp.HttpUrls.HOMEADDRESS;

public class AboutActivity extends BaseActivity {
    private TextView title;
    private LinearLayout back;

    private LinearLayout help;
    private LinearLayout inviteFriends;
    private LinearLayout about;
    private LinearLayout update;

    private TextView number;
    private TextView code;

    @Override
    public int intiLayout() {
        return R.layout.activity_about;
    }

    @Override
    public void initView() {
        title = findViewById(R.id.title);
        title.setText("About");
        back = findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        help = findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("title","服务条款");
                intent.setClass(AboutActivity.this, ServiceActivity.class);
                startActivity(intent);
            }
        });
        number = findViewById(R.id.number);
        number.setText("V" + getVersionName(this));
        inviteFriends = findViewById(R.id.inviteFriends);
        inviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AboutActivity.this, EmailActivity.class));
            }
        });

        about = findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutActivity.this, AboutAppActivity.class));
            }
        });
        update = findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取更新
                OkHttpUtils
                        .get()
                        .url(HttpUrls.GETAPKINFO)
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Toast.makeText(AboutActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.e("wzj", "xxxxxxxxxxxxxxxxxxxxxx: " + response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray array = jsonObject.optJSONArray("apkcode");
                                    String apkcode = array.getJSONObject(0).optString("new_version");
                                    if (getVersionName(AboutActivity.this).equals(apkcode)) {
                                        code.setText("当前版本已是最新版本");
                                    } else {
                                        startActivity(new Intent(AboutActivity.this, UpDataActivity.class));
                                    }
                                    String updata_info = array.getJSONObject(0).optString("update_log");
                                    String apk_address = array.getJSONObject(0).optString("apk_file_url");
                                    SPUtils.putString("updata_info",updata_info);
                                    SPUtils.putString("apk_address",HOMEADDRESS+apk_address);
                                    SPUtils.putString("apkcode",apkcode);
                                    Log.e("wzj","apk下载地址+++++++++++++++++++： "+HOMEADDRESS+apk_address);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                        });
            }
        });

        code = findViewById(R.id.code);
        code.setText("地遥宝 V" + getVersionName(this));
    }

    @Override
    public void initData() {

    }

    public static synchronized String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
