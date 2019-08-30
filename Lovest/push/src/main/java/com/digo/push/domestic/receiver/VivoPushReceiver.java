package com.digo.push.domestic.receiver;

import android.content.Context;

import com.digo.platform.logan.mine.Logz;
import com.digo.push.domestic.manager.EventTasks;
import com.digo.push.domestic.manager.PushSdkManager;
import com.digo.push.domestic.manager.PushSdkManagerConfig;
import com.digo.push.domestic.util.SharedPreUtils;
import com.vivo.push.model.UPSNotificationMessage;
import com.vivo.push.sdk.OpenClientPushMessageReceiver;

public class VivoPushReceiver extends OpenClientPushMessageReceiver {

    public static final String TAG = "VivoPushReceiver";

    /**
     * 通知被点击结果返回
     *
     * @param context
     * @param msg
     */
    @Override
    public void onNotificationMessageClicked(Context context, UPSNotificationMessage msg) {
        String customContentString = msg.getSkipContent();
        Logz.d(TAG + "通知点击 msgId " + msg.getMsgId() + " ;customContent=" + customContentString);
        EventTasks.notificationPushMessageClicked(msg.toString(), msg.getMsgId(), msg.getTitle(), msg.getContent(), PushSdkManagerConfig.PushType.PUSH_TYPE_VIVO);
    }

    /**
     * RegId 结果返回。当开发者首次调用 turnOnPush 成功或 regId 发生改变时会回调该 方法。
     *
     * @param context
     * @param regId
     */
    @Override
    public void onReceiveRegId(Context context, String regId) {
        Logz.d(TAG + "接收 regId = " + regId);
        PushSdkManager.getInstance().setOpenPushEntry(PushSdkManagerConfig.PushType.PUSH_TYPE_VIVO, regId);
        SharedPreUtils.setVivoToken(regId);//只回调一次进行token存储
    }
}
