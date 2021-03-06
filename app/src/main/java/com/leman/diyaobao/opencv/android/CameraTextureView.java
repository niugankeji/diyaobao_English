package com.leman.diyaobao.opencv.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

@SuppressLint("NewApi")
public class CameraTextureView extends TextureView implements TextureView.SurfaceTextureListener {
	private static final String TAG = "CameraTextureView";
	Context mContext;
	SurfaceTexture mSurface;
	public CameraTextureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		this.setSurfaceTextureListener(this);
	}
	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
                                          int height) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onSurfaceTextureAvailable...");
		mSurface = surface;
//		CameraInterface.getInstance().doStartPreview(surface, 1.33f);
	}
	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onSurfaceTextureDestroyed...");
		CameraInterface.getInstance().doStopCamera();
		return true;
	}
	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
                                            int height) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onSurfaceTextureSizeChanged...");
	}
	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onSurfaceTextureUpdated...");
		
	}
	
	/* 让Activity能得到TextureView的SurfaceTexture
	 * @see android.view.TextureView#getSurfaceTexture()
	 */
	public SurfaceTexture _getSurfaceTexture(){
		return mSurface;
	}
}
