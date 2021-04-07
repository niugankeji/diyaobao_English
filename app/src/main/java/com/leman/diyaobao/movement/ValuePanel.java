package com.leman.diyaobao.movement;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.leman.diyaobao.R;


/**
 * 数值面板
 *
 * @author jiaxin on 2017/8/14.
 */

public class ValuePanel extends FrameLayout {

    private TextView tvPanelTitle;
    private TextView tvPanelValue;

    public ValuePanel(@NonNull Context context) {
        super(context);
    }

    public ValuePanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);

    }

    public ValuePanel(@NonNull Context context, @Nullable AttributeSet attrs,
                      @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_value_panel, this);
        tvPanelTitle = (TextView)findViewById(R.id.tv_panel_title);
        tvPanelValue = (TextView)findViewById(R.id.tv_panel_value);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ValuePanel);
        String panelTitle = ta.getString(R.styleable.ValuePanel_panel_title);
        String panelValue = ta.getString(R.styleable.ValuePanel_panel_value);

        if(!TextUtils.isEmpty(panelTitle)){
            tvPanelTitle.setText(panelTitle);
        }
        if(!TextUtils.isEmpty(panelValue)){
            tvPanelValue.setText(panelValue);
        }
        ta.recycle();
    }

    public void setPanelTitle(@StringRes int resId){
        setPanelTitle(getResources().getString(resId));
    }


    public void setPanelTitle(String resStr){
        tvPanelTitle.setText(resStr);
    }

    public void setPanelValue(@StringRes int resId){
        setPanelValue(getResources().getString(resId));
    }


    public void setPanelValue(String resStr){
        tvPanelValue.setText(resStr);
    }

    public String getPanelValue(){
        return tvPanelValue.getText().toString();
    }
}
