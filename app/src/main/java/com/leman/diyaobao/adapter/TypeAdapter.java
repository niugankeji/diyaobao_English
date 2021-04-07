package com.leman.diyaobao.adapter;


import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.leman.diyaobao.R;
import com.leman.diyaobao.entity.DataItem;
import com.leman.diyaobao.entity.TypeItem;

import java.util.List;


public class TypeAdapter extends BaseQuickAdapter<TypeItem, BaseViewHolder> {

    public TypeAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TypeItem item) {

        helper.setText(R.id.text, item.getText());

    }
}


