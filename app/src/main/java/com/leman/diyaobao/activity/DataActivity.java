package com.leman.diyaobao.activity;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.leman.diyaobao.Constant;
import com.leman.diyaobao.R;
import com.leman.diyaobao.adapter.CommentsAdapter;
import com.leman.diyaobao.biaoqing.EmotionKeyboard;
import com.leman.diyaobao.biaoqing.viewpager.EmotionAdapter;
import com.leman.diyaobao.biaoqing.viewpager.GlobalOnItemClickManager;
import com.leman.diyaobao.entity.CommentsItem;
import com.leman.diyaobao.okhttp.HttpUrls;
import com.leman.diyaobao.dialog.CommomDialog;
import com.leman.diyaobao.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

import static com.leman.diyaobao.fragment.CameraFragment.getTime;

public class DataActivity extends BaseActivity {

    private TextView title;
    private LinearLayout back;
    private ImageView image;

    private TextView info;
    private TextView time;

    private EditText content;
    private RecyclerView rv_list;
    private RelativeLayout comments_layout;
    private Button send;
    private ImageView biaoqing;

    private List<CommentsItem> mDataList;
    CommentsItem item;

    Intent intent;

    private LinearLayout like_layout;
    private ImageView like_image;
    private TextView like_text;

    private FrameLayout emotionView;
    private EmotionKeyboard emotionKeyboard;
    private static final int emsNumOfEveryFragment = 20;//?????????????????????
    private RadioGroup rgTipPoints;
    private RadioButton rbPoint;

    private NestedScrollView nestedscrollview;



    @Override
    public int intiLayout() {
        return R.layout.activity_data;
    }

