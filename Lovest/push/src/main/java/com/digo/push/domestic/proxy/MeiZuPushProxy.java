package com.digo.push.domestic.proxy;

import android.content.Context;
import android.text.TextUtils;

import com.digo.platform.logan.mine.Logz;
import com.digo.push.domestic.util.KeyStoreUtil;
import com.meizu.cloud.pushsdk.PushManager;

public class MeiZuPushProxy extends BasePushProxy {
    public static final String TAG = " MeiZuPushProxy ";
    private static MeiZuPushProxy instance = null;

    static {
        instance = new MeiZuPushProxy();
        Logz.d(TAG, "create " + TAG);
    }

    private MeiZuPushProxy() {
    }

    public static MeiZuPushProxy getInstance() {
        return instance;
    }

    @Override
    public void init(Context context) {
        Logz.d(TAG + PushManager.getPushId(context));
        try {
            String APP_ID = KeyStoreUtil.getMetaValue(context, "MZ_APP_ID");
            String APP_KEY = KeyStoreUtil.getMetaValue(context, "MZ_APP_KEY");
            if (TextUtils.isEmpty(APP_ID) || TextUtils.isEmpty(APP_KEY)) {
                return;
            }
            PushManager.register(context, APP_ID, APP_KEY);
        } catch (Exception e) {
            Logz.e(e);
        }
    }

    /**
     * 注销魅族推送
     *
     * @param context
     */
    public void unRegisterMeiZuPush(Context context) {
        try {
            String APP_ID = KeyStoreUtil.getMetaValue(context, "MZ_APP_ID");
            String APP_KEY = KeyStoreUtil.getMetaValue(context, "MZ_APP_KEY");
            PushManager.unRegister(context, APP_ID, APP_KEY);
        } catch (Exception e) {
            Logz.e(e);
        }
    }
}
