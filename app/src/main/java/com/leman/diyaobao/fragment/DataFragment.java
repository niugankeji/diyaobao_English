package com.leman.diyaobao.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.leman.diyaobao.Constant;
import com.leman.diyaobao.R;
import com.leman.diyaobao.activity.DataActivity;
import com.leman.diyaobao.adapter.DataAdapter;
import com.leman.diyaobao.datalist.DataListAdapter;
import com.leman.diyaobao.datalist.DividerItemDecoration;
import com.leman.diyaobao.entity.DataItem;
import com.leman.diyaobao.entity.ZhiWuData;
import com.leman.diyaobao.okhttp.HttpUrls;
import com.leman.diyaobao.utils.SPUtils;
import com.leman.diyaobao.utils.jankinDBOpenHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import static com.leman.diyaobao.utils.netUtils.isNetworkConnected;


public class DataFragment extends Fragment {
    private TextView title;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private List<DataItem> mDataList;
    DataItem item;
    private TextView rTitle;

    private LinearLayout ll_mycollection_bottom_dialog;

    private DataListAdapter mRadioAdapter = null;
    private LinearLayoutManager mLinearLayoutManager;

    private static final int MYLIVE_MODE_CHECK = 0;
    private static final int MYLIVE_MODE_EDIT = 1;

    private Button btn_delete;
    private TextView select_all;
    private TextView tv_select_num;
    private boolean isSelectAll = false;
    private boolean editorStatus = false;
    private int index = 0;
    private int mEditMode = MYLIVE_MODE_CHECK;