    @Override
    public void initView() {
        title = findViewById(R.id.title);
        title.setText("Data details");
        info = findViewById(R.id.info);
        time = findViewById(R.id.time);

        emotionView = (FrameLayout) findViewById(R.id.emotion_layout);
        nestedscrollview = findViewById(R.id.nestedscrollview);


        back = findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        send = findViewById(R.id.send);

        content = findViewById(R.id.content);
        comments_layout = findViewById(R.id.comments_layout);

        rv_list = findViewById(R.id.rv_list);
        rv_list.setLayoutManager(new LinearLayoutManager(this));

        biaoqing = findViewById(R.id.biaoqing);




        intent = getIntent();
        Log.e("wzj","-------------------------: "+intent.getStringExtra("name"));
        info.setText("????????????:" + intent.getStringExtra("address") + "LAI:" + intent.getStringExtra("LAI") + " ?????????" + intent.getStringExtra("model") + "?????????" + intent.getStringExtra("color") + "???????????????" + intent.getStringExtra("name"));
        if (intent.getStringExtra("is_true").equals("true")) {
            time.setEnabled(false);
            time.setText(intent.getStringExtra("time"));
        } else {
            time.setEnabled(true);
            time.setText(intent.getStringExtra("time") + "      ??????");
        }
        image = findViewById(R.id.image);
        Glide.with(this)
                .load(intent.getStringExtra("image"))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL) //????????????
                .into(new BitmapImageViewTarget(image) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(50); //??????????????????
                        image.setImageDrawable(circularBitmapDrawable);
                    }
                });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtils.putString("image",intent.getStringExtra("image"));
                startActivity(new Intent(DataActivity.this,ImageActivity.class));
            }
        });

        like_layout = findViewById(R.id.like_layout);
        like_image = findViewById(R.id.like_image);
        like_text = findViewById(R.id.like_text);
        if (SPUtils.getString(Constant.ISLIKE, "3").equals("1")) {
            Glide.with(DataActivity.this).load(R.drawable.love).into(like_image);
        } else {
            Glide.with(DataActivity.this).load(R.drawable.love_1).into(like_image);
        }
        like_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SPUtils.getString(Constant.ISLIKE, "3").equals("1")) {
                    //????????????
                    OkHttpUtils
                            .post()
                            .url(HttpUrls.MYLIKE)
                            .addParams("user_number", SPUtils.getString(Constant.USERID, ""))
                            .addParams("data_id", intent.getStringExtra("id"))
                            .addParams("like", "0")
                            .addParams("time", getTime())
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    Toast.makeText(DataActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    Log.e("wzj", "xxxxxxxxxxxxxxxxxxxxxx: " + response);
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String message = jsonObject.optString("message");
                                        Toast.makeText(DataActivity.this, message, Toast.LENGTH_SHORT).show();
                                        SPUtils.putString(Constant.ISLIKE, jsonObject.optString("state"));
                                        like_text.setText(jsonObject.optJSONArray("list").length() + " ???");
                                        if (jsonObject.optString("state").equals("1")) {
                                            Glide.with(DataActivity.this).load(R.drawable.love).into(like_image);
                                        } else {
                                            Glide.with(DataActivity.this).load(R.drawable.love_1).into(like_image);
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                            });
                } else {
                    //??????
                    OkHttpUtils
                            .post()
                            .url(HttpUrls.MYLIKE)
                            .addParams("user_number", SPUtils.getString(Constant.USERID, ""))
                            .addParams("data_id", intent.getStringExtra("id"))
                            .addParams("like", "1")
                            .addParams("time", getTime())
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    Toast.makeText(DataActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    Log.e("wzj", "xxxxxxxxxxxxxxxxxxxxxx: " + response);
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String message = jsonObject.optString("message");
                                        Toast.makeText(DataActivity.this, message, Toast.LENGTH_SHORT).show();
                                        SPUtils.putString(Constant.ISLIKE, jsonObject.optString("state"));
                                        like_text.setText(jsonObject.optJSONArray("list").length() + " ???");
                                        if (jsonObject.optString("state").equals("1")) {
                                            Glide.with(DataActivity.this).load(R.drawable.love).into(like_image);
                                        } else {
                                            Glide.with(DataActivity.this).load(R.drawable.love_1).into(like_image);
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                            });
                }

            }
        });

        bindToEmotionKeyboard();
        content.addTextChangedListener(new ButtonBtnWatcher());//????????????EditText
    }

    private void bindToEmotionKeyboard() {
        emotionKeyboard = EmotionKeyboard.with(this)
                .setEmotionView(emotionView)
                .bindToContent(nestedscrollview)
                .bindToEditText(content)
                .bindToEmotionButton(biaoqing)
                .build();
        setUpEmotionViewPager();
    }

    @Override
    public void initData() {
        getPingList();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (content.getText().toString().equals("") | content.getText().toString() == null) {
                    Toast.makeText(DataActivity.this, "????????????????????????", Toast.LENGTH_SHORT).show();
                } else {

                    OkHttpUtils
                            .post()
                            .url(HttpUrls.UPLOADECOMMENTS)
                            .addParams("user_number", SPUtils.getString(Constant.USERID, ""))
                            .addParams("data_id", intent.getStringExtra("id"))
                            .addParams("content", content.getText().toString())
                            .addParams("time", getTime())
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    Toast.makeText(DataActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(String response, int id) {

                                    if (isSoftShowing()) {
                                        final View v = DataActivity.this.getWindow().peekDecorView();
                                        if (v != null && v.getWindowToken() != null) {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                                        }
                                    }

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String message = jsonObject.optString("message");
                                        Toast.makeText(DataActivity.this, message, Toast.LENGTH_SHORT).show();
                                        content.setText("");
                                        emotionView.setVisibility(View.GONE);
                                        getPingList();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                            });
                }

            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CommomDialog(DataActivity.this, R.style.dialog, "??????????????????????????????????????????", new CommomDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm) {
                            OkHttpUtils
                                    .post()
                                    .url(HttpUrls.DELETECOMMENTSANDDATA)
                                    .addParams("data_id", intent.getStringExtra("id"))
                                    .build()
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onError(Call call, Exception e, int id) {
                                            Toast.makeText(DataActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onResponse(String response, int id) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                String message = jsonObject.optString("message");
                                                Toast.makeText(DataActivity.this, message, Toast.LENGTH_SHORT).show();
                                                finish();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }

                                    });
                        }
                        dialog.dismiss();
                    }

                }).setTitle("??????").show();
            }
        });

    }

    private boolean isSoftShowing() {
        //??????????????????????????????
        int screenHeight = this.getWindow().getDecorView().getHeight();
        //??????View???????????????bottom
        Rect rect = new Rect();
        //DecorView??????activity?????????view
        this.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        //???????????????????????????????????????????????????????????????screenHeight = rect.bottom + ????????????????????????
        //??????screenHeight*2/3????????????
        return screenHeight * 2 / 3 > rect.bottom;
    }

    private void getPingList(){
        mDataList = new ArrayList<>();
        OkHttpUtils
                .get()
                .url(HttpUrls.GETCOMMENTS)
                .addParams("data_id", intent.getStringExtra("id"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(DataActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("wzj", "aaaaaaaaaaaa: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.optString("message");
                            if (message.equals("???????????????")) {
                                JSONArray array = jsonObject.getJSONArray("list");
                                JSONArray like = jsonObject.optJSONArray("like");
                                for (int i = 0; i < like.length(); i++) {
                                    JSONObject object = like.getJSONObject(i);
                                    if (object.optString("user_id").equals(SPUtils.getString(Constant.USERID, ""))) {
                                        Glide.with(DataActivity.this).load(R.drawable.love).into(like_image);
                                    } else {
                                        Glide.with(DataActivity.this).load(R.drawable.love_1).into(like_image);
                                    }
                                }
                                like_text.setText(jsonObject.optJSONArray("like").length() + " ???");
                                if (array.length() == 0) {
                                    comments_layout.setVisibility(View.GONE);
                                } else {
                                    comments_layout.setVisibility(View.VISIBLE);
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject data = array.optJSONObject(i);
                                        item = new CommentsItem();
                                        item.setId(data.optString("id"));
                                        item.setContent(data.optString("content"));
                                        item.setUser(data.optString("user"));
                                        item.setTime(data.optString("time"));
                                        item.setUsername(data.optString("username"));
                                        mDataList.add(item);
                                    }
                                }
                            }
                            BaseQuickAdapter homeAdapter = new CommentsAdapter(R.layout.comments_item, mDataList);
                            homeAdapter.openLoadAnimation();
                            rv_list.setAdapter(homeAdapter);
                            homeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                                }
                            });


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                });
    }

    /* ?????????????????????????????? */
    private void setUpEmotionViewPager() {
        int fragmentNum;
		/*??????ems???????????????????????????  ???1 ????????????????????????
                         ??????20?????????  ?????????length?????????
                         ????????????????????????  ????????????????????????????????????
		 */
        int emsTotalNum = getSizeOfAssetsCertainFolder("ems") - 1;//???????????????(??????????????????)
        if (emsTotalNum % emsNumOfEveryFragment == 0) {
            fragmentNum = emsTotalNum / emsNumOfEveryFragment;
        } else {
            fragmentNum = (emsTotalNum / emsNumOfEveryFragment) + 1;
        }
        EmotionAdapter mViewPagerAdapter = new EmotionAdapter(getSupportFragmentManager(), fragmentNum);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setCurrentItem(0);

        GlobalOnItemClickManager globalOnItemClickListener = GlobalOnItemClickManager.getInstance();
        globalOnItemClickListener.attachToEditText((EditText) findViewById(R.id.content));

        /* ??????????????????????????? */
        setUpTipPoints(fragmentNum, mViewPager);
    }

    /**
     * @param num ??????????????????
     */
    private void setUpTipPoints(int num, ViewPager mViewPager) {
        rgTipPoints = (RadioGroup) findViewById(R.id.rg_reply_layout);
        for (int i = 0; i < num; i++) {
            rbPoint = new RadioButton(this);
            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(30, 30);
            lp.setMargins(10, 0, 10, 0);
            rbPoint.setLayoutParams(lp);
            rbPoint.setId(i);//?????????RadioButton????????????
            rbPoint.setButtonDrawable(getResources().getDrawable(R.color.transparent));//??????button???@null
            rbPoint.setBackgroundResource(R.drawable.emotion_tip_points_selector);
            rbPoint.setClickable(false);
            if (i == 0) { // ????????????????????????????????????????????????????????????
                rbPoint.setChecked(true);
            } else {
                rbPoint.setChecked(false);
            }
            rgTipPoints.addView(rbPoint);
        }
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                rgTipPoints.check(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!emotionKeyboard.interceptBackPress()) {
            super.onBackPressed();
        }
    }

    /* ??????assets?????????????????????????????????????????? */
    private int getSizeOfAssetsCertainFolder(String folderName) {
        int size = 0;
        try {
            size = getAssets().list(folderName).length;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }

    /* EditText????????????????????? */
    class ButtonBtnWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(content.getText().toString())) { //??????????????????????????????????????????
                Log.e("wzj", "++++++++++++++++++++: " + content.getText().toString());
                send.setBackgroundResource(R.drawable.shape_button_reply_button_clickable);
                send.setTextColor(getResources().getColor(R.color.light_white));
            } else { // ?????????????????????????????????????????????
                send.setBackgroundResource(R.drawable.shape_button_reply_button_unclickable);
                send.setTextColor(getResources().getColor(R.color.reply_button_text_disable));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

}
