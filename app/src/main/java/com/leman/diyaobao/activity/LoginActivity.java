package com.leman.diyaobao.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.leman.diyaobao.Constant;
import com.leman.diyaobao.dialog.LoadingDialog;
import com.leman.diyaobao.dialog.MyDialog;
import com.leman.diyaobao.okhttp.HttpUrls;
import com.leman.diyaobao.utils.ActivityUtils;
import com.leman.diyaobao.HttpConnection;
import com.leman.diyaobao.R;
import com.leman.diyaobao.utils.GetPermissions;
import com.leman.diyaobao.utils.LogUtil;
import com.leman.diyaobao.utils.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

/**
 * 登录
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText phonNumber = null;
    private EditText password = null;
    private TextView login;
    private TextView regist;
    private ImageView showPassword;
    private CheckBox checkbox;
    private TextView fpassword;

    private String httpURL = HttpConnection.httpURL;
    private String username = null;
    private String passwd = null;




    @Override
    public int intiLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
        ActivityUtils.fullScreen(LoginActivity.this);
        phonNumber = (EditText) findViewById(R.id.phonNumber);
        password = (EditText) findViewById(R.id.password);
        login = findViewById(R.id.login);
        login.setOnClickListener(this);
        regist = findViewById(R.id.regist);
        regist.setOnClickListener(this);
        showPassword = findViewById(R.id.showPassword);
        showPassword.setOnClickListener(this);
        checkbox = findViewById(R.id.checkbox);
        checkbox.setOnClickListener(this);
        fpassword = findViewById(R.id.fpassword);
        fpassword.setOnClickListener(this);
    }

    @Override
    public void initData() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        passwd = SPUtils.getString(Constant.PASSWORD, null);
        password.setText(passwd);
        username = SPUtils.getString(Constant.USER_NAME, null);
        phonNumber.setText(username);

        checkbox.setChecked(SPUtils.getBoolean(Constant.CHACK_BOX, false));

        if (username != null & SPUtils.getBoolean(Constant.IS_LOGIN, false) & checkbox.isChecked()) {
            phonNumber.setText(username);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }



        if (password.getInputType() == 129) {
            Glide.with(LoginActivity.this).load(R.mipmap.show_password).into(showPassword);
        } else {
            Glide.with(LoginActivity.this).load(R.mipmap.hide_password).into(showPassword);
        }


        if (SPUtils.getBoolean(Constant.ISFIRSTINSTALL,true)){
            Log.e("wzj","第一次安装+++++++++++++++++++++++++++++++");
            SPUtils.putBoolean(Constant.ISFIRSTINSTALL,false);
            SPUtils.putString(Constant.USEENDTIME,getOldDate(30));
        }

    }

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.login, menu);
//		return true;
//	}


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            /*
		case R.id.GetVerficodeBtn:
			String TelEditText = TelEdit.getText().toString();
			if (TelEditText.length() == 11)
			{
				TelNum = TelEditText;
				Verficode = "";
				for (int i = 0; i < 6; i++) {
					Verficode = Verficode + (int)(Math.random()*10);
				}
				Map<String, String> Info = new HashMap<String, String>();
				Info.put("tel", TelNum);
				Info.put("secode", Verficode);
				String res = HttpConnection.HttpPostAction(httpURL+"telsdk/telsent.php", Info, "UTF-8");
				if (res == null) {
					Toast.makeText(LoginActivity.this, "连接服务器超时", Toast.LENGTH_SHORT).show();
				}
			}else {
				Toast.makeText(LoginActivity.this, "号码格式不正确", Toast.LENGTH_SHORT).show();
			}
			break;
			*/

            case R.id.login:
            //    MyDialog.showProgressDialog(getApplicationContext(),"正在登录");
                Log.e("wzj", "登录+++++++++++++++++++++++：  ");
                String LUsername = phonNumber.getText().toString();
                String LPasswd = password.getText().toString();
                if (LPasswd.length() > 16) {
                    MyDialog.dismissProgressDialog();
                    Toast.makeText(LoginActivity.this, "密码不能超过16位", Toast.LENGTH_LONG).show();
                    return;
                }

                Map<String, String> mInfo = new HashMap<String, String>();
                mInfo.put("user_phone", LUsername);
                mInfo.put("user_password", LPasswd);
                String mres = HttpConnection.HttpPostAction(HttpUrls.LOGIN, mInfo, "UTF-8");
                if (mres == null) {
                    MyDialog.dismissProgressDialog();
                    Toast.makeText(LoginActivity.this, "连接服务器超时", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Log.e("wzj", "登录成功 ------------------： " + mres);
                        JSONObject jsonObject = new JSONObject(mres);
                        String message = jsonObject.optString("message");
                        if (message.equals("登录成功！")) {
                            MyDialog.dismissProgressDialog();
                            JPushInterface.setAlias(LoginActivity.this, 0, SPUtils.getString(Constant.USERID, ""));
                            SPUtils.putString(Constant.USERID, jsonObject.optString("user_id"));
                            SPUtils.putString(Constant.USERNAME, jsonObject.optString("uesr_name"));
                            Log.e("wzj", "登录成功 ID-------------------： " + SPUtils.getString(Constant.USERID, ""));
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            username = LUsername;
                            passwd = LPasswd;
                            SPUtils.putString(Constant.PASSWORD, passwd);
                            SPUtils.putString(Constant.USER_NAME, username);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            MyDialog.dismissProgressDialog();
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        MyDialog.dismissProgressDialog();
                        // TODO Auto-generated catch block
                        Log.e("wzj", "登录异常+++++++++++++++++++++++：  " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                break;
            case R.id.regist:
                startActivity(new Intent(LoginActivity.this, RegistActivity.class));
                break;
            case R.id.showPassword:
                if (password.getInputType() == 129) {
                    password.setInputType(1);
                    Glide.with(LoginActivity.this).load(R.mipmap.hide_password).into(showPassword);
                } else {
                    password.setInputType(129);
                    Glide.with(LoginActivity.this).load(R.mipmap.show_password).into(showPassword);
                }
                break;
            case R.id.checkbox:
                if (checkbox.isChecked()) {
                    SPUtils.putBoolean(Constant.CHACK_BOX, true);
                } else {
                    SPUtils.putBoolean(Constant.CHACK_BOX, false);
                }
                break;

            case R.id.fpassword:
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
                break;
            default:
                break;
        }
    }

    /**
     * 获取前n天日期、后n天日期
     *
     * @param distanceDay 前几天 如获取前7天日期则传-7即可；如果后7天则传7
     * @return
     */
    public static String getOldDate(int distanceDay) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dft.format(endDate);
    }

}
