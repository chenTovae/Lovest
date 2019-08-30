package com.digo.push.domestic.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.digo.push.R;
import com.digo.push.domestic.manager.EventTasks;
import com.digo.push.domestic.manager.PushSdkManager;
import com.digo.push.domestic.manager.PushSdkManagerConfig;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.yibasan.lizhifm.pushsdk.R;

import java.util.Iterator;
import java.util.Map;

import androidx.core.app.NotificationCompat;

public class GetuiReceiver extends GTIntentService {
    private static final String TAG = "GetuiReceiver";

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
        PushSdkManager.getInstance().setOpenPushEntry(PushSdkManagerConfig.PushType.PUSH_TYPE_MEIZU, clientid);
        EventTasks.updateStatus("GetuiPush 连接成功", PushSdkManagerConfig.PUSH_CONNECTION_RESULTCODE_SUCCESS);
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        sendNotification(context, "get form Payload", "get form Payload", "get form Payload", null);//TODO 对接时修改实参
    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage message) {
        Log.e(TAG, "onNotificationMessageArrived -> " + "appid = " + message.getAppid() + "\ntaskid = " + message.getTaskId()
                + "\nmessageid = " + message.getMessageId() + "\npkg = " + message.getPkgName() + "\ncid = " + message.getClientId()
                + "\ntitle = " + message.getTitle() + "\ncontent = " + message.getContent());
        EventTasks.receivePushMessage(message.toString(), "groupId", message.getTitle(), message.getContent(), PushSdkManagerConfig.PushType.PUSH_TYPE_GETUI, "selfContent");
    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage message) {
        Log.e(TAG, "onNotificationMessageClicked -> " + "appid = " + message.getAppid() + "\ntaskid = " + message.getTaskId()
                + "\nmessageid = " + message.getMessageId() + "\npkg = " + message.getPkgName() + "\ncid = " + message.getClientId()
                + "\ntitle = " + message.getTitle() + "\ncontent = " + message.getContent());
        EventTasks.notificationPushMessageClicked(message.toString(), Long.parseLong(message.getMessageId()), message.getTitle(), message.getContent(), PushSdkManagerConfig.PushType.PUSH_TYPE_GETUI);
    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
    }

    /**
     * @param context
     * @param clickAction
     * @param title
     * @param content
     * @param extraMap action groupId channel
     */
    public void sendNotification(Context context, String clickAction, String title, String content, Map<String, String> extraMap) {
        if (TextUtils.isEmpty(clickAction) || TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            return;
        }
        Intent intent = new Intent();//(1)set click action
        intent.setComponent(new ComponentName(getPackageName(), clickAction));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Bundle bundle = new Bundle();
        Iterator<Map.Entry<String, String>> it = extraMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            bundle.putString(entry.getKey(), entry.getValue());
        }
        if (bundle != null && bundle.size() > 0)
            intent.putExtras(bundle);//(2)set intent bundle

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = context.getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.notify_lizhi_status)
                .setContentTitle(title)//(3)set notification title
                .setContentText(content)//(4)set notification content
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    context.getResources().getString(R.string.char_sequence_name), NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}
