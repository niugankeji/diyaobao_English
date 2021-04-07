package com.leman.diyaobao.activity;


import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leman.diyaobao.R;
import com.leman.diyaobao.utils.SPUtils;

public class ImageActivity extends BaseActivity {
    private TextView title;
    private LinearLayout back;
    private ImageView image;
    @Override
    public int intiLayout() {
        return R.layout.activity_image;
    }

    @Override
    public void initView() {
        title = findViewById(R.id.title);
        title.setText("Picture details");
        back = findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        image = findViewById(R.id.image);
        Glide.with(this).load(SPUtils.getString("image","")).into(image);
        image.setOnClickListener(new View.OnClickListener() {
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
