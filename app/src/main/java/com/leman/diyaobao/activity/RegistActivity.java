package com.leman.diyaobao.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.leman.diyaobao.Constant;
import com.leman.diyaobao.R;
import com.leman.diyaobao.okhttp.HttpUrls;
import com.leman.diyaobao.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;


import okhttp3.Call;

import static com.leman.diyaobao.fragment.CameraFragment.getTime;

/**
 * 注册
 */
public class RegistActivity extends BaseActivity implements View.OnClickListener {

    private EditText phonNumber;
    private EditText code;
    private TextView sendCode;
    private EditText password;
    private TextView regist;
    private ImageView showPassword;
    private TextView title;
    private TextView xieyi;

    MyCountDownTimer myCountDownTimer;
    private long time = 60000;


    private String username = null;
    private String passwd = null;

    private CheckBox checkbox;


    @Override
    public int intiLayout() {
        return R.layout.activity_regist;
    }

    @Override
    public void initView() {
        phonNumber = findViewById(R.id.phonNumber);
        code = findViewById(R.id.code);
        password = findViewById(R.id.password);
        sendCode = findViewById(R.id.sendCode);
        sendCode.setOnClickListener(this);

        regist = findViewById(R.id.regist);
        regist.setOnClickListener(this);
        showPassword = findViewById(R.id.showPassword);
        showPassword.setOnClickListener(this);

        title = findViewById(R.id.title);
        title.setText("Regist");

        xieyi = findViewById(R.id.xieyi);
        xieyi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("title","Privacy agreement");
                intent.setClass(RegistActivity.this, ServiceActivity.class);
                startActivity(intent);
            }
        });
        checkbox = findViewById(R.id.checkbox);
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox.isChecked()) {
                    checkbox.setChecked(true);
                }else {
                    checkbox.setChecked(false);
                }
            }
        });
    }

    @Override
    public void initData() {

        //    username = SPUtils.getString(Constant.USER_NAME, null);
        passwd = SPUtils.getString(Constant.PASSWORD, null);

        myCountDownTimer = new MyCountDownTimer(time, 1000);

        if (password.getInputType() == 129) {
            Glide.with(RegistActivity.this).load(R.mipmap.show_password).into(showPassword);
        } else {
            Glide.with(RegistActivity.this).load(R.mipmap.hide_password).into(showPassword);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.sendCode:
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
                                Toast.makeText(RegistActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                break;
            case R.id.showPassword:
                if (password.getInputType() == 129) {
                    password.setInputType(1);
                    Glide.with(RegistActivity.this).load(R.mipmap.hide_password).into(showPassword);
                } else {
                    password.setInputType(129);
                    Glide.with(RegistActivity.this).load(R.mipmap.show_password).into(showPassword);
                }

                break;

            case R.id.regist:
                if (!checkbox.isChecked()){
                    Toast.makeText(RegistActivity.this,"请认真阅读隐私协议",Toast.LENGTH_SHORT).show();
                    break;
                }
                String telnumber = phonNumber.getText().toString();
                if (telnumber.length() != 11) {
                    Toast.makeText(RegistActivity.this, "手机号码不正确", Toast.LENGTH_SHORT).show();
                    break;
                }
			/*
			if (Verficode==null || !VerficodeEdit.getText().toString().equals(Verficode)) {
				Toast.makeText(LoginActivity.this, "验证码不正确", Toast.LENGTH_SHORT).show();
				break;
			}
			*/

                final String RPasswd = password.getText().toString();
                if (RPasswd.length() < 5) {
                    Toast.makeText(RegistActivity.this, "密码长度太短", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (RPasswd.length() > 16) {
                    Toast.makeText(RegistActivity.this, "密码长度不能超过16位", Toast.LENGTH_SHORT).show();
                    break;
                }

                OkHttpUtils
                        .post()
                        .url(HttpUrls.REGIST)
                        .addParams("user_phone", phonNumber.getText().toString())
                        .addParams("user_password", password.getText().toString())
                        .addParams("code_code", code.getText().toString())
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Toast.makeText(RegistActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                SPUtils.putString(Constant.PHONNUMBER, phonNumber.getText().toString());
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String message = jsonObject.optString("message");
                                    if (message.equals("注册成功！")) {

                                        OkHttpUtils
                                                .post()
                                                .url(HttpUrls.UPLOADENUMBER)
                                                .addParams("user_id", jsonObject.optString("user_id"))
                                                .addParams("time",getTime())
                                                .addParams("get_way","0")
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
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                });


                                        SPUtils.putString(Constant.USERID, jsonObject.optString("user_id"));
                                        Log.e("wzj", "用户ID.................： " + SPUtils.getString(Constant.USERID, ""));
                                        Toast.makeText(RegistActivity.this, message, Toast.LENGTH_LONG).show();
                                        finish();
                                    } else {
                                        Toast.makeText(RegistActivity.this, message, Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        });
                break;
        }
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
