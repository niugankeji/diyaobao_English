package com.leman.diyaobao.activity;


import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.leman.diyaobao.Constant;
import com.leman.diyaobao.R;
import com.leman.diyaobao.adapter.DataAdapter;
import com.leman.diyaobao.adapter.HowSeeMeAdapter;
import com.leman.diyaobao.adapter.MyApplicationAdapter;
import com.leman.diyaobao.entity.DataItem;
import com.leman.diyaobao.entity.HowSeeMeItem;
import com.leman.diyaobao.entity.MyApplicationItem;
import com.leman.diyaobao.okhttp.HttpUrls;
import com.leman.diyaobao.utils.SPUtils;
import com.leman.diyaobao.utils.SelectDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class MyApplicationActivity extends BaseActivity {

    private TextView title;
    private LinearLayout back;

    private RecyclerView rv_list;
    private List<MyApplicationItem> mDataList;
    MyApplicationItem item;


    @Override
    public int intiLayout() {
        return R.layout.activity_my_application;
    }

    @Override
    public void initView() {
        title = findViewById(R.id.title);
        title.setText("My application");
        back = findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rv_list = findViewById(R.id.rv_list);
        rv_list.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void initData() {

        mDataList = new ArrayList<>();

        OkHttpUtils
                .get()
                .url(HttpUrls.GETMYAPPLILIST)
                .addParams("from_user", SPUtils.getString(Constant.USERID, ""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        //    Log.e("wzj", "1111111111111111111111: " + e.getMessage());
                        Toast.makeText(MyApplicationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("wzj", "0000000000000: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.optString("message");
                            if (message.equals("请求成功！")) {
                                JSONArray array = jsonObject.optJSONArray("list");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject user = array.getJSONObject(i);
                                    item = new MyApplicationItem();
                                    item.setImageUrl(HttpUrls.IMAGE + user.optString("image"));
                                    item.setState(user.optString("state"));
                                    item.setFrom_user(user.optString("from_user"));
                                    item.setTo_user(user.optString("to_user"));
                                    item.setId(user.optString("id"));
                                    mDataList.add(item);
                                }

                                final BaseQuickAdapter homeAdapter = new MyApplicationAdapter(R.layout.my_application_item, mDataList);
                                homeAdapter.openLoadAnimation();
                                homeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                        if (mDataList.get(position).getState().equals("2")) {
                                            Toast.makeText(MyApplicationActivity.this, "对方还未同意您的申请", Toast.LENGTH_LONG).show();
                                        } else if (mDataList.get(position).getState().equals("1")) {
                                            Toast.makeText(MyApplicationActivity.this, "对方拒绝了您的申请", Toast.LENGTH_LONG).show();
                                        } else {
                                            Intent intent = new Intent();
                                            intent.setClass(MyApplicationActivity.this, OtherDataActivity.class);
                                            intent.putExtra("ptone", mDataList.get(position).getTo_user());
                                            startActivity(intent);

                                        }
                                    }
                                });
                                rv_list.setAdapter(homeAdapter);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                });


    }
}
