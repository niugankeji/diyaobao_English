package com.leman.diyaobao.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.leman.diyaobao.Constant;
import com.leman.diyaobao.R;
import com.leman.diyaobao.adapter.RecordAdapter;
import com.leman.diyaobao.entity.RecordItem;
import com.leman.diyaobao.movement.InsideUpdate;
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

public class RecordFragment extends Fragment implements InsideUpdate.UpdateNotify {


    private RecyclerView rv_list;
    private List<RecordItem.DataBean> mDataList;
    RecordItem.DataBean item;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    private RecordAdapter mAdapter;

    private int mNextRequestPage = 1;
    private int PAGE_SIZE = 20;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        rv_list = view.findViewById(R.id.rv_list);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeLayout);

        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        initAdapter();
        initRefreshLayout();
        mSwipeRefreshLayout.setRefreshing(true);

        refresh();

        rv_list.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    private void initRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    private void initAdapter() {
        mAdapter = new RecordAdapter(R.layout.report_item, mDataList);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        });
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
//        mAdapter.setPreLoadNumber(3);
        rv_list.setAdapter(mAdapter);

        rv_list.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(final BaseQuickAdapter adapter, final View view, final int position) {
//                RecordItem.DataBean dataBean = (RecordItem.DataBean) adapter.getItem(position);
//                Intent intent = new Intent();
//                intent.setClass(MedicalReportActivity.this, ReportXiangActivity.class);
//                intent.putExtra("data", dataBean);
//                intent.putExtra("type", "0");
//                startActivity(intent);
            }
        });
    }

    private void refresh() {
        mNextRequestPage = 1;
        mAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
        mDataList = new ArrayList<>();


        OkHttpUtils
                .get()
                .url(HttpUrls.GETRECORDLIST)
                .addParams("user_id", SPUtils.getString(Constant.USERID,""))
                .addParams("page", String.valueOf(mNextRequestPage))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("wzj", "刷新失败---------------------： " + id);
                        Log.e("wzj", "刷新失败---------------------： " + e.getMessage());
                        mAdapter.setEnableLoadMore(true);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("wzj", "刷新成功---------------------: " + response);
                        //    setData(true, response.body().);
                        try {
                            JSONObject jsonObject = new JSONObject(response); //totalIntegral
                            JSONArray array = jsonObject.optJSONArray("list");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject data = array.getJSONObject(i);
                                item = new RecordItem.DataBean();
                                item.setIntegralRecordId(data.optInt("id"));
                                item.setCreatedTime(data.optString("time"));
                                item.setIntegralValue(data.optString("number"));
                                item.setIntegralWay(data.optInt("get_way"));
                                mDataList.add(item);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        setData(true, mDataList);
                        mAdapter.setEnableLoadMore(true);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });


    }

//    public void sortView(View view) {
//        if (mAdapter != null && mAdapter.getItemCount() > 0) {
//            mAdapter.sort(view);
//        }
//    }

    private void loadMore() {
        Log.e("wzj","xxxxxxxxxxxxxxx:  "+mNextRequestPage);
        OkHttpUtils
                .get()
                .url(HttpUrls.GETRECORDLIST)
                .addParams("userId", SPUtils.getString(Constant.USERID,""))
                .addParams("page", String.valueOf(mNextRequestPage))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("wzj", "刷新失败1111111111111---------------------： " + id);
                        Log.e("wzj", "刷新失败1111111111111111---------------------： " + e.getMessage());
                        mAdapter.setEnableLoadMore(true);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("wzj", "刷新成功---------------------: " + response);

                        //    setData(true, response.body().);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray array = jsonObject.optJSONArray("data");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject data = array.getJSONObject(i);
                                item.setIntegralRecordId(data.optInt("integralRecordId"));
                                item.setCreatedTime(data.optString("createdTime"));
                                item.setIntegralValue(data.optString("integralValue"));
                                item.setIntegralWay(data.optInt("integralWay"));
                                mDataList.add(item);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        setData(true, mDataList);
                        mAdapter.setEnableLoadMore(true);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }


    private void setData(boolean isRefresh, List<RecordItem.DataBean> data) {
        mNextRequestPage++;
        final int size = data == null ? 0 : data.size();
        if (isRefresh) {
            mAdapter.setNewData(data);
        } else {
            if (size > 0) {
                mAdapter.addData(data);
            }
        }
        if (size < PAGE_SIZE) {
            //第一页如果不够一页就不显示没有更多数据布局
            mAdapter.loadMoreEnd(isRefresh);
//            Toast.makeText(getActivity(), "no more data", Toast.LENGTH_SHORT).show();
        } else {
            mAdapter.loadMoreComplete();
        }
    }

    /**
     * @param action 更新指令
     * @param value  回传值 可变参数可以不传值
     */
    @Override
    public void updateUi(int action, Object... value) {
        switch (action) {
            case InsideUpdate.USER_UPDATE:
                if (mAdapter != null && mAdapter.getItemCount() > 0) {
                    refresh();
                }
                break;

        }
    }
}
