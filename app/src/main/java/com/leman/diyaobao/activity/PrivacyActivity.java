package com.leman.diyaobao.activity;


import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leman.diyaobao.Constant;
import com.leman.diyaobao.R;
import com.leman.diyaobao.map.LocationUtil;
import com.leman.diyaobao.myview.SwitchButton;
import com.leman.diyaobao.okhttp.HttpUrls;
import com.leman.diyaobao.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * 隐私设置
 */
public class PrivacyActivity extends BaseActivity {

    private TextView title;
    private LinearLayout back;

    private SwitchButton friend;
    private SwitchButton show;

    private double MyLatitude;
    private double MyLongitude;
    private LocationUtil locationUtil;

    @Override
    public int intiLayout() {
        return R.layout.activity_privacy;
    }

    @Override
    public void initView() {
        title = findViewById(R.id.title);
        title.setText("Privacy");
        back = findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        friend = findViewById(R.id.friend);
        show = findViewById(R.id.show);
        //       show.setChecked(SPUtils.getBoolean(Constant.IS_SHOW, true));
        friend.setChecked(SPUtils.getBoolean(Constant.IS_SHOW_FRIEND, true));
        OkHttpUtils
                .get()
                .url(HttpUrls.GETPRIVACY)
                .addParams("user_number", SPUtils.getString(Constant.USERID, ""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(PrivacyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("wzj", "0000000000000: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.optString("message");
                            Log.e("wzj", "xxxxxxxxxxxxxxxxxxxxx: " + jsonObject.optString("stat"));
                            if (jsonObject.optString("stat").equals("1")) {
                                show.setChecked(false);
                            }
                            if (jsonObject.optString("stat").equals("0")) {
                                show.setChecked(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                });

    }

    @Override
    public void initData() {
        friend.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                Log.e("wzj", "推荐通讯录朋友..............： " + isChecked);
                if (isChecked) {
                    //推荐通讯录朋友
                    SPUtils.putBoolean(Constant.IS_SHOW_FRIEND, isChecked);
                } else {
                    SPUtils.putBoolean(Constant.IS_SHOW_FRIEND, isChecked);
                }
            }
        });
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("wzj", "dianji1..............： ");
            }
        });
        show.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                Log.e("wzj", "是否出现在附近人中..............： " + isChecked);
                if (isChecked) {
                    //出现在附近人中
                    OkHttpUtils
                            .post()
                            .url(HttpUrls.SHOWNEARBY)
                            .addParams("user_number", SPUtils.getString(Constant.USERID, ""))
                            .addParams("show_nearby", "0")
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    Toast.makeText(PrivacyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    Log.e("wzj", "0000000000000: " + response);
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String message = jsonObject.optString("message");
                                        Toast.makeText(PrivacyActivity.this, message, Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                            });
                } else {
                    OkHttpUtils
                            .post()
                            .url(HttpUrls.HIDDenNEARBY)
                            .addParams("user_number", SPUtils.getString(Constant.USERID, ""))
                            .addParams("show_nearby", "1")
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    Toast.makeText(PrivacyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    Log.e("wzj", "0000000000000: " + response);
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String message = jsonObject.optString("message");
                                        Toast.makeText(PrivacyActivity.this, message, Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                            });
                }
            }
        });
    }

}
