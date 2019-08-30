package com.digo.push.domestic.manager;

import android.content.Context;

import com.digo.platform.logan.mine.Logz;
import com.digo.push.domestic.interfaces.PushUpdateTokenToServerInterface;
import com.digo.push.domestic.model.UpdatePushToken;
import com.digo.push.domestic.proxy.BasePushProxy;
import com.digo.push.domestic.util.SharedPreUtils;
import com.digo.push.domestic.util.SystemUtil;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class PushSdkManager {
    private static final String TAG = "PushSdkManager ";

    private static boolean sEnablePush = true;//是否启用推送功能 默认开启
    public static boolean sEnableDebug = false;//是否启用debug功能 默认关闭

    private UpdatePushToken mUpdatePushToken;//连接状态实体类
    private Queue<Integer> mPushSpareChannelQueue = new LinkedBlockingQueue<>();//备选队列
    private PushUpdateTokenToServerInterface mIUpdateTokenToServer;//token更新回调

    private static PushSdkManager instance;//管理单例

    static {
        instance = new PushSdkManager();
        Logz.d(TAG + " creat PushSdkManager");
    }

    private PushSdkManager() {
        mUpdatePushToken = new UpdatePushToken();
    }

    public static PushSdkManager getInstance() {
        return instance;
    }

    public PushSdkManager setIUpdateToken(PushUpdateTokenToServerInterface iUpdateTokenToServer) {
        this.mIUpdateTokenToServer = iUpdateTokenToServer;
        return instance;
    }

    /**
     * 设置推送开关
     *
     * @param enablePush
     */
    public PushSdkManager setEnablePush(boolean enablePush) {
        sEnablePush = enablePush;
        return instance;
    }

    /**
     * 设置推送Toast开关
     *
     * @param enablePush
     */
    public PushSdkManager setEnableDebug(boolean enablePush) {
        sEnableDebug = enablePush;
        return instance;
    }

    /**
     * 当前推送信息数据结构
     *
     * @return
     */
    public void setOpenPushEntry(int pushType, String token) {
        //成功注册会被刷新，最好做本地缓存最后一次成功注册的type+token
        mUpdatePushToken.setToken(token);
        mUpdatePushToken.setPushType(pushType);
        mUpdatePushToken.setTime(System.currentTimeMillis() / 1000);
        //本地缓存存储
        SharedPreUtils.setFinalType(mUpdatePushToken.getPushType());
        SharedPreUtils.setFinalToken(mUpdatePushToken.getToken());
        SharedPreUtils.setFinalModel(mUpdatePushToken.getModel());
        SharedPreUtils.setFinalAct(mUpdatePushToken.getAct());
        SharedPreUtils.setFinalTime(mUpdatePushToken.getTime());
        Logz.i("PushSdkManager setOpenPushEntry : [" + mUpdatePushToken.toString() + "]");

        updateToken();//刷新token回调
    }

    /**
     * 获取上传到服务器的token封装 默认act = 0（更新token）
     *
     * @return
     */
    public UpdatePushToken getUpdatePushToken() {
        return mUpdatePushToken;//直接返回，如果更新走回调调用上层重新上传
    }

    /**
     * 获取本次初始化的备用通道列表
     */
    public Integer getPushSpareChannel() {
        return mPushSpareChannelQueue.poll();
    }

    public void initAssignPush(Context context, int push_type, int[] spareArray) {
        //如果推送开关是关闭的则返回
        if (!sEnablePush) {
            return;
        }

        try {
            if (spareArray != null && spareArray.length > 0) {
                mPushSpareChannelQueue.removeAll(mPushSpareChannelQueue);//清空
                for (int emu : spareArray)
                    mPushSpareChannelQueue.add(emu);//入队操作
            }
        } catch (Exception e) {
            //ingore
        }

        //通道初始化
        initPush(context, push_type);
    }

    public void initAssignPush(Context context, int push_type) {
        //如果推送开关是关闭的则返回
        if (!sEnablePush) {
            return;
        }
        //通道初始化
        initPush(context, push_type);
    }

    private void initPush(Context context, int push_type) {
        mUpdatePushToken.setToken("");//设置默认的token为空字符串
        mUpdatePushToken.setPushType(push_type);//默认是手机型号推送通道
        mUpdatePushToken.setModel(SystemUtil.getSystemModel());//手机系统模式
        mUpdatePushToken.setAct(PushSdkManagerConfig.PushAct.PUSHACT_RESET);//ACT_RESEET
        BasePushProxy.initAssignPush(context, push_type);
    }

    /**
     * ------外部请谨慎调用------
     * 获取推送的token值：每个平台的叫法不一,魅族叫pushId,小米叫regId,华为叫token,信鸽叫token
     * 这里我们统一叫token
     */
    public void updateToken() {
        if (mIUpdateTokenToServer != null) {
            mIUpdateTokenToServer.updateTokenToServerCallBack(mUpdatePushToken);
        }
    }

    /**
     * 根据自动识别当前使用的pushSdk、删除token
     */
    public void unRegisterPush() {
        BasePushProxy.unRegisterPush(mUpdatePushToken);
        mUpdatePushToken = null;
    }
}
