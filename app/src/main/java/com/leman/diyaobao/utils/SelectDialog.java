package com.leman.diyaobao.utils;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.leman.diyaobao.R;
import com.leman.diyaobao.myview.CheckBoxSample;

public class SelectDialog extends Dialog implements View.OnClickListener{
    private TextView titleTxt;
    private TextView submitTxt;
    private TextView cancelTxt;

    private CheckBoxSample agreement;
    private CheckBoxSample refuse;

    private Context mContext;
    private OnCloseListener listener;
    private String positiveName;
    private String negativeName;
    private String title;
    private int FeatureMode = 2;

    public SelectDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public SelectDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;

    }

    public SelectDialog(Context context, int themeResId, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.listener = listener;
    }

    protected SelectDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public SelectDialog setTitle(String title){
        this.title = title;
        return this;
    }

    public SelectDialog setPositiveButton(String name){
        this.positiveName = name;
        return this;
    }

    public SelectDialog setNegativeButton(String name){
        this.negativeName = name;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView(){

        titleTxt = (TextView)findViewById(R.id.title);
        submitTxt = (TextView)findViewById(R.id.submit);
        submitTxt.setOnClickListener(this);
        cancelTxt = (TextView)findViewById(R.id.cancel);
        cancelTxt.setOnClickListener(this);

        agreement = findViewById(R.id.agreement);
        agreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeatureMode = 0;
                agreement.toggle();
                refuse.setChecked(false);
            }
        });
        refuse = findViewById(R.id.refuse);
        refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refuse.toggle();
                FeatureMode = 1;
                agreement.setChecked(false);
            }
        });



        if(!TextUtils.isEmpty(positiveName)){
            submitTxt.setText(positiveName);
        }

        if(!TextUtils.isEmpty(negativeName)){
            cancelTxt.setText(negativeName);
        }

        if(!TextUtils.isEmpty(title)){
            titleTxt.setText(title);
        }

        switch (FeatureMode) {
            case 0:
                agreement.setChecked(true);
                break;
            case 1:
                refuse.setChecked(true);
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                if(listener != null){
                    listener.onClick(this, false,FeatureMode);
                }
                this.dismiss();
                break;
            case R.id.submit:
                if(listener != null){
                    listener.onClick(this, true,FeatureMode);
                }
                break;
        }
    }

    public interface OnCloseListener{
        void onClick(Dialog dialog, boolean confirm,int FeatureMode);
    }
}
