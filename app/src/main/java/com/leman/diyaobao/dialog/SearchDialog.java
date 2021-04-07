package com.leman.diyaobao.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.leman.diyaobao.R;

public class SearchDialog extends Dialog implements View.OnClickListener{
    private TextView contentTxt;
    private TextView titleTxt;
    private TextView submitTxt;
    private TextView id_search;
    private TextView name_search;
    private TextView quxiao;

    private Context mContext;
    private OnCloseListener listener;
    private String positiveName;
    private String negativeName;
    private String title;

    public SearchDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public SearchDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;

    }

    public SearchDialog(Context context, int themeResId, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.listener = listener;
    }

    protected SearchDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public SearchDialog setTitle(String title){
        this.title = title;
        return this;
    }

//    public SearchDialog setPositiveButton(String name){
//        this.positiveName = name;
//        return this;
//    }
//
//    public SearchDialog setNegativeButton(String name){
//        this.negativeName = name;
//        return this;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_search);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView(){
        contentTxt = findViewById(R.id.content);
        titleTxt = findViewById(R.id.title);
        id_search = findViewById(R.id.id_search);
        id_search.setOnClickListener(this);
        name_search = findViewById(R.id.name_search);
        name_search.setOnClickListener(this);
        quxiao = findViewById(R.id.quxiao);
        quxiao.setOnClickListener(this);

//        if(!TextUtils.isEmpty(positiveName)){
//            submitTxt.setText(positiveName);
//        }
//
//        if(!TextUtils.isEmpty(negativeName)){
//            cancelTxt.setText(negativeName);
//        }
//
//        if(!TextUtils.isEmpty(title)){
//            titleTxt.setText(title);
//        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_search:
                if(listener != null){
                    listener.IdClick();
                }
                this.dismiss();
                break;
            case R.id.name_search:
                if(listener != null){
                    listener.NameonClick();
                }
                this.dismiss();
                break;
            case R.id.quxiao:
                this.dismiss();
                break;
        }
    }

    public interface OnCloseListener{
        void NameonClick();
        void IdClick();
    }
}
