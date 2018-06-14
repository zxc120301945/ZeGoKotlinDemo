package com.kotlin.demo.two.zego;


import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.kotlin.demo.two.KotlinZegoApplication;
import com.kotlin.demo.two.util.PreferenceUtil;
import com.kotlin.demo.two.util.SystemUtil;
import com.kotlin.demo.two.util.ZegoAppUtil;
import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.constants.ZegoAvConfig;
import com.zego.zegoliveroom.constants.ZegoConstants;


/**
 * des: zego api管理器.
 */
public class ZegoApiManager {

    private static ZegoApiManager sInstance = null;

    private ZegoLiveRoom mZegoLiveRoom = null;

    private ZegoAvConfig zegoAvConfig;


    private final int[][] VIDEO_RESOLUTIONS = new int[][]{{320, 240}, {352, 288}, {640, 360},
            {960, 540}, {1280, 720}, {1920, 1080}};

    private long mAppID = 0;
    private byte[] mSignKey = null;

    private ZegoApiManager() {
        mZegoLiveRoom = new ZegoLiveRoom();
    }

    public static ZegoApiManager getInstance() {
        if (sInstance == null) {
            synchronized (ZegoApiManager.class) {
                if (sInstance == null) {
                    sInstance = new ZegoApiManager();
                }
            }
        }
        return sInstance;
    }

    private void setupSDKContext() {
        // 注意，必须在调用其它 ZegoAPI 之前调用此方法
        //设置 SDK 上下文，如日志路径、Application Context 等，同时检查 so 库是否成功加载。
        //建议在应用 Application 继承类的 onCreate() 方法中设置
        ZegoLiveRoom.setSDKContext(new ZegoLiveRoom.SDKContextEx() {
            @Override
            public long getLogFileSize() {
                return 10 * 1024 * 1024;    // 单个日志文件大小不超过 10M，取值范围为 [5M, 100M]
            }

            @Nullable
            @Override
            public String getSoFullPath() {
                return null;                // return null 表示使用默认方式加载 libzegoliveroom.so，此处可以返回 so 的绝对路径，用来指定从这个位置加载 libzegoliveroom.so，确保应用具备存取此路径的权限
            }

            @Nullable
            @Override
            public String getLogPath() {
                return null;        // return null 表示日志文件会存储到默认位置，如果返回非空，则将日志文件存储到该路径下，注意应用必须具备存取该目录的权限
            }

            @NonNull
            @Override
            public Application getAppContext() {
                return KotlinZegoApplication.Companion.getApp();    // 必须返回当前应用的 Application 实例
            }
        });
    }

    private void initUserInfo() {
        // 初始化用户信息
        String userID = PreferenceUtil.getInstance().getUserID();
        String userName = PreferenceUtil.getInstance().getUserName();

        if (TextUtils.isEmpty(userID) || TextUtils.isEmpty(userName)) {
            long ms = System.currentTimeMillis();
            userID = ms + "";
            userName = "Android_" + SystemUtil.getOsInfo() + "-" + ms;

            // 保存用户信息
            PreferenceUtil.getInstance().setUserID(userID);
            PreferenceUtil.getInstance().setUserName(userName);
        }
        // 必须设置用户信息 ps随便设置，但是得有
        ZegoLiveRoom.setUser(userID, userName);

    }


    private void init(long appID, byte[] signKey) {
        setupSDKContext();

        initUserInfo();

        mAppID = appID;
        mSignKey = signKey;
        PreferenceUtil.getInstance().setAppId(mAppID);
        PreferenceUtil.getInstance().setAppKey(mSignKey);

        // 初始化sdk
        boolean ret = mZegoLiveRoom.initSDK(appID, signKey);
//        if (!ret) {
//            // sdk初始化失败
//            Toast.makeText(KotlinZegoApplication.Companion.getApp(), "Zego SDK初始化失败!", Toast.LENGTH_LONG).show();
//        } else {
//            //设置HIGH
//            // 开发者根据需求定制
//            zegoAvConfig = new ZegoAvConfig(ZegoAvConfig.Level.High);
//
//            mZegoLiveRoom.setAVConfig(zegoAvConfig);
//            //音频采集自动增益开关
//            mZegoLiveRoom.enableAGC(true);
//            //设置推流音频声道数  1->单声道
//            mZegoLiveRoom.setAudioChannelCount(1);
//            //设置延迟模式  普通延迟模式，该模式下，可支持 11kbps ~ 192kbps 码率范围
//            mZegoLiveRoom.setLatencyMode(ZegoConstants.LatencyMode.Normal2);
//        }

    }

    /**
     * 此方法是通过 appId 模拟获取与之对应的 signKey，强烈建议 signKey 不要存储在本地，而是加密存储在云端，通过网络接口获取
     *
     * @param appId
     * @return
     */
    private byte[] requestSignKey(long appId) {
        return ZegoAppUtil.requestSignKey(appId);
    }

    /**
     * 初始化sdk.
     */
    public void initSDK() {
        // 即构分配的key与id, 默认使用 UDP 协议的 AppId
        if (mAppID <= 0) {
            long storedAppId = PreferenceUtil.getInstance().getAppId();
            if (storedAppId > 0) {
                mAppID = storedAppId;
                mSignKey = PreferenceUtil.getInstance().getAppKey();
            } else {
                mAppID = ZegoAppUtil.UDP_APP_ID;
                mSignKey = requestSignKey(mAppID);
            }
        }
        init(mAppID, mSignKey);
    }

    public void reInitSDK(long appID, byte[] signKey) {
        init(appID, signKey);
    }

    public ZegoLiveRoom getZegoLiveRoom() {
        return mZegoLiveRoom;
    }

    public ZegoAvConfig getZegoAvConfig() {
        return zegoAvConfig;
    }

    public void setUseTestEvn(boolean useTestEvn) {

        PreferenceUtil.getInstance().setUseTestEvn(useTestEvn);
    }

    public boolean isUseExternalRender() {
        return PreferenceUtil.getInstance().getUseExternalRender(false);
    }

    public void setUseExternalRender(boolean useExternalRender) {

        PreferenceUtil.getInstance().setExternalRender(useExternalRender);
    }

    public void setUseVideoCapture(boolean useVideoCapture) {

        PreferenceUtil.getInstance().setVideoCapture(useVideoCapture);
    }

    public void setUseVideoFilter(boolean useVideoFilter) {

        PreferenceUtil.getInstance().setVideoFilter(useVideoFilter);
    }

    public boolean isUseVideoCapture() {
        return PreferenceUtil.getInstance().getVideoCapture(false);
    }

    public boolean isUseVideoFilter() {
        return PreferenceUtil.getInstance().getVideoFilter(false);
    }

    public long getAppID() {
        return mAppID;
    }

    public byte[] getSignKey() {
        return mSignKey;
    }

    public boolean isUseTestEvn() {
        return PreferenceUtil.getInstance().getTestEnv(false);
    }

}
