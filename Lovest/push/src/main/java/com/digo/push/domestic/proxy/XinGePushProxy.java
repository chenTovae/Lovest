package com.digo.push.domestic.proxy;

import android.content.Context;

import com.digo.platform.logan.mine.Logz;
import com.digo.push.domestic.manager.PushSdkManager;
import com.digo.push.domestic.manager.PushSdkManagerConfig;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

public class XinGePushProxy extends BasePushProxy {
    public static final String TAG = "XinGePushProxy %s";
    private static XinGePushProxy instance = null;

    static {
        instance = new XinGePushProxy();
        Logz.d(TAG, "create XinGePushProxy");
    }

    private XinGePushProxy() {
    }

    public static XinGePushProxy getInstance() {
        return instance;
    }

    @Override
    public void init(Context context) {
        //开启debug日志
        XGPushConfig.enableDebug(context, PushSdkManager.sEnableDebug);
        XGPushManager.registerPush(context, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                //token在设备卸载重装的时候有可能会变
                Logz.d(TAG, " 注册成功，设备token为：" + data);
                PushSdkManager.getInstance().setOpenPushEntry(PushSdkManagerConfig.PushType.PUSH_TYPE_XINGE, data.toString());//TODO ?toString();
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                Logz.d(TAG, " 注册失败，错误码：" + errCode + ",错误信息：" + msg);
            }
        });
    }

    /**
     * 注销信鸽推送
     *
     * @param context
     */
    public void unRegisterXGPush(Context context) {
        XGPushManager.unregisterPush(context);
    }
}
