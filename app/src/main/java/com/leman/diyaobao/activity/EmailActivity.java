package com.leman.diyaobao.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leman.diyaobao.R;

public class EmailActivity extends BaseActivity {

    private TextView title;
    private LinearLayout back;

    @Override
    public int intiLayout() {
        return R.layout.activity_email;
    }

    @Override
    public void initView() {
        title = findViewById(R.id.title);
        title.setText("Email");
        back = findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initData() {

    }
}