    int page = 1;
    int pageTotal;
    boolean isMore = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data, container, false);

        title = view.findViewById(R.id.title);
        title.setText("Data");

        recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        // 设置刷新控件颜色
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#4DB6AC"));

        btn_delete = view.findViewById(R.id.btn_delete);
        select_all = view.findViewById(R.id.select_all);
        tv_select_num = view.findViewById(R.id.tv_select_num);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteVideo();
            }
        });
        select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAllMain();
            }
        });

        mRadioAdapter = new DataListAdapter(getContext());
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        DividerItemDecoration itemDecorationHeader = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        itemDecorationHeader.setDividerDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider_main_bg_height_1));
        recyclerView.addItemDecoration(itemDecorationHeader);
        ll_mycollection_bottom_dialog = view.findViewById(R.id.ll_mycollection_bottom_dialog);



        rTitle = view.findViewById(R.id.rTitle);
        rTitle.setText("取消");
        rTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_mycollection_bottom_dialog.setVisibility(View.GONE);
                rTitle.setVisibility(View.GONE);
                editorStatus = false;
                mEditMode = mEditMode == MYLIVE_MODE_CHECK ? MYLIVE_MODE_EDIT : MYLIVE_MODE_CHECK;
                mRadioAdapter.setEditMode(mEditMode);
            }
        });
        mDataList = new ArrayList<>();
        init();
        getData(page);
        return view;
    }

    private void init() {

        // 设置下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDataList.clear();
                isMore = false;
                page = 1;
                getData(page);
                mRadioAdapter.notifyDataSetChanged();

            }
        });

        // 设置加载更多监听
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                isMore = true;
                mRadioAdapter.setLoadState(mRadioAdapter.LOADING);
                page++;

                if (page < pageTotal | page == pageTotal) {

                    getData(page);

                } else {
                    // 显示加载到底的提示
                    mRadioAdapter.setLoadState(mRadioAdapter.LOADING_END);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }
    private void getData(int page){
        if (isNetworkConnected(getActivity())) {
            OkHttpUtils
                    .get()
                    .url(HttpUrls.GETDATA)
                    .addParams("user_number", SPUtils.getString(Constant.USERID, ""))
                    .addParams("page",page+"")
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
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

                                if (message.equals("请求成功！")) {
                                    JSONArray array = jsonObject.getJSONArray("list");
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject data = array.optJSONObject(i);
                                        item = new DataItem();
                                        item.setId(data.optInt("id"));
                                        item.setData_lai(data.optString("data_lai"));
                                        item.setData_address(data.optString("data_address"));
                                        item.setData_munsell(data.optString("data_munsell"));
                                        item.setData_lon(data.optString("data_lon"));
                                        item.setData_lat(data.optString("data_lat"));

                                        String url = data.optString("data_image");
                                        if (url.contains("/media/media/")){
                                            url = url.replace("/media/media/","/media/");
                                        }
                                        item.setData_image(HttpUrls.IMAGE + url);
                                        item.setData_day(data.optString("data_day"));
                                        item.setData_duty(data.optString("data_duty"));
                                        item.setData_model(data.optString("data_model"));
                                        item.setData_cost(data.optString("data_cost"));
                                        item.setData_moshi(data.optString("data_moshi"));
                                        item.setData_ping_lai(data.optString("data_ping_lai"));
                                        item.setData_moshi(data.optString("data_moshi"));
                                        item.setName(data.optString("data_name"));
                                        mDataList.add(item);
                                        Log.e("wzj", "3333333333333: " + data.optString("data_name"));
                                    }
                                }
                                recyclerView.setAdapter(mRadioAdapter);
                                mRadioAdapter.notifyAdapter(mDataList, false);
                                mRadioAdapter.notifyDataSetChanged();
                                if (isMore){
                                    Log.e("wzj","222222222222222222222222222++++++++++++++++++++： ");
                                    mRadioAdapter.setLoadState(mRadioAdapter.LOADING_COMPLETE);
                                }
                                //关闭下拉刷新
                                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                                    swipeRefreshLayout.setRefreshing(false);
                                }

                                mRadioAdapter.setOnItemClickListener(new DataListAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClickListener(int pos, List<DataItem> myLiveList) {
                                        if (ll_mycollection_bottom_dialog.getVisibility() == View.GONE){
                                            Intent intent = new Intent();
                                            intent.setClass(getActivity(), DataActivity.class);
                                            intent.putExtra("image", myLiveList.get(pos).getData_image());
                                            intent.putExtra("address", myLiveList.get(pos).getData_address());
                                            intent.putExtra("LAI", myLiveList.get(pos).getData_lai());
                                            intent.putExtra("model", myLiveList.get(pos).getData_model());
                                            intent.putExtra("color", myLiveList.get(pos).getData_munsell());
                                            intent.putExtra("time", myLiveList.get(pos).getData_day());
                                            intent.putExtra("id", myLiveList.get(pos).getId() + "");
                                            intent.putExtra("is_true", "f");
                                            intent.putExtra("name",myLiveList.get(pos).getName());
                                            startActivity(intent);
                                        }else {
                                            if (editorStatus) {
                                                DataItem myLive = myLiveList.get(pos);
                                                boolean isSelect = myLive.isSelect();
                                                if (!isSelect) {
                                                    index++;
                                                    myLive.setSelect(true);
                                                    if (index == myLiveList.size()) {
                                                        isSelectAll = true;
                                                        select_all.setText("取消全选");
                                                    }

                                                } else {
                                                    myLive.setSelect(false);
                                                    index--;
                                                    isSelectAll = false;
                                                    select_all.setText("全选");
                                                }
                                                setBtnBackground(index);
                                                tv_select_num.setText(String.valueOf(index));
                                                mRadioAdapter.notifyDataSetChanged();
                                            }
                                        }

                                    }
                                });

                                mRadioAdapter.setOnItemLongClickListener(new DataListAdapter.OnItemLongClickLinstener() {
                                    @Override
                                    public void onItemLongClickListener(int pos, List<DataItem> myLiveList) {
                                        ll_mycollection_bottom_dialog.setVisibility(View.VISIBLE);
                                        rTitle.setVisibility(View.VISIBLE);
                                        mEditMode = mEditMode == MYLIVE_MODE_CHECK ? MYLIVE_MODE_EDIT : MYLIVE_MODE_CHECK;
                                        mRadioAdapter.setEditMode(mEditMode);
                                        editorStatus = true;
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    });
        } else {
            List<ZhiWuData> list = DataSupport.findAll(ZhiWuData.class);
            for (int i = 0; i < list.size(); i++) {
                item = new DataItem();
                item.setData_lai(list.get(i).getData_lai());
                item.setData_address(list.get(i).getData_address());
                item.setData_munsell(list.get(i).getData_munsell());
                item.setData_lon(list.get(i).getData_lon());
                item.setData_lat(list.get(i).getData_lat());
                item.setData_image(list.get(i).getImage_url());
                item.setData_day(list.get(i).getData_day());
                item.setData_duty(list.get(i).getData_duty());
                item.setData_model(list.get(i).getData_model());
                item.setData_cost(list.get(i).getData_cost());
                item.setData_ping_lai(list.get(i).getData_ping_lai());
                item.setData_moshi(list.get(i).getMoshi());
                item.setName(list.get(i).getName());
                mDataList.add(item);
                recyclerView.setAdapter(mRadioAdapter);
                mRadioAdapter.notifyAdapter(mDataList, false);
            }
            mRadioAdapter.setOnItemClickListener(new DataListAdapter.OnItemClickListener() {
                @Override
                public void onItemClickListener(int pos, List<DataItem> myLiveList) {
                    if (ll_mycollection_bottom_dialog.getVisibility() == View.GONE){
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), DataActivity.class);
                        intent.putExtra("image", myLiveList.get(pos).getData_image());
                        intent.putExtra("address", myLiveList.get(pos).getData_address());
                        intent.putExtra("LAI", myLiveList.get(pos).getData_lai());
                        intent.putExtra("model", myLiveList.get(pos).getData_model());
                        intent.putExtra("color", myLiveList.get(pos).getData_munsell());
                        intent.putExtra("time", myLiveList.get(pos).getData_day());
                        intent.putExtra("id", myLiveList.get(pos).getId() + "");
                        intent.putExtra("is_true", "f");
                        intent.putExtra("name",myLiveList.get(pos).getName());
                        startActivity(intent);
                    }else {
                        if (editorStatus) {
                            DataItem myLive = myLiveList.get(pos);
                            boolean isSelect = myLive.isSelect();
                            if (!isSelect) {
                                index++;
                                myLive.setSelect(true);
                                if (index == myLiveList.size()) {
                                    isSelectAll = true;
                                    select_all.setText("取消全选");
                                }

                            } else {
                                myLive.setSelect(false);
                                index--;
                                isSelectAll = false;
                                select_all.setText("全选");
                            }
                            setBtnBackground(index);
                            tv_select_num.setText(String.valueOf(index));
                            mRadioAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
            mRadioAdapter.setOnItemLongClickListener(new DataListAdapter.OnItemLongClickLinstener() {
                @Override
                public void onItemLongClickListener(int pos, List<DataItem> myLiveList) {
                    ll_mycollection_bottom_dialog.setVisibility(View.VISIBLE);
                    rTitle.setVisibility(View.VISIBLE);
                    mEditMode = mEditMode == MYLIVE_MODE_CHECK ? MYLIVE_MODE_EDIT : MYLIVE_MODE_CHECK;
                    mRadioAdapter.setEditMode(mEditMode);
                    editorStatus = true;
                }
            });

        }
    }

    /**
     * 根据选择的数量是否为0来判断按钮的是否可点击.
     *
     * @param size
     */
    private void setBtnBackground(int size) {
        if (size != 0) {
            btn_delete.setBackgroundResource(R.drawable.button_shape);
            btn_delete.setEnabled(true);
            btn_delete.setTextColor(Color.WHITE);
        } else {
            btn_delete.setBackgroundResource(R.drawable.button_noclickable_shape);
            btn_delete.setEnabled(false);
            btn_delete.setTextColor(ContextCompat.getColor(getContext(), R.color.color_b7b8bd));
        }
    }

    /**
     * 全选和反选
     */
    private void selectAllMain() {
        if (mRadioAdapter == null) return;
        if (!isSelectAll) {
            for (int i = 0, j = mRadioAdapter.getMyLiveList().size(); i < j; i++) {
                mRadioAdapter.getMyLiveList().get(i).setSelect(true);
            }
            index = mRadioAdapter.getMyLiveList().size();
            btn_delete.setEnabled(true);
            select_all.setText("取消全选");
            isSelectAll = true;
        } else {
            for (int i = 0, j = mRadioAdapter.getMyLiveList().size(); i < j; i++) {
                mRadioAdapter.getMyLiveList().get(i).setSelect(false);
            }
            index = 0;
            btn_delete.setEnabled(false);
            select_all.setText("全选");
            isSelectAll = false;
        }
        mRadioAdapter.notifyDataSetChanged();
        setBtnBackground(index);
        tv_select_num.setText(String.valueOf(index));
    }

    /**
     * 删除逻辑
     */
    private void deleteVideo() {
        if (index == 0) {
            btn_delete.setEnabled(false);
            return;
        }
        final AlertDialog builder = new AlertDialog.Builder(getContext())
                .create();
        builder.show();
        if (builder.getWindow() == null) return;
        builder.getWindow().setContentView(R.layout.pop_user);//设置弹出框加载的布局
        TextView msg = (TextView) builder.findViewById(R.id.tv_msg);
        Button cancle = (Button) builder.findViewById(R.id.btn_cancle);
        Button sure = (Button) builder.findViewById(R.id.btn_sure);
        if (msg == null || cancle == null || sure == null) return;

        if (index == 1) {
            msg.setText("删除后不可恢复，是否删除该条目？");
        } else {
            msg.setText("删除后不可恢复，是否删除这" + index + "个条目？");
        }
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = mRadioAdapter.getMyLiveList().size(), j = 0; i > j; i--) {
                    DataItem myLive = mRadioAdapter.getMyLiveList().get(i - 1);
                    if (myLive.isSelect()) {
                        OkHttpUtils
                                .post()
                                .url(HttpUrls.DELETECOMMENTSANDDATA)
                                .addParams("data_id", myLive.getId() + "")
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            String message = jsonObject.optString("message");
                                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                });
                        index--;
                    }
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDataList.clear();
                        page = 1;
                        Log.e("wzj","xxxxxxxxxxxxxxxxxx:   "+mDataList.size());
                        getData(page);
                    }
                }, 100);//3秒后执行Runnable中的run方法


                index = 0;
                tv_select_num.setText(String.valueOf(0));
                setBtnBackground(index);
                if (mRadioAdapter.getMyLiveList().size() == 0) {
                    ll_mycollection_bottom_dialog.setVisibility(View.GONE);
                }
                mRadioAdapter.notifyDataSetChanged();
                builder.dismiss();


                ll_mycollection_bottom_dialog.setVisibility(View.GONE);
                rTitle.setVisibility(View.GONE);
                editorStatus = false;
                mEditMode = mEditMode == MYLIVE_MODE_CHECK ? MYLIVE_MODE_EDIT : MYLIVE_MODE_CHECK;
                mRadioAdapter.setEditMode(mEditMode);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ll_mycollection_bottom_dialog.setVisibility(View.GONE);
        rTitle.setVisibility(View.GONE);
        editorStatus = false;
        mEditMode = mEditMode == MYLIVE_MODE_CHECK ? MYLIVE_MODE_EDIT : MYLIVE_MODE_CHECK;
        mRadioAdapter.setEditMode(mEditMode);
    }
}
