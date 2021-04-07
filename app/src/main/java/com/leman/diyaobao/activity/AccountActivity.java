package com.leman.diyaobao.activity;


import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leman.diyaobao.R;

public class AccountActivity extends BaseActivity {
    private TextView title;
    private LinearLayout back;
    private LinearLayout modifyPhone;
    private LinearLayout modifyPassword;
    @Override
    public int intiLayout() {
        return R.layout.activity_account;
    }

    @Override
    public void initView() {
        title = findViewById(R.id.title);
        title.setText("Accounts and security");
        back = findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        modifyPhone = findViewById(R.id.modifyPhone);
        modifyPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountActivity.this,ModifyPhoneActivity.class));
            }
        });
        modifyPassword = findViewById(R.id.modifyPassword);
        modifyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountActivity.this,ModifyPasswordActivity.class));
            }
        });
    }

    @Override
    public void initData() {

    }
}
