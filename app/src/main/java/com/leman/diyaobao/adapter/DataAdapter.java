package com.leman.diyaobao.adapter;


import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.leman.diyaobao.R;
import com.leman.diyaobao.entity.DataItem;

import java.util.List;


public class DataAdapter extends BaseQuickAdapter<DataItem, BaseViewHolder> {

    public DataAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, DataItem item) {

        String[] s = item.getData_day().split(" ")[0].split("-");

        //加载图片
        Glide.with(mContext).load(item.getData_image()).crossFade().into((ImageView) helper.getView(R.id.image));

        helper.setText(R.id.month,s[1]+"month");
        helper.setText(R.id.day,s[2]);
        helper.setText(R.id.lai, item.getData_lai());
        helper.setText(R.id.name, item.getData_address());
        helper.setText(R.id.info, item.getData_model() + "score" + item.getData_cost());

        helper.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.setOnLongClickLinater(helper.itemView);
                return false;
            }
        });

    }

    public interface setOnLongClick{
        public void setOnLongClickLinater(View view);
    }
    /**
     *定义一个变量储存数据
     */
    private setOnLongClick listener;
    /**
     *提供公共的方法,并且初始化接口类型的数据
     */
    public void setListener( setOnLongClick listener){
        this.listener = listener;
    }

}


