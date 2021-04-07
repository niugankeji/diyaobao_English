package com.leman.diyaobao.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.leman.diyaobao.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MarkerUtils {

    static BitmapDescriptor bitmapDescriptor;

    /**
     * func:批量添加自定义marker到地图上
     */
    public static void addCustomMarkersToMap(final AMap aMap, Context context, List<MarkerSign> markerSignList) {

        //    locations = addSimulatedData(centerLocation, 20, 0.02);
        for (int i = 0; i < markerSignList.size(); i++) {
            addCustomMarker(markerSignList.get(i).getUrl(), markerSignList.get(i), aMap, context);
        }

    }

    /**
     * func:添加单个自定义marker
     *
     * @param sign marker标记
     */
    private static void addCustomMarker(String url, final MarkerSign sign, final AMap aMap, Context context) {

        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.position(new LatLng(sign.getLat(), sign.getLgt()));
        customizeMarkerIcon(url, context, sign.getName(), new OnMarkerIconLoadListener() {
            @Override
            public void markerIconLoadingFinished(View view) {
                //bitmapDescriptor = BitmapDescriptorFactory.fromView(view);
                Marker marker;
                markerOptions.icon(bitmapDescriptor);
                marker = aMap.addMarker(markerOptions);
                marker.setObject(sign);
            }
        });
    }

    /**
     * func:定制化marker的图标
     *
     * @return
     */
    private static void customizeMarkerIcon(String image, Context context, String name, final OnMarkerIconLoadListener listener) {
        if (context == null){
            return;
        }
        final View markerView = LayoutInflater.from(context).inflate(R.layout.map_item, null);
    //    final CircleImageView icon = markerView.findViewById(R.id.image);
        final ImageView icon = markerView.findViewById(R.id.image);
        TextView id = markerView.findViewById(R.id.name);
        id.setText(name);
        if (image.contains("null") || image.equals("http://39.100.36.22:80/media/")|| image.equals("http://192.168.3.25:8000/media/")){

            Glide.with(context)
                    .load(R.mipmap.def_head)
                    .asBitmap()
                    .thumbnail(0.2f)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .centerCrop()
                    .placeholder(R.mipmap.def_head)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {

                            //待图片加载完毕后再设置bitmapDes
                            icon.setImageBitmap(bitmap);
                            bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(ViewUtil.convertViewToBitmap(markerView));
                            listener.markerIconLoadingFinished(markerView);
                        }
                    });
        }else {
            Glide.with(context)
                    .load(image)
                    .asBitmap()
                    .thumbnail(0.2f)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .centerCrop()
                    .placeholder(R.mipmap.def_head)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                            //待图片加载完毕后再设置bitmapDes
                            icon.setImageBitmap(bitmap);
                            bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(ViewUtil.convertViewToBitmap(markerView));
                            listener.markerIconLoadingFinished(markerView);
                        }
                    });
        }

    }


    /**
     * func:自定义监听接口,用来marker的icon加载完毕后回调添加marker属性
     */
    public interface OnMarkerIconLoadListener {
        void markerIconLoadingFinished(View view);
    }

}
