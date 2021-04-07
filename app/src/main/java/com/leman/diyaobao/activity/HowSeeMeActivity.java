package com.leman.diyaobao.activity;


import android.app.Dialog;
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
import com.leman.diyaobao.adapter.HowSeeMeAdapter;
import com.leman.diyaobao.entity.HowSeeMeItem;
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

import static com.leman.diyaobao.fragment.CameraFragment.getTime;

public class HowSeeMeActivity extends BaseActivity {
    private TextView title;
    private LinearLayout back;

    private RecyclerView rv_list;
    private List<HowSeeMeItem> mDataList;
    HowSeeMeItem item;

    @Override
    public int intiLayout() {
        return R.layout.activity_how_see_me;
    }

    @Override
    public void initView() {
        title = findViewById(R.id.title);
        title.setText("Who views my data");
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
                .url(HttpUrls.GETOTHERAPPLYLIST)
                .addParams("to_user", SPUtils.getString(Constant.USERID, ""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(HowSeeMeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                                    item = new HowSeeMeItem();
                                    item.setImageUrl(HttpUrls.IMAGE + user.optString("image"));
                                    item.setState(user.optString("state"));
                                    item.setFrom_user(user.optString("from_user"));
                                    item.setTo_user(user.optString("to_user"));
                                    item.setId(user.optString("id"));
                                    mDataList.add(item);
                                }
                                BaseQuickAdapter homeAdapter = new HowSeeMeAdapter(R.layout.how_sew_me_item, mDataList);
                                homeAdapter.openLoadAnimation();
                                HowSeeMeAdapter.OnClick(new HowSeeMeAdapter.OnCloseListener() {
                                    @Override
                                    public void onClick(final String id, final TextView view) {
                                        if (view.getText().toString().equals("待处理")) {
                                            new SelectDialog(HowSeeMeActivity.this, R.style.dialog, new SelectDialog.OnCloseListener() {
                                                @Override
                                                public void onClick(Dialog dialog, boolean confirm, final int FeatureMode) {
                                                    if (confirm) {
                                                        Log.e("wzj", "cccccccccccccc: " + id);
                                                        Log.e("wzj", "sssssssssssssss: " + FeatureMode);

                                                        OkHttpUtils
                                                                .post()
                                                                .url(HttpUrls.UPLOADAPPLYSTATE)
                                                                .addParams("id", id)
                                                                .addParams("state_code", FeatureMode + "")
                                                                .addParams("time", getTime())
                                                                .build()
                                                                .execute(new StringCallback() {
                                                                    @Override
                                                                    public void onError(Call call, Exception e, int id) {
                                                                        Toast.makeText(HowSeeMeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }

                                                                    @Override
                                                                    public void onResponse(String response, int id) {
                                                                        Log.e("wzj", "0000000000000: " + response);
                                                                        try {
                                                                            JSONObject jsonObject = new JSONObject(response);
                                                                            String message = jsonObject.optString("message");
                                                                            Toast.makeText(HowSeeMeActivity.this, message, Toast.LENGTH_SHORT).show();
                                                                            if (FeatureMode == 0) {
                                                                                view.setText("同意");
                                                                            } else if (FeatureMode == 1) {
                                                                                view.setText("拒绝");
                                                                            }

                                                                        } catch (JSONException e) {
                                                                            e.printStackTrace();
                                                                        }

                                                                    }

                                                                });


                                                    }


                                                    dialog.dismiss();

                                                }
                                            }).setTitle("选择").show();
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
