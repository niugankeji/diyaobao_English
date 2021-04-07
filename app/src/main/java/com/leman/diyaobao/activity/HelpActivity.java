package com.leman.diyaobao.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.leman.diyaobao.Constant;
import com.leman.diyaobao.R;
import com.leman.diyaobao.adapter.TypeAdapter;
import com.leman.diyaobao.entity.TypeItem;
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

import static com.leman.diyaobao.fragment.CameraFragment.getTime;

/**
 * 帮助反馈
 */
public class HelpActivity extends BaseActivity {
    private TextView title;
    private LinearLayout back;

    private TextView type;
    private EditText content;
    private LinearLayout type_layout;
    private TextView uploade;

    private RecyclerView rv_list;
    private List<TypeItem> mDataList;

    @Override
    public int intiLayout() {
        return R.layout.activity_help;
    }

    @Override
    public void initView() {
        title = findViewById(R.id.title);
        title.setText("Help and feedback");

        type = findViewById(R.id.type);
        content = findViewById(R.id.content);
        type_layout = findViewById(R.id.type_layout);
        uploade = findViewById(R.id.uploade);


        back = findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        type_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(HelpActivity.this,ProblemWebActivity.class));
//            }
//        });

//        //选择问题类型
//        type_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mDataList = new ArrayList<>();
//                OkHttpUtils
//                        .get()
//                        .url(HttpUrls.GETFEEDBACKTYPE)
//                        .build()
//                        .execute(new StringCallback() {
//                            @Override
//                            public void onError(Call call, Exception e, int id) {
//                                //    Log.e("wzj", "1111111111111111111111: " + e.getMessage());
//                                Toast.makeText(HelpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void onResponse(String response, int id) {
//                                Log.e("wzj", "0000000000000: " + response);
//                                try {
//                                    JSONObject jsonObject = new JSONObject(response);
//                                    JSONArray array = jsonObject.optJSONArray("list");
//                                    for (int i = 0; i < array.length(); i++) {
//                                        TypeItem item = new TypeItem();
//                                        JSONObject object = array.getJSONObject(i);
//                                        item.setText(object.optString("feedbacktype_ontent"));
//                                        mDataList.add(item);
//                                    }
//
//                                    showPopupWindow(type_layout, mDataList, type);
//                                    //    Log.e("wzj", "3333333333333: " + message);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//
//                        });
//            }
//        });
        uploade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpUtils
                        .post()
                        .url(HttpUrls.UPLOADFEEDBACK)
                        .addParams("user_number", SPUtils.getString(Constant.USERID, ""))
                        .addParams("time", getTime())
                        .addParams("type", type.getText().toString())
                        .addParams("content", content.getText().toString())
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                //    Log.e("wzj", "1111111111111111111111: " + e.getMessage());
                                Toast.makeText(HelpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.e("wzj", "0000000000000: " + response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String message = jsonObject.optString("message");
                                    Toast.makeText(HelpActivity.this, message, Toast.LENGTH_SHORT).show();
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

    }

    private void showPopupWindow(View view, final List<TypeItem> list, final TextView textView) {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(HelpActivity.this).inflate(
                R.layout.pop_window, null);

        rv_list = contentView.findViewById(R.id.rv_list);
        rv_list.setLayoutManager(new LinearLayoutManager(HelpActivity.this));

        final PopupWindow popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        BaseQuickAdapter homeAdapter = new TypeAdapter(R.layout.type_item, mDataList);
        homeAdapter.openLoadAnimation();
        rv_list.setAdapter(homeAdapter);
        homeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                textView.setText(list.get(position).getText());
                popupWindow.dismiss();
            }
        });

        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#cfcfcf")));

        // 设置好参数之后再show
        popupWindow.showAsDropDown(view);

    }

}
