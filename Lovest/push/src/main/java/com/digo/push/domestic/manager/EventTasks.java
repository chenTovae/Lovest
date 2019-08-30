package com.digo.push.domestic.manager;

import android.widget.Toast;

import com.digo.platform.ApplicationContext;
import com.digo.push.domestic.event.PushConnectionStatusEvent;
import com.digo.push.domestic.event.PushMessageNotificationClickedEvent;
import com.digo.push.domestic.event.PushMessageNotificationDeletedEvent;
import com.digo.push.domestic.event.PushMessageReceiveEvent;
import com.digo.push.domestic.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

public class EventTasks {
    /**
     * 更新推送状态
     */
    public static void updateStatus(String status, int resultCode) {
        EventBus.getDefault().post(new PushConnectionStatusEvent(status, resultCode));
        if (resultCode != PushSdkManagerConfig.PUSH_CONNECTION_RESULTCODE_SUCCESS) {
            try {
                Integer nextChanel = PushSdkManager.getInstance().getPushSpareChannel();
                if (nextChanel != null) {
                    //返回的连接码不为0，且网络正常，这个时候启动备用推送, 如果不设置备用推送通道直接失败
                    PushSdkManager.getInstance().initAssignPush(ApplicationContext.getContext(), nextChanel);
                }
            } catch (Exception e) {
                //ingore
            }
        }
    }

    /**
     * 接收到通知
     */
    public static void receivePushMessage(String pushMessage, String groupId, String title, String content, int pushType, String customContent) {
        EventBus.getDefault().post(new PushMessageReceiveEvent(pushMessage, groupId, title, content, pushType, customContent));
    }

    /**
     * 通知栏通知被点击
     */
    public static void notificationPushMessageClicked(String pushMessage, long notifyId, String title, String content, int pushType) {
        EventBus.getDefault().post(new PushMessageNotificationClickedEvent(pushMessage, notifyId, title, content, pushType));
        ToastUtil.show(ApplicationContext.getContext(), pushMessage, Toast.LENGTH_SHORT);
    }

    /**
     * 通知栏通知被删除
     */
    public static void notificationPushMessageDeleted(String pushMessage) {
        EventBus.getDefault().post(new PushMessageNotificationDeletedEvent(pushMessage));
    }
}
