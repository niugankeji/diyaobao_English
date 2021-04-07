package com.leman.diyaobao.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.leman.diyaobao.R;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private List<String> list;
    private Context context;

    public ImageAdapter(Context context ,List<String> list){
        this.list = list;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item,null);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder viewHolder, int i) {
        Glide.with(context).load(list.get(i)).into(viewHolder.image);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View myView;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
