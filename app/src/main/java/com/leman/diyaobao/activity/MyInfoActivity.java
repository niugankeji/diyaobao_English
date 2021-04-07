package com.leman.diyaobao.activity;


import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.bumptech.glide.Glide;
import com.leman.diyaobao.Constant;
import com.leman.diyaobao.R;
import com.leman.diyaobao.myview.PhotoPopWindow;
import com.leman.diyaobao.okhttp.HttpUrls;
import com.leman.diyaobao.utils.GetPermissions;
import com.leman.diyaobao.utils.ImageUtils;
import com.leman.diyaobao.utils.ImputDialog;
import com.leman.diyaobao.utils.LQRPhotoSelectUtils;
import com.leman.diyaobao.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

public class MyInfoActivity extends BaseActivity implements View.OnClickListener {

    private TextView title;
    private LinearLayout back;
    private LinearLayout birthday;
    private TextView showBirthday;

    private TextView name;
    private TextView user_id;
    private TextView emial;
    private TextView phone;
    private TextView address;

    private TextView other;

    private File imagFile;
    private TextView upload;
    private String sex = "0";


    private TimePickerView pvTime;

    private RadioGroup choice;
    private CircleImageView civ_avatar;
    private PhotoPopWindow mPopwindow;

    private LQRPhotoSelectUtils mLqrPhotoSelectUtils;

