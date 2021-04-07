package com.leman.diyaobao.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.leman.diyaobao.R;
import com.leman.diyaobao.biaoqing.SpanStringUtils;
import com.leman.diyaobao.entity.CommentsItem;

import java.util.List;

import static com.leman.diyaobao.MyApp.getContext;

public class CommentsAdapter extends BaseQuickAdapter<CommentsItem, BaseViewHolder> {
    public CommentsAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CommentsItem item) {
        helper.setText(R.id.user, item.getUsername());
        helper.setText(R.id.time, item.getTime());
    //    helper.setText(R.id.content, item.getContent());
        helper.setText(R.id.content, SpanStringUtils.getEmotionContent(getContext(), (TextView) helper.getView(R.id.content), item.getContent()));

    }
}
