package com.leman.diyaobao.activity;


import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leman.diyaobao.Constant;
import com.leman.diyaobao.R;
import com.leman.diyaobao.fragment.RecordFragment;
import com.leman.diyaobao.fragment.RulesFragment;
import com.leman.diyaobao.okhttp.HttpUrls;
import com.leman.diyaobao.utils.FragmentUtils;
import com.leman.diyaobao.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * 奖励记录
 */
public class RewardsRecordActivity extends BaseActivity implements View.OnClickListener {

    private TextView title;
    private LinearLayout back;

    private FrameLayout frameLayout;

    private RecordFragment recordFragment;
    private RulesFragment rulesFragment;
    private TextView record;
    private TextView rules;

    private TextView total;

    @Override
    public int intiLayout() {
        return R.layout.activity_rewards_record;
    }

    @Override
    public void initView() {

        title = findViewById(R.id.title);
        title.setText("Rewards record");

        frameLayout = findViewById(R.id.frameLayout);
        recordFragment = new RecordFragment();
        rulesFragment = new RulesFragment();
        record = findViewById(R.id.record);
        record.setOnClickListener(this);
        rules = findViewById(R.id.rules);
        rules.setOnClickListener(this);

        total = findViewById(R.id.total);

        back = findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        record.setEnabled(false);
        FragmentUtils.replceFragment(this, R.id.frameLayout, recordFragment);


        OkHttpUtils
                .get()
                .url(HttpUrls.GETTOTALNUMBER)
                .addParams("user_id", SPUtils.getString(Constant.USERID,""))
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
                            total.setText(jsonObject.optString("total"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record:
                record.setEnabled(false);
                rules.setEnabled(true);
                FragmentUtils.replceFragment(RewardsRecordActivity.this, R.id.frameLayout, recordFragment);
                break;
            case R.id.rules:
                record.setEnabled(true);
                rules.setEnabled(false);
                FragmentUtils.replceFragment(RewardsRecordActivity.this, R.id.frameLayout, rulesFragment);
                break;
        }
    }
}
