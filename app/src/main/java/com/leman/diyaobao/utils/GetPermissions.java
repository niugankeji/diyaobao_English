package com.leman.diyaobao.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

public class GetPermissions {

    List<String> mPermissionList = new ArrayList<>();
    private PermissionCallback callback;
    private Activity activity;

    public GetPermissions(Activity activity) {
        this.activity = activity;
    }

    public void getpermissions(String[] permissions) {
/**
 * 判断哪些权限未授予
 */

        mPermissionList.clear();

        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }
/**
 * 判断是否为空
 */
        if (mPermissionList.size()>0){
            Log.e("tag","mPermissionList************: "+mPermissionList.get(0));
        }
        if (mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了
            callback.success();
        } else {//请求权限方法

            String[] permissions1 = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组

            ActivityCompat.requestPermissions(activity, permissions1, 1);

        }
    }

    public void getPression(PermissionCallback callback) {

        this.callback = callback;
    }

    public abstract static class PermissionCallback {
        public abstract void success();
    }
}
