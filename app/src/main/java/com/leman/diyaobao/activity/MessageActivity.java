package com.leman.diyaobao.activity;


import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leman.diyaobao.Constant;
import com.leman.diyaobao.R;
import com.leman.diyaobao.myview.SwitchButton;
import com.leman.diyaobao.utils.SPUtils;

/**
 * 消息通知
 */
public class MessageActivity extends BaseActivity {

    private TextView title;
    private LinearLayout back;
    private SwitchButton message;


    @Override
    public int intiLayout() {
        return R.layout.activity_message;
    }

    @Override
    public void initView() {
        title = findViewById(R.id.title);
        title.setText("Message");
        back = findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        message = findViewById(R.id.message);
        message.setChecked(SPUtils.getBoolean(Constant.IS_MESSAGE,true));
    }


    @Override
    public void initData() {
        message.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                Log.e("wzj", "推荐通讯录朋友..............： " + isChecked);
                if (isChecked) {
                    //推荐通讯录朋友
                    SPUtils.putBoolean(Constant.IS_MESSAGE, isChecked);
                } else {
                    SPUtils.putBoolean(Constant.IS_MESSAGE, isChecked);
                }
            }
        });
    }
}
