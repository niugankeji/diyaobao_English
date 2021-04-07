package com.leman.diyaobao.numpicker;

import android.app.Activity;
import android.app.Dialog;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.leman.diyaobao.R;

/**
 * Author cjet
 * Date   2018-1-16 14:02
 */

public class NumPicker {

    private Activity mActivity;
    private TextView tvCancel;
    private TextView tvComfirm;
    private TextView tvTitle;
    private NumPickView mNpv;
    private Dialog mDialog;
    private OnCancelClickListener mCancelListener;
    private onComfirmClickListener mComfirmListener;
    private int currentSelecedNum;

    public NumPicker(Activity activity) {
        mActivity = activity;
        initDialog();

    }

    private void initDialog() {
        mDialog = new Dialog(mActivity, R.style.time_dialog);
        mDialog.setContentView(mActivity.getLayoutInflater().inflate(R.layout.popu_num_picker, null));
        Display dd = mActivity.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        dd.getMetrics(dm);
        WindowManager.LayoutParams attributes = mDialog.getWindow().getAttributes();
        mDialog.getWindow().setGravity(Gravity.BOTTOM);
        attributes.height = (int) (dm.heightPixels * 0.4);
        attributes.width = dm.widthPixels;
        mDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnimation);

        tvCancel = mDialog.findViewById(R.id.tvCancel);
        tvComfirm = mDialog.findViewById(R.id.tvConfirm);
        tvTitle = mDialog.findViewById(R.id.tvTitle);
        mNpv = mDialog.findViewById(R.id.numPickView);
        currentSelecedNum = mNpv.getCurrentPostion();
        setListener();
    }

    private void setListener() {
        mNpv.setOnSelectNumListener(new NumPickView.OnSelectNumListener() {
            @Override
            public void onSelected(int num) {
                currentSelecedNum = num;
            }
        });

        tvComfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mComfirmListener != null) {
                    mComfirmListener.onClick(currentSelecedNum);
                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCancelListener != null) {
                    mCancelListener.onClick();
                }

            }
        });
    }

    public void show() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.cancel();
        }
    }

    public void selecNum(int num) {
        mNpv.select(num);
    }

    public void setOnCancelListener(OnCancelClickListener listener) {
        this.mCancelListener = listener;
    }

    public void setOnComfirmListener(onComfirmClickListener listener) {
        this.mComfirmListener = listener;
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public interface OnCancelClickListener {
        void onClick();
    }

    public interface onComfirmClickListener {
        void onClick(int num);
    }


}
