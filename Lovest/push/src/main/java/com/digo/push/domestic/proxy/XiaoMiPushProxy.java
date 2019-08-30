package com.digo.push.domestic.proxy;

import android.content.Context;
import android.text.TextUtils;

import com.digo.platform.ApplicationContext;
import com.digo.platform.logan.mine.Logz;
import com.digo.push.domestic.util.KeyStoreUtil;
import com.xiaomi.mipush.sdk.MiPushClient;

public class XiaoMiPushProxy extends BasePushProxy {
    public static final String TAG = "XiaoMiPushProxy";

    public static XiaoMiPushProxy instance;

    private String mRegId;
    private long mResultCode = -1;
    private String mReason;
    private String mCommand;
    private String mMessage;
    private String mTopic;
    private String mAlias;
    private String mUserAccount;
    private String mStartTime;
    private String mEndTime;

    private XiaoMiPushProxy() {

    }

    @Override
    public void init(Context context) {
        Logz.d(TAG + " init");
        try {
            //初始化push推送服务
            getInstance();
            try {
                String APP_ID = KeyStoreUtil.getMetaValue(context, "XM_APP_ID");
                String APP_KEY = KeyStoreUtil.getMetaValue(context, "XM_APP_KEY");
                if (TextUtils.isEmpty(APP_ID) || TextUtils.isEmpty(APP_KEY)){
                    return;
                }
                MiPushClient.registerPush(context, APP_ID, APP_KEY);
            } catch (Exception e) {
                Logz.e(e);
            }
        } catch (Exception e) {
            Logz.e(e);
        }
    }

    public static XiaoMiPushProxy getInstance() {
        if (null == instance) {
            synchronized (XiaoMiPushProxy.class) {
                if (null == instance) {
                    instance = new XiaoMiPushProxy();
                }
            }
        }
        return instance;
    }

    /**
     * 清除所有小米推送通知栏通知
     */
    public static void clearXiaomiNotifications() {
        MiPushClient.clearNotification(ApplicationContext.getContext());
    }

    public void setRegId(String regId) {
        mRegId = regId;
    }

    public String getRegId() {
        return mRegId;
    }

    /**
     * 注销小米推送
     */
    public void unRegisterXiaoMiPush(Context context) {
        MiPushClient.unregisterPush(context);
    }
}
