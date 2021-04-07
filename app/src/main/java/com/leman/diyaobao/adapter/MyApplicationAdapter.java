package com.leman.diyaobao.adapter;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.leman.diyaobao.R;
import com.leman.diyaobao.entity.HowSeeMeItem;
import com.leman.diyaobao.entity.MyApplicationItem;

import java.util.List;


public class MyApplicationAdapter extends BaseQuickAdapter<MyApplicationItem, BaseViewHolder> {


    public MyApplicationAdapter(int layoutResId, List data) {
        super(layoutResId, data);

    }

    @Override
    protected void convert(BaseViewHolder helper, final MyApplicationItem item) {
        helper.setText(R.id.name, item.getTo_user());
        Glide.with(mContext).load(item.getImageUrl()).crossFade().into((ImageView) helper.getView(R.id.civ_avatar));
        if (item.getState().equals("0")){
            helper.setText(R.id.state, "agree");
        }else if (item.getState().equals("1")){
            helper.setText(R.id.state, "refuse");
        }else if (item.getState().equals("2")){
            helper.setText(R.id.state, "Pending");
        }

    }

}


