package com.digo.push.domestic.proxy;

import android.content.Context;

import com.coloros.mcssdk.PushManager;
import com.digo.platform.ApplicationContext;
import com.digo.platform.logan.mine.Logz;
import com.digo.push.domestic.manager.PushSdkManagerConfig;
import com.digo.push.domestic.model.UpdatePushToken;
import com.digo.push.domestic.util.SystemUtil;
import com.vivo.push.PushClient;

public abstract class BasePushProxy {
    public static final String TAG = " BasePushProxy ";

    public static String HUAWEI = "huawei";
    public static String MEIZU = "meizu";
    public static String XIAOMI = "xiaomi";
    public static String OPPO = "oppo";
    public static String VIVO = "vivo";

    /**
     * 初始化推送
     *
     * @param context
     */
    public abstract void init(Context context);


    /**
     * @param push_type 启动指定推送
     */
    public static void initAssignPush(Context context, int push_type) {
        switch (push_type) {
            case PushSdkManagerConfig.PushType.PUSH_TYPE_HUAWEI:
                Logz.i(TAG + " init HuaWeiPush");
                HuaWeiPushProxy.getInstance().init(context);
                break;
            case PushSdkManagerConfig.PushType.PUSH_TYPE_MEIZU:
                Logz.i(TAG + " init MeiZuPush");
                MeiZuPushProxy.getInstance().init(context);
                break;
            case PushSdkManagerConfig.PushType.PUSH_TYPE_XIAOMI:
                Logz.i(TAG + " init XiaoMiPush");
                XiaoMiPushProxy.getInstance().init(context);
                break;
            case PushSdkManagerConfig.PushType.PUSH_TYPE_OPPO:
                Logz.i(TAG + " init OppoPush");
                OppoPushProxy.getInstance().init(context);
                break;
            case PushSdkManagerConfig.PushType.PUSH_TYPE_VIVO:
                Logz.i(TAG + " init VivoPush");
                VivoPushProxy.getInstance().init(context);
                break;
            case PushSdkManagerConfig.PushType.PUSH_TYPE_XINGE:
                Logz.i(TAG + "init XinGePush");
                XinGePushProxy.getInstance().init(context);
                break;
            case PushSdkManagerConfig.PushType.PUSH_TYPE_GETUI:
                Logz.i(TAG + "init Getui");
                GetuiPushProxy.getInstance().init(context);
                break;
            default:
                break;
        }
    }

    /**
     * 删除token
     *
     * @param mUpdatePushToken
     */
    public static void unRegisterPush(UpdatePushToken mUpdatePushToken) {
        if (null == mUpdatePushToken || null == mUpdatePushToken.getToken()) {
            return;
        }
        switch (mUpdatePushToken.getPushType()) {
            case PushSdkManagerConfig.PushType.PUSH_TYPE_XINGE:
                XinGePushProxy.getInstance().unRegisterXGPush(ApplicationContext.getContext());
                Logz.d(TAG + "unRegisterPush XinGePush");
                break;
            case PushSdkManagerConfig.PushType.PUSH_TYPE_HUAWEI:
                HuaWeiPushProxy.getInstance().unRegisterHuaWeiPush(mUpdatePushToken.getToken());
                Logz.d(TAG + " unRegisterPush HuaWeiPush");
                break;
            case PushSdkManagerConfig.PushType.PUSH_TYPE_MEIZU:
                MeiZuPushProxy.getInstance().unRegisterMeiZuPush(ApplicationContext.getContext());
                Logz.d(TAG + " unRegisterPush MeiZuPush");
                break;
            case PushSdkManagerConfig.PushType.PUSH_TYPE_XIAOMI:
                XiaoMiPushProxy.getInstance().unRegisterXiaoMiPush(ApplicationContext.getContext());
                Logz.d(TAG + " unRegisterPush XiaoMiPush");
                break;
            case PushSdkManagerConfig.PushType.PUSH_TYPE_OPPO:
                OppoPushProxy.getInstance().unRegisterOppoPush();
                Logz.d(TAG + " unRegisterPush OppoPush");
                break;
            case PushSdkManagerConfig.PushType.PUSH_TYPE_VIVO:
                VivoPushProxy.getInstance().unRegisterVivoPush(ApplicationContext.getContext());
                Logz.d(TAG + " unRegisterPush VivoPush");
                break;
            case PushSdkManagerConfig.PushType.PUSH_TYPE_GETUI:
                GetuiPushProxy.getInstance().unRegisterGetuiPush(ApplicationContext.getContext());
                Logz.d(TAG + " unRegisterPush GetuiPush");
                break;
            default:
                Logz.d(TAG + "unRegisterPush XinGePush");
                break;
        }
    }

    /**
     * @return 根据机型返回启用推送类型  抓取机型失败使用信鸽。
     */
    public static int getPhoneModel() {
        try {
            String phoneManufacturer = SystemUtil.getSystemManufacturer().toLowerCase();
            String phoneBrand = SystemUtil.getDeviceBrand().toLowerCase();
            if (phoneManufacturer.contains(HUAWEI) || phoneBrand.startsWith(HUAWEI)) {
                return PushSdkManagerConfig.PushType.PUSH_TYPE_HUAWEI;
            } else if (phoneManufacturer.contains(MEIZU) || phoneBrand.startsWith(MEIZU)) {
                return PushSdkManagerConfig.PushType.PUSH_TYPE_MEIZU;
            } else if (phoneManufacturer.contains(XIAOMI) || phoneBrand.startsWith(XIAOMI)) {
                return PushSdkManagerConfig.PushType.PUSH_TYPE_XIAOMI;
            } else if ((phoneManufacturer.contains(OPPO) || phoneBrand.startsWith(OPPO))
                    && PushManager.isSupportPush(ApplicationContext.getContext())) {
                return PushSdkManagerConfig.PushType.PUSH_TYPE_OPPO;
            } else if ((phoneManufacturer.contains(VIVO) || phoneBrand.startsWith(VIVO))
                    && PushClient.getInstance(ApplicationContext.getContext()).isSupport()) {
                return PushSdkManagerConfig.PushType.PUSH_TYPE_VIVO;
            } else {
                return PushSdkManagerConfig.PushType.PUSH_TYPE_GETUI;//确保整个组件只有这个默认配置项
            }
        } catch (Exception e) {
            Logz.e(TAG + " getPhoneModel run exception ");
            return PushSdkManagerConfig.PushType.PUSH_TYPE_GETUI;//确保整个组件只有这个默认配置项
        }
    }
}
