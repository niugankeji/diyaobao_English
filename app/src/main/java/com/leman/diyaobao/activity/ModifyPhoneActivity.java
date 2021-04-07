package com.leman.diyaobao.activity;


import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leman.diyaobao.Constant;
import com.leman.diyaobao.R;
import com.leman.diyaobao.okhttp.HttpUrls;
import com.leman.diyaobao.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class ModifyPhoneActivity extends BaseActivity {

    private TextView title;
    private LinearLayout back;
    private TextView sendCode;

    private EditText phone;
    private EditText code;
    private TextView save;

    MyCountDownTimer myCountDownTimer;
    private long time = 60000;

    @Override
    public int intiLayout() {
        return R.layout.activity_modify_phone;
    }

    @Override
    public void initView() {
        title = findViewById(R.id.title);
        title.setText("Modify phone number");
        phone = findViewById(R.id.phone);
        code = findViewById(R.id.code);
        save = findViewById(R.id.save);
        back = findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sendCode = findViewById(R.id.sendCode);
        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCountDownTimer.start();
                OkHttpUtils
                        .get()
                        .url(HttpUrls.GETMSM)
                        .addParams("phone_number", phone.getText().toString())
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                //    Log.e("wzj", "1111111111111111111111: " + e.getMessage());
                                Toast.makeText(ModifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.e("wzj", "0000000000000: " + response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String message = jsonObject.optString("message");

                                    //    Log.e("wzj", "3333333333333: " + message);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                        });
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpUtils
                        .post()
                        .url(HttpUrls.MODIFYPHONE)
                        .addParams("user_phone", phone.getText().toString())
                        .addParams("user_number", SPUtils.getString(Constant.USERID, ""))
                        .addParams("code_code", code.getText().toString())
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                //    Log.e("wzj", "1111111111111111111111: " + e.getMessage());
                                Toast.makeText(ModifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.e("wzj", "0000000000000: " + response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String message = jsonObject.optString("message");
                                    Toast.makeText(ModifyPhoneActivity.this, message, Toast.LENGTH_SHORT).show();
                                    //    Log.e("wzj", "3333333333333: " + message);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                        });
            }
        });

    }

    @Override
    public void initData() {
        myCountDownTimer = new MyCountDownTimer(time, 1000);
    }

    /**
     * 发送验证码倒计时
     */
    private class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //计时过程
        @Override
        public void onTick(long l) {
            //防止计时过程中重复点击
            sendCode.setClickable(false);
            sendCode.setText(l / 1000 + "s" + "后重新获取");

        }

        //计时完毕的方法
        @Override
        public void onFinish() {
            //重新给Button设置文字
            sendCode.setText("重新获取验证码");
            //设置可点击
            sendCode.setClickable(true);
        }
    }
}
