package com.leman.diyaobao.activity;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class ForgetPasswordActivity extends BaseActivity {
    private TextView title;
    private LinearLayout back;
    private TextView sendCode;

    private EditText code;
    private EditText phonNumber;
    private EditText password;
    private TextView uploade;

    MyCountDownTimer myCountDownTimer;
    private long time = 60000;

    @Override
    public int intiLayout() {
        return R.layout.activity_forget_password;
    }

    @Override
    public void initView() {
        title = findViewById(R.id.title);
        title.setText("Forget the password");
        code = findViewById(R.id.code);
        phonNumber = findViewById(R.id.phonNumber);
        password = findViewById(R.id.password);
        uploade = findViewById(R.id.uploade);

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
                        .addParams("phone_number", phonNumber.getText().toString())
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                //    Log.e("wzj", "1111111111111111111111: " + e.getMessage());
                                Toast.makeText(ForgetPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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


        uploade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpUtils
                        .post()
                        .url(HttpUrls.MODIFYPASSWORD)
                        .addParams("user_phone", phonNumber.getText().toString())
                        .addParams("user_number", SPUtils.getString(Constant.USERID, ""))
                        .addParams("user_password", password.getText().toString())
                        .addParams("code_code", code.getText().toString())
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                //    Log.e("wzj", "1111111111111111111111: " + e.getMessage());
                                Toast.makeText(ForgetPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.e("wzj", "0000000000000: " + response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String message = jsonObject.optString("message");
                                    Toast.makeText(ForgetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                                    finish();
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
