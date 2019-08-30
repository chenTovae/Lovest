package com.digo.push.domestic.proxy;

import android.content.Context;

import com.digo.platform.logan.mine.Logz;
import com.digo.push.domestic.receiver.GetuiReceiver;
import com.digo.push.domestic.service.GetuiPushService;
import com.igexin.sdk.PushManager;

public class GetuiPushProxy extends BasePushProxy {
    public static final String TAG = " GetuiPushProxy";
    private static GetuiPushProxy instance = null;

    private GetuiPushProxy() {
    }

    static {
        instance = new GetuiPushProxy();
        Logz.d(TAG + " create " + TAG);
    }

    public static GetuiPushProxy getInstance() {
        return instance;
    }

    @Override
    public void init(Context context) {
        PushManager.getInstance().initialize(context, GetuiPushService.class);
        PushManager.getInstance().registerPushIntentService(context, GetuiReceiver.class);
    }

    public void unRegisterGetuiPush(Context context) {
        try {
            PushManager.getInstance().stopService(context);
        }catch (Exception e){
            //ingore
        }
    }
}
