package com.leman.diyaobao.myview;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.leman.diyaobao.R;

public class PhotoPopWindow extends PopupWindow {
    private final View view;
    private Activity context;
    WindowOnClickLinster mwindowOnClickLinster;

    public PhotoPopWindow(Activity context) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.widget_popupwindow, null);//alt+ctrl+f
        initView();
        initPopWindow();
    }


    private void initView() {
        LinearLayout item_popupwindows_camera = view.findViewById(R.id.item_popupwindows_camera);
        item_popupwindows_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mwindowOnClickLinster.onClickCamera();
                dismiss();
            }
        });
        LinearLayout item_popupwindows_Photo = view.findViewById(R.id.item_popupwindows_Photo);
        item_popupwindows_Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mwindowOnClickLinster.onClickPhoto();
                dismiss();
            }
        });
        TextView item_popupwindows_cancel = view.findViewById(R.id.item_popupwindows_cancel);
        item_popupwindows_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    private void initPopWindow() {
        this.setContentView(view);
        // 设置弹出窗体的宽
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        // 设置弹出窗体的高
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置弹出窗体可点击()
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.mypopwindow_anim_style);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00FFFFFF);
        //设置弹出窗体的背景
        this.setBackgroundDrawable(dw);
      //  backgroundAlpha(context, 0.5f);//0.0-1.0
    }

    /**
     * 设置添加屏幕的背景透明度(值越大,透明度越高)
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    public  interface WindowOnClickLinster{
        public void onClickCamera();
        public void onClickPhoto();
    }

    public  void OnClickLinster(WindowOnClickLinster windowOnClickLinster){
        mwindowOnClickLinster = windowOnClickLinster;
    }


}
