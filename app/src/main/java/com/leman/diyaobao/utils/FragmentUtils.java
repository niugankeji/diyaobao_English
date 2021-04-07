package com.leman.diyaobao.utils;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


/**
 * Created by Administrator on 2018/1/20.
 */

public class FragmentUtils {
    static FragmentTransaction transaction;
    // Fragment管理器
    private static FragmentManager manager;
    public static void replceFragment(FragmentActivity context, @IdRes int layout, Fragment fragment){
        manager = context.getSupportFragmentManager();
        transaction = manager.beginTransaction();
        // 使用add方法添加Fragment，第一个参数是要把Fragment添加到的布局Id
        // 第二个就是要添加的Fragment
        transaction.replace(layout, fragment);
        // 提交事务，否则添加就没成功
        transaction.commit();
    }
}
