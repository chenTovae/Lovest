package com.digo.push.domestic.proxy;

import android.content.Context;
import android.text.TextUtils;

import com.coloros.mcssdk.PushManager;
import com.coloros.mcssdk.callback.PushCallback;
import com.coloros.mcssdk.mode.SubscribeResult;
import com.digo.platform.ApplicationContext;
import com.digo.platform.logan.mine.Logz;
import com.digo.push.domestic.manager.EventTasks;
import com.digo.push.domestic.manager.PushSdkManager;
import com.digo.push.domestic.manager.PushSdkManagerConfig;
import com.digo.push.domestic.util.KeyStoreUtil;

import java.util.List;

public class OppoPushProxy extends BasePushProxy {
    public static final String TAG = " OppoPushProxy ";
    private static volatile OppoPushProxy instance = null;

    private OppoPushProxy() {
    }

    public static OppoPushProxy getInstance() {
        if (instance == null) {
            synchronized (OppoPushProxy.class) {
                if (instance == null) {
                    instance = new OppoPushProxy();
                }
            }
        }
        return instance;
    }

    @Override
    public void init(Context context) {
        if (!PushManager.isSupportPush(context)) {
            Logz.e("the phone not support oppo push");
            return;
        }
        try {
            String APP_KEY = KeyStoreUtil.getMetaValue(context, "OPPO_APP_KEY");
            String APP_SECRET = KeyStoreUtil.getMetaValue(context, "OPPO_APP_SECRET");
            if (TextUtils.isEmpty(APP_KEY) || TextUtils.isEmpty(APP_SECRET)){
                return;
            }
            PushManager.getInstance().register(context, APP_KEY, APP_SECRET, new OppoPushCallback());
        } catch (Exception e) {
            Logz.e(e);
        }
    }

    /**
     * 设置pushCallback,会覆盖register中的pushCallback
     */
    public void setPushCallback(PushCallback callback) {
        PushManager.getInstance().setPushCallback(callback);
    }

    /**
     * 获取注册OPush推送服务的注册ID
     */
    public void getRegister() {
        PushManager.getInstance().getRegister();
    }

    /**
     * 获取OPush推送服务状态
     */
    public void getPushState() {
        PushManager.getInstance().getPushStatus();
    }

    /**
     * 当前系统是否支持 PUSH
     *
     * @return
     */
    public boolean isSupport() {
        return PushManager.isSupportPush(ApplicationContext.getContext());
    }

    /**
     * 当前设备的当前应用的唯一标识
     *
     * @return
     */
    public String getRegId() {
        return PushManager.getInstance().getRegisterID();
    }

    /**
     * 注销推送
     */
    public void unRegisterOppoPush() {
        PushManager.getInstance().unRegister();
    }

    class OppoPushCallback implements PushCallback {

        @Override
        public void onRegister(int code, String registerID) {
            String text = "";
            if (code == 0) {
                PushSdkManager.getInstance().setOpenPushEntry(PushSdkManagerConfig.PushType.PUSH_TYPE_OPPO, registerID);
                text = "oppo push注册成功";
                Logz.d("注册成功 RegistrationId:" + registerID);
            } else {
                Logz.d("注册失败 code:" + code);
                text = "oppo push注册失败";
            }
            Logz.d("oppopush", "code:" + code + " registerID:" + registerID);
            EventTasks.updateStatus(text, code);
        }

        @Override
        public void onUnRegister(int i) {

        }

        @Override
        public void onGetAliases(int i, List<SubscribeResult> list) {

        }

        @Override
        public void onSetAliases(int i, List<SubscribeResult> list) {

        }

        @Override
        public void onUnsetAliases(int i, List<SubscribeResult> list) {

        }

        @Override
        public void onSetUserAccounts(int i, List<SubscribeResult> list) {

        }

        @Override
        public void onUnsetUserAccounts(int i, List<SubscribeResult> list) {

        }

        @Override
        public void onGetUserAccounts(int i, List<SubscribeResult> list) {

        }

        @Override
        public void onSetTags(int i, List<SubscribeResult> list) {

        }

        @Override
        public void onUnsetTags(int i, List<SubscribeResult> list) {

        }

        @Override
        public void onGetTags(int i, List<SubscribeResult> list) {

        }

        @Override
        public void onGetPushStatus(int code, int status) {
            if (code == 0 && status == 0) {
                Logz.d("OPPO Push状态正常 " + " code=" + code + ", status= " + status);
            } else {
                Logz.d("OPPO Push状态错误 " + " code=" + code + ", status= " + status);
            }
        }

        @Override
        public void onSetPushTime(int i, String s) {

        }

        @Override
        public void onGetNotificationStatus(int code, int status) {
            if (code == 0 && status == 0) {
                Logz.d("OPPO Push 通知状态正常 " + " code= " + code + " ,status= " + status);
            } else {
                Logz.d("OPPO Push 通知状态错误 " + " code= " + code + " ,status= " + status);
            }
        }
    }
}
