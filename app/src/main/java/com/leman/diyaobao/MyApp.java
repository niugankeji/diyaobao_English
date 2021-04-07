package com.leman.diyaobao;


import android.content.Context;
import com.leman.diyaobao.utils.SPUtils;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.zhy.http.okhttp.OkHttpUtils;
import org.litepal.LitePalApplication;
import java.util.concurrent.TimeUnit;
import cn.jpush.android.api.JPushInterface;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.OkHttpClient;

public class MyApp extends LitePalApplication {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(6000L, TimeUnit.MILLISECONDS)
                .readTimeout(6000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);

        CrashReport.initCrashReport(getApplicationContext(), "aa2410839a", false);

        SPUtils.getSP(getApplicationContext());

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        JPushInterface.resumePush(this);

        super.onCreate();
        UMShareAPI.get(this);//初始化sdk
        //开启debug模式，方便定位错误，具体错误检查方式可以查看http://dev.umeng.com/social/android/quick-integration的报错必看，正式发布，请关闭该模式
        Config.DEBUG = true;

        context=getApplicationContext();

        Realm.init(this);
        RealmConfiguration myConfig = new RealmConfiguration.Builder()
                .name("myrealm.realm")
                .schemaVersion(2)
                .build();
        Realm.setDefaultConfiguration(myConfig);


    }

    //各个平台的配置
    {
        //微信
        PlatformConfig.setWeixin("wx80c683ccd29571a0", "737325bce2e5ff14dee1385823e30bb7");
        //QQ
        PlatformConfig.setQQZone("101579520", "80f459f343969395a7351bd362b22272");
    }

    public static Context getContext() {
                 return context;
             }

}
