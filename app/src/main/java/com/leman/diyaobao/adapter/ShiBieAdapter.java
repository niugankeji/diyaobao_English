package com.leman.diyaobao.adapter;


import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.leman.diyaobao.R;
import com.leman.diyaobao.entity.DataItem;
import com.leman.diyaobao.entity.ShiBie;

import java.util.List;


public class ShiBieAdapter extends BaseQuickAdapter<ShiBie, BaseViewHolder> {

    public ShiBieAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ShiBie item) {

        helper.setText(R.id.name, item.getName());
    //    helper.setText(R.id.code, item.getScore());

    }
}


