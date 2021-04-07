package com.leman.diyaobao.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.leman.diyaobao.R;

public class ApkDialog extends Dialog implements View.OnClickListener {

    private TextView titleTxt;
    private TextView tv_update;
    private TextView tv2;

    private Context mContext;
    private String content;
    private OnCloseListener listener;
    private String title;

    public ApkDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public ApkDialog(Context context, int themeResId, String content) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
    }

    public ApkDialog(Context context, int themeResId, String title, String content, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
        this.listener = listener;
        this.title = title;
    }

    protected ApkDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public ApkDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_apk);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView() {

        titleTxt = (TextView) findViewById(R.id.title);
        tv_update = (TextView) findViewById(R.id.tv_update);
        tv_update.setOnClickListener(this);
        tv2 = (TextView) findViewById(R.id.tv2);

        if (!TextUtils.isEmpty(content)) {
            tv2.setText(content);
        }

        if (!TextUtils.isEmpty(title)) {
            titleTxt.setText(title);
        }

        setCancelable(false);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_update:
                if (listener != null) {
                    listener.onClick();
                }
                dismiss();
                break;
        }
    }

    public interface OnCloseListener {
        void onClick();
    }
}
