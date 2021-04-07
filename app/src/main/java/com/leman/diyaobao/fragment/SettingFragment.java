package com.leman.diyaobao.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leman.diyaobao.R;
import com.leman.diyaobao.activity.AboutActivity;
import com.leman.diyaobao.activity.HelpActivity;
import com.leman.diyaobao.activity.MessageActivity;
import com.leman.diyaobao.activity.PrivacyActivity;
import com.leman.diyaobao.activity.SettingActivity;
import com.leman.diyaobao.movement.ToastUtil;
import com.leman.diyaobao.utils.FileUtils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

public class SettingFragment extends Fragment implements View.OnClickListener {
    private TextView title;
    private LinearLayout parameterSetting;//参数设置
    private LinearLayout privacy;//隐私
    private LinearLayout message;
    private LinearLayout cache;//缓存管理
    private LinearLayout inviteFriends;//邀请好友
    private LinearLayout help;//帮助与反馈
    private LinearLayout about;

    private TextView cash;

    private boolean isbutton = true;
    private LinearLayout ll_invite;
    private ImageButton iv_invite_wechat;
    private ImageButton iv_invite_friend;
    private ImageButton iv_invite_qq;
    private ImageButton iv_invite_space;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        title = view.findViewById(R.id.title);
        title.setText("Setting");

        parameterSetting = view.findViewById(R.id.parameterSetting);
        parameterSetting.setOnClickListener(this);
        privacy = view.findViewById(R.id.privacy);
        privacy.setOnClickListener(this);
        message = view.findViewById(R.id.message);
        message.setOnClickListener(this);
        cache = view.findViewById(R.id.cache);
        cache.setOnClickListener(this);
        inviteFriends = view.findViewById(R.id.inviteFriends);
        inviteFriends.setOnClickListener(this);
        help = view.findViewById(R.id.help);
        help.setOnClickListener(this);
        about = view.findViewById(R.id.about);
        about.setOnClickListener(this);

        cash = view.findViewById(R.id.cash);
        ll_invite = view.findViewById(R.id.ll_invite);

        iv_invite_wechat = view.findViewById(R.id.iv_invite_wechat);
        iv_invite_wechat.setOnClickListener(this);
        iv_invite_friend = view.findViewById(R.id.iv_invite_friend);
        iv_invite_friend.setOnClickListener(this);
        iv_invite_qq = view.findViewById(R.id.iv_invite_qq);
        iv_invite_qq.setOnClickListener(this);
        iv_invite_space = view.findViewById(R.id.iv_invite_space);
        iv_invite_space.setOnClickListener(this);


        //加载布局的同时计算APP缓存
        long cacheSize = 0;
        try {
            cacheSize = FileUtils.getCacheSize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String size = FileUtils.formatFileSize(cacheSize, FileUtils.SIZETYPE_MB) + "MB";
        cash.setText(size);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //参数设置
            case R.id.parameterSetting:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            //隐私
            case R.id.privacy:
                startActivity(new Intent(getActivity(), PrivacyActivity.class));
                break;
            case R.id.message:
                startActivity(new Intent(getActivity(), MessageActivity.class));
                break;
            //缓存管理
            case R.id.cache:
                FileUtils.clearCache();
                Toast.makeText(getActivity(), "清除成功", Toast.LENGTH_LONG).show();
                cash.setText(0 + "MB");
                break;
            //邀请好友
            case R.id.inviteFriends:
                if (isbutton) {
                    ll_invite.setVisibility(View.VISIBLE);
                    isbutton = false;
                } else {
                    ll_invite.setVisibility(View.GONE);
                    isbutton = true;
                }
                break;
            case R.id.iv_invite_wechat:
                sendThree("地遥宝", "https://www.aiwalk.com", "我是微信分享", BitmapFactory.decodeResource(getResources(), R.drawable.app), SHARE_MEDIA.WEIXIN);
                break;
            case R.id.iv_invite_friend:
                sendThree("地遥宝", "https://www.aiwalk.com", "我是微信分享", BitmapFactory.decodeResource(getResources(), R.drawable.app), SHARE_MEDIA.WEIXIN_CIRCLE);
                break;
            case R.id.iv_invite_qq:
                sendThree("地遥宝", "https://www.aiwalk.com", "我是QQ分享", BitmapFactory.decodeResource(getResources(), R.drawable.app), SHARE_MEDIA.QQ);
                break;
            case R.id.iv_invite_space:
                sendThree("地遥宝", "https://www.aiwalk.com", "我是QQ空间分享", BitmapFactory.decodeResource(getResources(), R.drawable.app), SHARE_MEDIA.QZONE);
                break;
            case R.id.help:
                startActivity(new Intent(getActivity(), HelpActivity.class));
                break;
            case R.id.about:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;

        }
    }


    /**
     * @param title       分享的标题
     * @param openUrl     点击分享item打开的网页地址url
     * @param description 网页的描述
     * @param icon        分享item的图片
     * @param media       分享到第三方平台标记
     */
    public void sendThree(String title, String openUrl, String description, Bitmap icon, SHARE_MEDIA media) {

        UMWeb umWeb = new UMWeb(openUrl);
        umWeb.setTitle(title);
        umWeb.setThumb(new UMImage(getContext(), icon));
        umWeb.setDescription(description);
        new ShareAction(getActivity())
                .withText(description)//内容
                .withMedia(umWeb)
                .setPlatform(media)
                .setCallback(umShareListener)
                .share();
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            ToastUtil.showShortToast(getContext(), "分享成功");
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            ToastUtil.showShortToast(getContext(), "分享失败");

        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            ToastUtil.showShortToast(getContext(), "分享取消");
        }
    };

}
