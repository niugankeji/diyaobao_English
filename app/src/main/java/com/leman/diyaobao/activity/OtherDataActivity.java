package com.leman.diyaobao.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.leman.diyaobao.Constant;
import com.leman.diyaobao.R;
import com.leman.diyaobao.adapter.DataAdapter;
import com.leman.diyaobao.adapter.OtherDataListAdapter;
import com.leman.diyaobao.datalist.DividerItemDecoration;
import com.leman.diyaobao.entity.DataItem;
import com.leman.diyaobao.fragment.EndlessRecyclerOnScrollListener;
import com.leman.diyaobao.okhttp.HttpUrls;
import com.leman.diyaobao.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class OtherDataActivity extends BaseActivity {

    private TextView title;
    private LinearLayout back;
    DataItem item1;
    private List<DataItem> mDataList1;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private OtherDataListAdapter otherDataListAdapter;
    String phone;

    int page = 1;
    int pageTotal;
    boolean isMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int intiLayout() {
        return R.layout.activity_other_data;
    }

    @Override
    public void initView() {

        Intent intent = getIntent();
        phone = intent.getStringExtra("ptone");

        title = findViewById(R.id.title);
        title.setText(phone + "'s data'");
        back = findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        // 设置刷新控件颜色
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#4DB6AC"));

        otherDataListAdapter = new OtherDataListAdapter(this);
        mLinearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        DividerItemDecoration itemDecorationHeader = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        itemDecorationHeader.setDividerDrawable(ContextCompat.getDrawable(this, R.drawable.divider_main_bg_height_1));
        recyclerView.addItemDecoration(itemDecorationHeader);

        mDataList1 = new ArrayList<>();
        getData(page);

    }

    @Override
    public void initData() {

        // 设置下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDataList1.clear();
                isMore = false;
                page = 1;
                getData(page);
                otherDataListAdapter.notifyDataSetChanged();

            }
        });

        // 设置加载更多监听
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                isMore = true;
                otherDataListAdapter.setLoadState(otherDataListAdapter.LOADING);
                page++;

                if (page < pageTotal | page == pageTotal) {

                    getData(page);

                } else {
                    // 显示加载到底的提示
                    otherDataListAdapter.setLoadState(otherDataListAdapter.LOADING_END);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void getData(int page){


        OkHttpUtils
                .get()
                .url(HttpUrls.GETDATA)
                .addParams("user_number", phone)
                .addParams("page",page+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(OtherDataActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("wzj", "0000000000000: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.optString("message");
                            pageTotal = jsonObject.optInt("pagenumber");
                            Log.e("wzj", "3333333333333: " + message);
                            if (message.equals("请求成功！")) {
                                JSONArray array = jsonObject.getJSONArray("list");

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject data = array.optJSONObject(i);
                                    item1 = new DataItem();
                                    item1.setId(data.optInt("id"));
                                    item1.setData_lai(data.optString("data_lai"));
                                    item1.setData_address(data.optString("data_address"));
                                    item1.setData_munsell(data.optString("data_munsell"));
                                    item1.setData_lon(data.optString("data_lon"));
                                    item1.setData_lat(data.optString("data_lat"));
                                    item1.setData_image(HttpUrls.IMAGE + data.optString("data_image"));
                                    item1.setData_day(data.optString("data_day"));
                                    item1.setData_duty(data.optString("data_duty"));
                                    item1.setData_model(data.optString("data_model"));
                                    item1.setData_cost(data.optString("data_cost"));
                                    mDataList1.add(item1);
                                }
                            }

                            recyclerView.setAdapter(otherDataListAdapter);
                            otherDataListAdapter.notifyAdapter(mDataList1, false);
                            otherDataListAdapter.notifyDataSetChanged();
                            if (isMore){
                                Log.e("wzj","222222222222222222222222222++++++++++++++++++++： ");
                                otherDataListAdapter.setLoadState(otherDataListAdapter.LOADING_COMPLETE);
                            }
                            //关闭下拉刷新
                            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            otherDataListAdapter.setOnItemClickListener(new OtherDataListAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClickListener(int pos, List<DataItem> myLiveList) {
                                    Intent intent = new Intent();
                                    intent.setClass(OtherDataActivity.this, DataActivity.class);
                                    intent.putExtra("image", mDataList1.get(pos).getData_image());
                                    intent.putExtra("address", mDataList1.get(pos).getData_address());
                                    intent.putExtra("LAI", mDataList1.get(pos).getData_lai());
                                    intent.putExtra("model", mDataList1.get(pos).getData_model());
                                    intent.putExtra("color", mDataList1.get(pos).getData_munsell());
                                    intent.putExtra("time", mDataList1.get(pos).getData_day());
                                    Log.e("wzj","id///////////////////////: "+mDataList1.get(pos).getId());
                                    intent.putExtra("id", mDataList1.get(pos).getId()+"");
                                    intent.putExtra("is_true", "true");
                                    startActivity(intent);
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                });

    }
}
