package com.leman.diyaobao.adapter;


import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.leman.diyaobao.R;
import com.leman.diyaobao.entity.HowSeeMeItem;

import java.util.List;


public class HowSeeMeAdapter extends BaseQuickAdapter<HowSeeMeItem, BaseViewHolder> {

    static OnCloseListener closeListener;

    public HowSeeMeAdapter(int layoutResId, List data) {
        super(layoutResId, data);

    }

    @Override
    protected void convert(final BaseViewHolder helper, final HowSeeMeItem item) {
        helper.setText(R.id.name, item.getFrom_user());
        Glide.with(mContext).load(item.getImageUrl()).crossFade().into((ImageView) helper.getView(R.id.civ_avatar));
        if (item.getState().equals("0")){
            helper.setText(R.id.state, "agree");
        }else if (item.getState().equals("1")){
            helper.setText(R.id.state, "refuse");
        }else if (item.getState().equals("2")){
            helper.setText(R.id.state, "Pending");
        }


        helper.getView(R.id.state).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("wzj","fffffffffffffffff: "+item.getId());
                closeListener.onClick(item.getId(), (TextView) helper.getView(R.id.state));
            }
        });

    }

    public interface OnCloseListener{
        void onClick(String id, TextView view);
    }

    public static void OnClick(OnCloseListener onCloseListener){
        closeListener = onCloseListener;
    }
}


