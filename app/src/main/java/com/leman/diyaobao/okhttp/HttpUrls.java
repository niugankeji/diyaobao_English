package com.leman.diyaobao.okhttp;

public class HttpUrls {
    //本地地址
//    public static final String HOMEADDRESS = "http://192.168.3.124:8000"; //主机地址   39.100.70.102
//    public static final String IMAGE = "http://192.168.3.124:8000"; //主机地址

    public static final String SERVER = "http://39.96.184.18:8060/iwalk";


    //服务器地址
    public static final String HOMEADDRESS = "http://39.98.179.72:80"; //主机地址   39.100.70.102
    public static final String IMAGE = "http://39.98.179.72:80"; //主机地址

    //用户相关
    public static final String GETMSM = HOMEADDRESS + "/smscode/get_msm_code/"; //获取短信验证码
    public static final String REGIST = HOMEADDRESS + "/user/registered/"; //注册
    public static final String LOGIN = HOMEADDRESS + "/user/login/"; //登录
    public static final String GETUSERINFO = HOMEADDRESS + "/user/get_user_info/"; //获取用户信息
    public static final String UPDOLADUSERADDRESS = HOMEADDRESS + "/user/upload_user_address/"; //上传用户位置
    public static final String UPLOADUSERINFO = HOMEADDRESS + "/user/upload_user_info/"; //提交用户资料
    public static final String GETNEARUSER = HOMEADDRESS + "/user/get_near_user/"; //获取附近用户
    public static final String GETSEARCHUSER = HOMEADDRESS + "/user/get_search_user/"; //获取搜索用户
    public static final String MODIFYPASSWORD = HOMEADDRESS + "/user/modify_password/"; //修改用户密码
    public static final String MODIFYPHONE = HOMEADDRESS + "/user/modify_phone/";//修改手机号

    //数据相关
    public static final String UPLOADDATA = HOMEADDRESS + "/data/upload_data/"; //上传数据
    public static final String GETDATA = HOMEADDRESS + "/data/getdat/"; //获取数据
    public static final String GETDATASIX = HOMEADDRESS + "/data/getdataSix/"; //获取6条数据
    public static final String GETNEARDATA = HOMEADDRESS + "/data/get_near_data/"; //获取附近数据

    //申请查看数据相关
    public static final String APPLYSEEDATA = HOMEADDRESS + "/apply/apply_see_data/"; //申请查看对方数据
    public static final String GETMYAPPLILIST = HOMEADDRESS + "/apply/get_my_apply_list/"; //获取我的申请记录
    public static final String GETOTHERAPPLYLIST = HOMEADDRESS + "/apply/get_other_apply_list/"; //获取别人对我的申请记录
    public static final String UPLOADAPPLYSTATE = HOMEADDRESS + "/apply/upload_apply_state/"; //提交申请处理状态

    //评论相关
    public static final String UPLOADECOMMENTS = HOMEADDRESS + "/comments/uploade_comments/";//提交评论
    public static final String GETCOMMENTS = HOMEADDRESS + "/comments/get_comments/"; //获取评论
    public static final String DELETECOMMENTSANDDATA = HOMEADDRESS + "/comments/delete_comments_and_data/"; //删除评论和数据

    //帮助反馈相关
    public static final String GETFEEDBACKTYPE = HOMEADDRESS + "/feedback/get_feedback_type/";//获取反馈类型
    public static final String UPLOADFEEDBACK = HOMEADDRESS + "/feedback/upload_feedback/"; //提交反馈

    //隐私相关
    public static final String SHOWNEARBY = HOMEADDRESS + "/user/show_nearby/"; //出现在附近人中
    public static final String HIDDenNEARBY = HOMEADDRESS + "/user/hidden_nearby/"; //不出现在附近人中
    public static final String GETPRIVACY = HOMEADDRESS + "/user/get_privacy/";

    //点赞
    public static final String MYLIKE = HOMEADDRESS + "/like/data_like/"; //

    //获取积分列表
    public static final String GETRECORDLIST = HOMEADDRESS + "/integral/get_integral_list/"; //
    //获取积分总数
    public static final String GETTOTALNUMBER = HOMEADDRESS + "/integral/get_integral_total/";
    //添加积分
    public static final String UPLOADENUMBER = HOMEADDRESS + "/integral/upload_integral/";
    //获取积分规则
    public static final String GETRUKES = HOMEADDRESS + "/integral/get_integral_rules/";
    //上传运动步数
    public static final String UPLOADSPORT = SERVER + "/sport/add";

    //获取apk信息
    public static final String GETAPKINFO = HOMEADDRESS + "/apk/getapkinfo/";


    public static String DISTANCE;
    public static String KALULI;
    public static String TIME;
    public static String BUPIN;
    public static String SPEED;
    public static int STEPMS;


}
