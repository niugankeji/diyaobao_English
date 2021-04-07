package com.leman.diyaobao.movement;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;


import com.leman.diyaobao.R;

import java.util.Calendar;

/**
 * 创建日期：18/12/4 下午5:10
 * 描述:
 * 作者: 赵伟闯
 */
public class AppUtils {


    public static String OPENID;
    public static int currentID;
    public static String currenValue;
    public static final String STEP_DAY = "step_DAY";
    public static final String STEP_9 = "step_9";
    public static final String STEP_12 = "step_12";
    public static final String STEP_16 = "step_16";
    public static final String STEP_18 = "step_18";
    public static final String STEP_22 = "step_22";
    public static final String STEP_24 = "step_24";


    /**
     * 展示分享弹窗
     *
     * @param context
     * @param listener
     */
    public static void showShareDialog(Activity context, View.OnClickListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.share_layout, null);
        final Dialog dialog2 = new Dialog(context, R.style.transparentFrameWindowStyle1);
        dialog2.setContentView(view, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));


        view.findViewById(R.id.lay_chat_friend).setOnClickListener(listener);
        view.findViewById(R.id.lay_qq).setOnClickListener(listener);
        view.findViewById(R.id.lay_sina).setOnClickListener(listener);
        view.findViewById(R.id.lay_chat_circle).setOnClickListener(listener);
        view.findViewById(R.id.lay_qq_zone).setOnClickListener(listener);
        view.findViewById(R.id.lay_link).setOnClickListener(listener);


        Window window = dialog2.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        window.setGravity(Gravity.BOTTOM);
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog2.onWindowAttributesChanged(wl);
        dialog2.setCanceledOnTouchOutside(true);
        dialog2.show();
    }

    public static void saveStemp(Context context, int temps) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        int mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        if (ShareUtils.getInt(context, STEP_DAY, 0) != day) {
            ShareUtils.putInt(context, STEP_DAY, day);
            ShareUtils.putInt(context, STEP_9, 0);
            ShareUtils.putInt(context, STEP_12, 0);
            ShareUtils.putInt(context, STEP_16, 0);
            ShareUtils.putInt(context, STEP_18, 0);
            ShareUtils.putInt(context, STEP_22, 0);
            ShareUtils.putInt(context, STEP_24, 0);
        }

        int everStep;
        if (mHour <= 9) {
            ShareUtils.putInt(context, STEP_9, temps);
        } else if (mHour <= 12) {
            everStep = ShareUtils.getInt(context, STEP_9, 0);
            ShareUtils.putInt(context, STEP_12, temps - everStep);
        } else if (mHour <= 16) {
            everStep = ShareUtils.getInt(context, STEP_12, 0);
            ShareUtils.putInt(context, STEP_16, temps - everStep);
        } else if (mHour <= 18) {
            everStep = ShareUtils.getInt(context, STEP_16, 0);
            ShareUtils.putInt(context, STEP_18, temps - everStep);
        } else if (mHour <= 22) {
            everStep = ShareUtils.getInt(context, STEP_18, 0);
            ShareUtils.putInt(context, STEP_22, temps - everStep);
        } else if (mHour <= 24) {
            everStep = ShareUtils.getInt(context, STEP_22, 0);
            ShareUtils.putInt(context, STEP_24, temps - everStep);
        }
    }
}
