package com.leman.diyaobao.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leman.diyaobao.Constant;
import com.leman.diyaobao.R;
import com.leman.diyaobao.activity.AccountActivity;
import com.leman.diyaobao.activity.HowSeeMeActivity;
import com.leman.diyaobao.activity.LoginActivity;
import com.leman.diyaobao.activity.MyApplicationActivity;
import com.leman.diyaobao.activity.MyInfoActivity;
import com.leman.diyaobao.activity.RewardsRecordActivity;
import com.leman.diyaobao.movement.SportMapActivity;
import com.leman.diyaobao.okhttp.HttpUrls;
import com.leman.diyaobao.step.UpdateUiCallBack;
import com.leman.diyaobao.step.bean.StepData;
import com.leman.diyaobao.step.service.StepService;
import com.leman.diyaobao.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.leman.diyaobao.fragment.CameraFragment.getTime;

public class MeFragment extends Fragment implements View.OnClickListener {

    private TextView title;
    private LinearLayout info; // 个人信息
    private LinearLayout account; //账户与安全
    private LinearLayout howSeeMe; //谁查看我
    private LinearLayout myApplication; //我的申请
    private LinearLayout myWallet;//我的钱包

    private TextView address;
    private TextView name;
    private TextView exit;

    private TextView todayNumber;

    private ServiceConnection mConnection;
    private boolean isBind;

    private CircleImageView civ_avatar;

    private TextView rTitle;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);


        title = view.findViewById(R.id.title);
        title.setText("Me");
        name = view.findViewById(R.id.name);
        name.setText(SPUtils.getString(Constant.USERNAME, "") + "'s data");

        rTitle = view.findViewById(R.id.rTitle);
        rTitle.setText("Move");
        rTitle.setVisibility(View.VISIBLE);
        rTitle.setOnClickListener(this);

        info = view.findViewById(R.id.info);
        info.setOnClickListener(this);
        account = view.findViewById(R.id.account);
        account.setOnClickListener(this);
        howSeeMe = view.findViewById(R.id.howSeeMe);
        howSeeMe.setOnClickListener(this);
        myApplication = view.findViewById(R.id.myApplication);
        myApplication.setOnClickListener(this);
        myWallet = view.findViewById(R.id.myWallet);
        myWallet.setOnClickListener(this);

        address = view.findViewById(R.id.address);
        address.setText(SPUtils.getString(Constant.ADDESS, "Locate failed"));
        todayNumber = view.findViewById(R.id.todayNumber);

        Log.e("wzj", "刷新失败---------------------： " + getTime().split(" ")[0]);

        civ_avatar = view.findViewById(R.id.civ_avatar);
        Log.e("wzj", "sssssssssssss:  " + SPUtils.getString(Constant.USERIMAGE, ""));
        Glide.with(getActivity()).load(SPUtils.getString(Constant.USERIMAGE, "")).into(civ_avatar);

        exit = view.findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtils.putBoolean(Constant.IS_LOGIN, false);
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });

        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                StepService stepService = ((StepService.StepBinder) service).getService();
                todayNumber.setText(String.valueOf(stepService.getStepCount()));
                //设置步数监听回调
                stepService.registerCallback(new UpdateUiCallBack() {
                    @Override
                    public void updateUi(int stepCount) {
                        todayNumber.setText(String.valueOf(stepCount));
                        if (Integer.parseInt(todayNumber.getText().toString()) > 9999 && !SPUtils.getString(Constant.UPLOADTIME, "123").equals(getTime().split(" ")[0])) {
                            OkHttpUtils
                                    .post()
                                    .url(HttpUrls.UPLOADENUMBER)
                                    .addParams("user_id", SPUtils.getString(Constant.USERID, ""))
                                    .addParams("time", getTime())
                                    .addParams("get_way", "1")
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
                                            SPUtils.putString(Constant.UPLOADTIME, getTime().split(" ")[0]);
                                            //    setData(true, response.body().);
                                            try {
                                                JSONObject jsonObject = new JSONObject(response); //totalIntegral
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                        }
                    }
                });
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        Intent intent = new Intent(getActivity(), StepService.class);
        getActivity().startService(intent);
        isBind = getActivity().bindService(intent, this.mConnection, Context.BIND_AUTO_CREATE);


        uploadData();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //个人信息
            case R.id.info:
                startActivity(new Intent(getActivity(), MyInfoActivity.class));
                break;
            //账户与安全
            case R.id.account:
                startActivity(new Intent(getActivity(), AccountActivity.class));
                break;
            //谁申请查看我
            case R.id.howSeeMe:
                startActivity(new Intent(getActivity(), HowSeeMeActivity.class));
                break;
            //我的申请
            case R.id.myApplication:
                startActivity(new Intent(getActivity(), MyApplicationActivity.class));
                break;
            //运动
            case R.id.rTitle:
                startActivity(new Intent(getActivity(), SportMapActivity.class));
                break;
            //我的钱包
            case R.id.myWallet:
                startActivity(new Intent(getActivity(), RewardsRecordActivity.class));
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBind) {
            getActivity().unbindService(mConnection);
        }
    }

    private void uploadData() {

        Map<String, String> params = new HashMap<>();
        params.put("userId", SPUtils.getString(Constant.USERID, ""));
        params.put("userName", SPUtils.getString(Constant.USER_NAME, ""));
        params.put("code", "000");
        params.put("time", String.valueOf(System.currentTimeMillis()));
        params.put("dataType", "1");
        params.put("productType", "2");
        params.put("timeType", "1");
        params.put("stepData", todayNumber.getText().toString());

        JSONObject jsonObject = new JSONObject(params);

        OkHttpUtils
                .postString()
                .url(HttpUrls.UPLOADSPORT)
                .content(String.valueOf(jsonObject))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new com.zhy.http.okhttp.callback.StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("wzj", "22222aaaaaaaaaaaaaa: " + id);
                        Log.e("wzj", "22222aaaaaaaaaaaaaa: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("wzj", "上传成功------------------------: " + response);
                        // ToastUtils.showLong("上传成功");
                    }
                });
    }
}