    GetPermissions getPermissions = new GetPermissions(this);
    String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};


    @Override
    public int intiLayout() {
        return R.layout.activity_my_info;
    }

    @Override
    public void initView() {
        title = findViewById(R.id.title);
        title.setText(SPUtils.getString(Constant.USERNAME, "") + "'s information'");
        back = findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);

        birthday = findViewById(R.id.birthday);
        birthday.setOnClickListener(this);
        showBirthday = findViewById(R.id.showBirthday);
        other = findViewById(R.id.other);

        civ_avatar = findViewById(R.id.civ_avatar);
        civ_avatar.setOnClickListener(this);
        choice = (RadioGroup) this.findViewById(R.id.choice);
        name = findViewById(R.id.name);
        user_id = findViewById(R.id.id);
        emial = findViewById(R.id.emial);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        upload = findViewById(R.id.upload);
        upload.setOnClickListener(this);

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImputDialog(MyInfoActivity.this, R.style.dialog, new ImputDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm, String text) {
                        if (confirm) {
                            name.setText(text);
                        }
                        dialog.dismiss();
                    }
                }).setTitle("请输入昵称").show();
            }
        });
        emial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImputDialog(MyInfoActivity.this, R.style.dialog, new ImputDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm, String text) {
                        if (confirm) {
                            emial.setText(text);
                        }
                        dialog.dismiss();
                    }
                }).setTitle("请输入邮箱").show();
            }
        });
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImputDialog(MyInfoActivity.this, R.style.dialog, new ImputDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm, String text) {
                        if (confirm) {
                            other.setText(text);
                        }
                        dialog.dismiss();
                    }
                }).setTitle("请输入个性签名").show();
            }
        });


        //获取用户资料
        OkHttpUtils
                .get()
                .url(HttpUrls.GETUSERINFO)
                .addParams("user_number", SPUtils.getString(Constant.USERID, ""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(MyInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("wzj", "0000000000000: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.optString("message");
                            if (message.equals("请求成功！")) {
                                JSONArray array = jsonObject.optJSONArray("info");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject info = array.optJSONObject(i);
                                    name.setText(info.optString("uesr_name"));
                                    user_id.setText(info.optString("user_number"));
                                    if (info.optString("uesr_sex").equals("0")) {
                                        choice.check(R.id.man);
                                    } else if (info.optString("uesr_sex").equals("1")) {
                                        choice.check(R.id.woman);
                                    }
                                    showBirthday.setText(info.optString("user_birthday"));
                                    emial.setText(info.optString("user_email"));
                                    phone.setText(info.optString("user_phone"));
                                    address.setText(info.optString("user_address"));
                                    other.setText(info.optString("user_other"));
                                    if (!info.optString("uesr_avatar").equals("null")) {
                                        String image = HttpUrls.IMAGE + info.optString("uesr_avatar");
                                        SPUtils.putString(Constant.USERIMAGE, image);
                                        Glide.with(MyInfoActivity.this).load(image).into(civ_avatar);
                                    } else {
                                        Glide.with(MyInfoActivity.this).load(R.mipmap.head).into(civ_avatar);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                });


        choice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //得到用户选中的 RadioButton 对象
                RadioButton radioButton_checked = (RadioButton) group.findViewById(checkedId);
                String gender = radioButton_checked.getText().toString();
                switch (checkedId) {
                    case R.id.man:
                        //当用户点击男性按钮时执行的代码
                        Log.e("wzj", "===男性===");
                        sex = "0";
                        break;
                    case R.id.woman:
                        //当用户点击女性按钮时执行的代码
                        Log.e("wzj", "===女性===");
                        sex = "1";
                        break;
                }

            }

        });
        initTimePicker();

    }

    @Override
    public void initData() {
        mLqrPhotoSelectUtils = new LQRPhotoSelectUtils(this, new LQRPhotoSelectUtils.PhotoSelectListener() {
            @Override
            public void onFinish(File outputFile, String imagepath) {
                imagFile = outputFile;
                Glide.with(MyInfoActivity.this).load(imagepath).into(civ_avatar);
            }

        }, false);//true裁剪，false不裁剪
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.birthday:
                if (pvTime != null) {
                    pvTime.show(v);
                }
                break;
            case R.id.civ_avatar:
                getPermissions.getPression(new GetPermissions.PermissionCallback() {
                    @Override
                    public void success() {
                        showPopWindow();
                    }
                });
                getPermissions.getpermissions(permissions);
                break;
            case R.id.upload:
                //提交用户资料
                if (imagFile == null) {
                    OkHttpUtils
                            .post()
                            .url(HttpUrls.UPLOADUSERINFO)
                            .addParams("user_number", SPUtils.getString(Constant.USERID, ""))
                            .addParams("user_phone", phone.getText().toString())
                            .addParams("uesr_name", name.getText().toString())
                            .addParams("uesr_sex", sex)
                            .addParams("user_other", other.getText().toString())
                            .addParams("user_birthday", showBirthday.getText().toString())
                            .addParams("user_email", emial.getText().toString())
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    Toast.makeText(MyInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    Log.e("wzj", "0000000000000: " + response);
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String message = jsonObject.optString("message");
                                        Toast.makeText(MyInfoActivity.this, message, Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                            });
                } else {
                    OkHttpUtils
                            .post()
                            .url(HttpUrls.UPLOADUSERINFO)
                            .addParams("user_number", SPUtils.getString(Constant.USERID, ""))
                            .addParams("user_phone", phone.getText().toString())
                            .addFile("uesr_avatar", "123", imagFile)
                            .addParams("uesr_name", name.getText().toString())
                            .addParams("uesr_sex", sex)
                            .addParams("user_other", other.getText().toString())
                            .addParams("user_birthday", showBirthday.getText().toString())
                            .addParams("user_email", emial.getText().toString())
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    Toast.makeText(MyInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    Log.e("wzj", "0000000000000: " + response);
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String message = jsonObject.optString("message");
                                        Toast.makeText(MyInfoActivity.this, message, Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                            });
                }

                break;
        }
    }

    private void initTimePicker() {//Dialog 模式下，在底部弹出

        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                showBirthday.setText(getTime(date));

            }
        })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                        Log.i("pvTime", "onTimeSelectChanged");
                    }
                })
                .setType(new boolean[]{true, true, true, false, false, false})
                .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
                .addOnCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("pvTime", "onCancelClickListener");
                    }
                })
                .build();

        Dialog mDialog = pvTime.getDialog();
        if (mDialog != null) {

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);

            params.leftMargin = 0;
            params.rightMargin = 0;
            pvTime.getDialogContainerLayout().setLayoutParams(params);

            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
                dialogWindow.setDimAmount(0.1f);
            }
        }
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    private void showPopWindow() {
        mPopwindow = new PhotoPopWindow(MyInfoActivity.this);
        mPopwindow.showAtLocation(civ_avatar,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        mPopwindow.OnClickLinster(new PhotoPopWindow.WindowOnClickLinster() {
            @Override
            public void onClickCamera() {
                mLqrPhotoSelectUtils.takePhoto();
            }

            @Override
            public void onClickPhoto() {
                mLqrPhotoSelectUtils.selectPhoto();
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
                        boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(MyInfoActivity.this, permissions[i]);
                        if (showRequestPermission) {//
                            Log.e("tag", "拒绝11111*****************");
                            return;
                        } else {

                        }
                    }
                }
                showPopWindow();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 2、在Activity中的onActivityResult()方法里与LQRPhotoSelectUtils关联
        mLqrPhotoSelectUtils.attachToActivityForResult(requestCode, resultCode, data);
    }

}
