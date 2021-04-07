package com.leman.diyaobao.activity;

import android.content.Intent;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leman.diyaobao.R;

public class ServiceActivity extends BaseActivity {

    private TextView title;
    private LinearLayout back;
    private WebView webView2;

    @Override
    public int intiLayout() {
        return R.layout.activity_service;
    }

    @Override
    public void initView() {

        title = findViewById(R.id.title);

        back = findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webView2 = (WebView) findViewById(R.id.web_service);
        Intent intent = getIntent();
        if (intent.getStringExtra("title").equals("服务条款")) {
            title.setText("Terms of service");
            webView2.loadUrl("file:////android_asset/agreement.html");
        }
        if (intent.getStringExtra("title").equals("隐私协议")) {
            title.setText("Privacy agreement");
            webView2.loadUrl("file:////android_asset/yinsi.html");
        }


    }

    @Override
    public void initData() {

    }
}
