package com.leman.diyaobao.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.leman.diyaobao.R;
import com.leman.diyaobao.entity.RecordItem;
import java.util.List;


public class RecordAdapter extends BaseQuickAdapter<RecordItem.DataBean, BaseViewHolder> {


    public RecordAdapter(int layoutResId, List data) {
        super(layoutResId, data);

    }

    @Override
    protected void convert(final BaseViewHolder helper, final RecordItem.DataBean item) {
        if (item.getIntegralWay() == 0){
            helper.setText(R.id.name, "First registration");
        }
        if (item.getIntegralWay() == 1){
            helper.setText(R.id.name, "Daily steps up to standard");
        }
        if (item.getIntegralWay() == 2){
            helper.setText(R.id.name, "Photo upload data");
        }
        if (item.getIntegralWay() == 3){
            helper.setText(R.id.name, "Data contribution award");
        }
        if (item.getIntegralWay() == 4){
            helper.setText(R.id.name, "View data");
        }
    //    helper.setText(R.id.name, item.getReportName());
        helper.setText(R.id.time, item.getCreatedTime());
        helper.setText(R.id.number, item.getIntegralValue());

    }

}


