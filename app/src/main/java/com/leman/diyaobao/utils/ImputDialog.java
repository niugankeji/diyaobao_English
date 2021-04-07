package com.leman.diyaobao.utils;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.leman.diyaobao.R;

public class ImputDialog extends Dialog implements View.OnClickListener{
    private EditText contentTxt;
    private TextView titleTxt;
    private TextView submitTxt;
    private TextView cancelTxt;

    private Context mContext;
    private OnCloseListener listener;
    private String positiveName;
    private String negativeName;
    private String title;

    public ImputDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public ImputDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    public ImputDialog(Context context, int themeResId, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.listener = listener;
    }

    protected ImputDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public ImputDialog setTitle(String title){
        this.title = title;
        return this;
    }

    public ImputDialog setPositiveButton(String name){
        this.positiveName = name;
        return this;
    }

    public ImputDialog setNegativeButton(String name){
        this.negativeName = name;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_imput);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView(){
        contentTxt = (EditText) findViewById(R.id.content);
        titleTxt = (TextView)findViewById(R.id.title);
        submitTxt = (TextView)findViewById(R.id.submit);
        submitTxt.setOnClickListener(this);
        cancelTxt = (TextView)findViewById(R.id.cancel);
        cancelTxt.setOnClickListener(this);


        if(!TextUtils.isEmpty(positiveName)){
            submitTxt.setText(positiveName);
        }

        if(!TextUtils.isEmpty(negativeName)){
            cancelTxt.setText(negativeName);
        }

        if(!TextUtils.isEmpty(title)){
            titleTxt.setText(title);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                if(listener != null){
                    listener.onClick(this, false,contentTxt.getText().toString());
                }
                this.dismiss();
                break;
            case R.id.submit:
                if(listener != null){
                    listener.onClick(this, true,contentTxt.getText().toString());
                }
                break;
        }
    }

    public interface OnCloseListener{
        void onClick(Dialog dialog, boolean confirm,String text);
    }
}
