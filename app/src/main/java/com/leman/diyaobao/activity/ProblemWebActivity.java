package com.leman.diyaobao.activity;


import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leman.diyaobao.R;

public class ProblemWebActivity extends BaseActivity {
    private TextView title;
    private LinearLayout back;
    private WebView webView;
    @Override
    public int intiLayout() {
        return R.layout.activity_problem_web;
    }

    @Override
    public void initView() {
        title = findViewById(R.id.title);
        title.setText("Common problem");
        back = findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webView = (WebView) findViewById(R.id.web_problem_web);
        webView.loadUrl("file:///android_asset/problem.html");
    }

    @Override
    public void initData() {

    }
}
