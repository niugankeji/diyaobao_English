package com.leman.diyaobao.datalist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leman.diyaobao.R;
import com.leman.diyaobao.entity.DataItem;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by guohao on 2017/9/6.
 */

public class DataListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MYLIVE_MODE_CHECK = 0;
    int mEditMode = MYLIVE_MODE_CHECK;

    // 普通布局
    private final int TYPE_ITEM = 1;
    // 脚布局
    private final int TYPE_FOOTER = 2;
    // 当前加载状态，默认为加载完成
    private int loadState = 2;
    // 正在加载
    public final int LOADING = 1;
    // 加载完成
    public final int LOADING_COMPLETE = 2;
    // 加载到底
    public final int LOADING_END = 3;

    private Context context;
    private List<DataItem> mMyLiveList;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickLinstener onItemLongClickLinstener;

    public DataListAdapter(Context context) {
        this.context = context;
    }


    public void notifyAdapter(List<DataItem> myLiveList, boolean isAdd) {
        if (!isAdd) {
            this.mMyLiveList = myLiveList;
        } else {
            this.mMyLiveList.addAll(myLiveList);
        }
        notifyDataSetChanged();
    }

    public List<DataItem> getMyLiveList() {
        if (mMyLiveList == null) {
            mMyLiveList = new ArrayList<>();
        }
        return mMyLiveList;
    }
    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为FooterView
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_item, parent, false);
//        ViewHolder holder = new ViewHolder(view);
//        return holder;

        // 通过判断显示类型，来创建不同的View
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.data_item, parent, false);
            return new ViewHolder(view);

        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_refresh_footer, parent, false);
            return new FootViewHolder(view);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int i) {
        if (holder instanceof ViewHolder) {
            final ViewHolder recyclerViewHolder = (ViewHolder) holder;
            final DataItem item = mMyLiveList.get(holder.getAdapterPosition());
            String[] s = item.getData_day().split(" ")[0].split("-");
            recyclerViewHolder.month.setText(s[1]+"month");
            recyclerViewHolder.day.setText(s[2]);
            if (item.getData_moshi().equals("dai")){
                recyclerViewHolder.lai.setText(item.getData_lai());
                recyclerViewHolder.info.setText(item.getData_model() + "分值" + item.getData_cost());
            }else {
                recyclerViewHolder.lai.setText(item.getData_ping_lai());
                recyclerViewHolder.info.setText("");
            }

            recyclerViewHolder.name.setText(item.getData_address());

            //加载图片
            Glide.with(context).load(item.getData_image()).crossFade().into(recyclerViewHolder.image);

            if (mEditMode == MYLIVE_MODE_CHECK) {
                recyclerViewHolder.mCheckBox.setVisibility(View.GONE);
            } else {
                recyclerViewHolder.mCheckBox.setVisibility(View.VISIBLE);

                if (item.isSelect()) {
                    recyclerViewHolder.mCheckBox.setImageResource(R.mipmap.ic_checked);
                } else {
                    recyclerViewHolder.mCheckBox.setImageResource(R.mipmap.ic_uncheck);
                }
            }
            recyclerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClickListener(recyclerViewHolder.getAdapterPosition(), mMyLiveList);
                }
            });
            recyclerViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemLongClickLinstener.onItemLongClickListener(recyclerViewHolder.getAdapterPosition(), mMyLiveList);
                    return true;
                }
            });

        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            switch (loadState) {
                case LOADING: // 正在加载
                    footViewHolder.pbLoading.setVisibility(View.VISIBLE);
                    footViewHolder.tvLoading.setVisibility(View.VISIBLE);
                    footViewHolder.llEnd.setVisibility(View.GONE);
                    break;

                case LOADING_COMPLETE: // 加载完成
                    footViewHolder.pbLoading.setVisibility(View.INVISIBLE);
                    footViewHolder.tvLoading.setVisibility(View.INVISIBLE);
                    footViewHolder.llEnd.setVisibility(View.GONE);
                    break;

                case LOADING_END: // 加载到底
                    footViewHolder.pbLoading.setVisibility(View.GONE);
                    footViewHolder.tvLoading.setVisibility(View.GONE);
                    footViewHolder.llEnd.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mMyLiveList.size()+1;
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    // 如果当前是footer的位置，那么该item占据2个单元格，正常情况下占据1个单元格
                    return getItemViewType(position) == TYPE_FOOTER ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener {
        void onItemClickListener(int pos, List<DataItem> myLiveList);
    }

    public void setOnItemLongClickListener(OnItemLongClickLinstener onItemLongClickLinstener){
        this.onItemLongClickLinstener = onItemLongClickLinstener;
    }
    public interface OnItemLongClickLinstener{
        void onItemLongClickListener(int pos, List<DataItem> myLiveList);
    }



    public void setEditMode(int editMode) {
        mEditMode = editMode;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView month;
        TextView day;
        TextView lai;
        TextView name;
        TextView info;
        ImageView mCheckBox;
        ImageView image;



        public ViewHolder(View itemView) {
            super(itemView);
            month = itemView.findViewById(R.id.month);
            day = itemView.findViewById(R.id.day);
            lai = itemView.findViewById(R.id.lai);
            name = itemView.findViewById(R.id.name);
            info = itemView.findViewById(R.id.info);
            mCheckBox = itemView.findViewById(R.id.mCheckBox);
            image = itemView.findViewById(R.id.image);
        }
    }
    private class FootViewHolder extends RecyclerView.ViewHolder {

        ProgressBar pbLoading;
        TextView tvLoading;
        LinearLayout llEnd;

        FootViewHolder(View itemView) {
            super(itemView);
            pbLoading = (ProgressBar) itemView.findViewById(R.id.pb_loading);
            tvLoading = (TextView) itemView.findViewById(R.id.tv_loading);
            llEnd = (LinearLayout) itemView.findViewById(R.id.ll_end);
        }
    }
    /**
     * 设置上拉加载状态
     *
     * @param loadState 0.正在加载 1.加载完成 2.加载到底
     */
    public void setLoadState(int loadState) {
        this.loadState = loadState;
        notifyDataSetChanged();
    }
}
