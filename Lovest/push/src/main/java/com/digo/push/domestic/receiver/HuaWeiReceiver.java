package com.digo.push.domestic.receiver;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.digo.platform.logan.mine.Logz;
import com.digo.push.domestic.manager.EventTasks;
import com.digo.push.domestic.manager.PushSdkManager;
import com.digo.push.domestic.manager.PushSdkManagerConfig;
import com.digo.push.domestic.util.ToastUtil;
import com.huawei.hms.support.api.push.PushReceiver;

public class HuaWeiReceiver extends PushReceiver {

    private final static String TAG = " HuaweiPushReceiver ";

    /**
     * token获取回调
     *
     * @param context
     * @param token
     * @param extras
     */
    @Override
    public void onToken(Context context, String token, Bundle extras) {
        Logz.d(TAG + " onToken token = " + token);
        EventTasks.updateStatus("HuaweiPush 连接成功", PushSdkManagerConfig.PUSH_CONNECTION_RESULTCODE_SUCCESS);
        PushSdkManager.getInstance().setOpenPushEntry(PushSdkManagerConfig.PushType.PUSH_TYPE_HUAWEI, token);
    }

    /**
     * 透传回调
     * 供子类继承实现后，推送消息下来时会自动回调onPushMsg方法实现应用透传消息处理。本接口必须被实现。
     *
     * @param context
     * @param msg
     * @param bundle
     * @return
     */
    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        Logz.d(TAG + " onPushMsg");
        return false;
    }

    /**
     * 通知栏事件回调
     * NOTIFICATION_OPENED, //通知栏中的通知被点击打开
     * NOTIFICATION_CLICK_BTN, //通知栏中通知上的按钮被点击
     *
     * @param context
     * @param event
     * @param extras
     */
    @Override
    public void onEvent(Context context, Event event, Bundle extras) {
        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {
            int notifyId = extras.getInt(BOUND_KEY.pushNotifyId, 0);
            if (0 != notifyId) {
                NotificationManager manager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notifyId);
            }
            Logz.d(TAG + "收到通知栏消息点击事件,notifyId:" + notifyId);
            ToastUtil.show(context, "收到华为通知栏消息点击事件,notifyId:" + notifyId, Toast.LENGTH_SHORT);
            String message = extras.getString(BOUND_KEY.pushMsgKey);
            //华为获取不到title和token
            EventTasks.notificationPushMessageClicked(message, notifyId, "", "", PushSdkManagerConfig.PushType.PUSH_TYPE_HUAWEI);
        }
        super.onEvent(context, event, extras);
    }

    /**
     * 查看Push通道是否已连接，结果会通过自定义广播里的onPushState方法返回。
     *
     * @param context
     * @param pushState
     */
    @Override
    public void onPushState(Context context, boolean pushState) {
        Logz.d(TAG + " onPushState isPushEnable=%s" + pushState);
    }
}