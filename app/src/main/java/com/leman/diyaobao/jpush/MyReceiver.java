package com.leman.diyaobao.jpush;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.leman.diyaobao.R;
import com.leman.diyaobao.activity.MainActivity;

import cn.jpush.android.api.JPushInterface;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MyReceiver extends BroadcastReceiver {

    private static final String TAG = "JIGUANG";
    public static String regId;

    private static final int PUSH_NOTIFICATION_ID = (0x001);
    private static final String PUSH_CHANNEL_ID = "PUSH_NOTIFY_ID";
    private static final String PUSH_CHANNEL_NAME = "PUSH_NOTIFY_NAME";


    @Override
    public void onReceive(Context context, Intent intent) {

        try {

            Bundle bundle = intent.getExtras();

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Log.e(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                //send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                Log.e(TAG, "[MyReceiver] 接收到推送下来的自定义消息(内容为): " + bundle.getString(JPushInterface.EXTRA_MESSAGE));

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(PUSH_CHANNEL_ID, PUSH_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                    if (notificationManager != null) {
                        notificationManager.createNotificationChannel(channel);
                    }
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context,PUSH_CHANNEL_ID);
                Intent notificationIntent = new Intent(context, MainActivity.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
                builder.setContentTitle("LAIsmart")//设置通知栏标题
                        .setContentIntent(pendingIntent) //设置通知栏点击意图
                        .setContentText(bundle.getString(JPushInterface.EXTRA_MESSAGE))
                        .setTicker(bundle.getString(JPushInterface.EXTRA_MESSAGE)) //通知首次出现在通知栏，带上升动画效果的
                        .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                        .setSmallIcon(R.mipmap.ic_launcher)//设置通知小ICON
                        .setChannelId(PUSH_CHANNEL_ID)
                        .setDefaults(Notification.DEFAULT_ALL);

                Notification notification = builder.build();
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                if (notificationManager != null) {
                    notificationManager.notify(PUSH_NOTIFICATION_ID, notification);
                }




            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                Log.e(TAG, "[MyReceiver] 接收到推送下来的通知");

                String extra_json = bundle.getString(JPushInterface.EXTRA_EXTRA);
                if (!TextUtils.isEmpty(extra_json))
                    Log.e(TAG, "[MyReceiver] 接收到推送下来的通知附加字段" + extra_json);

                // 可以利用附加字段来区别Notication,指定不同的动作,extra_json是个json字符串
                // 通知（Notification），指在手机的通知栏（状态栏）上会显示的一条通知信息
            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                Log.e(TAG, "[MyReceiver] 用户点击打开了通知");

                // 在这里根据 JPushInterface.EXTRA_EXTRA(附加字段) 的内容处理代码，
                // 比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Log.e(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                Log.e(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
            } else {
                Log.e(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    // 一般登录之后调用此方法设置别名
// sequence 用来标识一次操作的唯一性(退出登录时根据此参数删除别名)
// alias 设置有效的别名
// 有效的别名组成：字母（区分大小写）、数字、下划线、汉字、特殊字符@!#$&*+=.|。限制：alias 命名长度限制为 40 字节。
//JPushInterface.setAlias(context,  int sequence, String alias);
// 退出登录删除别名
//JPushInterface.deleteAlias(Context context,int sequence);
}
