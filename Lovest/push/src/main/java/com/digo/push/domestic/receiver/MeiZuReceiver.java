package com.digo.push.domestic.receiver;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.digo.platform.logan.mine.Logz;
import com.digo.push.domestic.manager.EventTasks;
import com.digo.push.domestic.manager.PushSdkManager;
import com.digo.push.domestic.manager.PushSdkManagerConfig;
import com.meizu.cloud.pushsdk.MzPushMessageReceiver;
import com.meizu.cloud.pushsdk.handler.MzPushMessage;
import com.meizu.cloud.pushsdk.notification.PushNotificationBuilder;
import com.meizu.cloud.pushsdk.platform.message.PushSwitchStatus;
import com.meizu.cloud.pushsdk.platform.message.RegisterStatus;
import com.meizu.cloud.pushsdk.platform.message.SubAliasStatus;
import com.meizu.cloud.pushsdk.platform.message.SubTagsStatus;
import com.meizu.cloud.pushsdk.platform.message.UnRegisterStatus;
import com.yibasan.lizhifm.pushsdk.R;

import org.json.JSONObject;

/**
 * author: JackSong .
 * describe:魅族推送接收器
 * on 2017/12/4.
 */

public class MeiZuReceiver extends MzPushMessageReceiver {
    private static final String TAG = " MeiZuReceiver ";
    private int MEIZU_PUSH_CONNECTION_RESULTCODE_SUCCESS = 200;

    @Override
    public void onReceive(Context context, Intent intent) {
        Logz.d(TAG + intent.getData() + "");
        super.onReceive(context, intent);
    }

    @Override
    @Deprecated
    public void onRegister(Context context, String pushid) {
        //调用PushManager.register(context）方法后，会在此回调注册状态 应用在接受返回的pushid
        Logz.d(TAG + "onRegister pushID  " + pushid);
    }

    @Override
    public void onMessage(Context context, String s) {
    }

    @Override
    public void onMessage(Context context, Intent intent) {
        String content = intent.getExtras().toString();
        Logz.d(TAG + "flyme3 onMessage " + content);
    }

    @Override
    public void onMessage(Context context, String message, String platformExtra) {
        Logz.d(TAG + "onMessage " + message + " platformExtra " + platformExtra);
    }

    @Override
    @Deprecated
    public void onUnRegister(Context context, boolean b) {
        //调用PushManager.unRegisterMeiZuPush(context）方法后，会在此回调反注册状态
        Logz.d(TAG + "onUnRegister " + b);
    }

    @Override
    public void onPushStatus(Context context, PushSwitchStatus pushSwitchStatus) {
    }

    @Override
    public void onRegisterStatus(Context context, RegisterStatus registerStatus) {
        //调用新版订阅PushManager.register(context,appId,appKey)回调
        Logz.d(TAG + "onRegisterStatus " + registerStatus + " %s " + context.getPackageName());
        int code = Integer.parseInt(registerStatus.code);
        String text = "";
        if (code == MEIZU_PUSH_CONNECTION_RESULTCODE_SUCCESS) {
            PushSdkManager.getInstance().setOpenPushEntry(PushSdkManagerConfig.PushType.PUSH_TYPE_MEIZU, registerStatus.getPushId());
            code = PushSdkManagerConfig.PUSH_CONNECTION_RESULTCODE_SUCCESS;
            text = " Meizu推送：连接成功。";
        } else {
            text = " Meizu推送：连接失败。";
        }
        EventTasks.updateStatus(text + registerStatus.message, code);
    }

    @Override
    public void onUnRegisterStatus(Context context, UnRegisterStatus unRegisterStatus) {
        //新版反订阅回调
        Logz.d(TAG + "onUnRegisterStatus " + unRegisterStatus + " " + context.getPackageName());
    }

    @Override
    public void onSubTagsStatus(Context context, SubTagsStatus subTagsStatus) {
        //标签回调
        Logz.d(TAG + "onSubTagsStatus " + subTagsStatus + " " + context.getPackageName());
    }

    @Override
    public void onSubAliasStatus(Context context, SubAliasStatus subAliasStatus) {
        //别名回调
        Logz.d(TAG + "onSubAliasStatus " + subAliasStatus + " " + context.getPackageName());
    }

    @Override
    public void onUpdateNotificationBuilder(PushNotificationBuilder pushNotificationBuilder) {
        //设置通知栏弹出的小图标
        pushNotificationBuilder.setmLargIcon(R.drawable.notify_lizhi);
        pushNotificationBuilder.setmStatusbarIcon(R.drawable.notify_lizhi_status);
    }

    @Override
    public void onNotificationArrived(Context context, MzPushMessage mzPushMessage) {
        //通知栏消息到达回调，flyme6基于android6.0以上不再回调
        Logz.d(TAG + " onNotificationArrived " + mzPushMessage.toString());
        String groupId = "";
        if (!TextUtils.isEmpty(mzPushMessage.getSelfDefineContentString())){
            try{
                JSONObject obj = new JSONObject(mzPushMessage.getSelfDefineContentString());
                groupId = obj.getString("groupId");//通过name字段获取其所包含的字符串
            }catch (Exception e){
                Logz.e(e);
            }
        }
        EventTasks.receivePushMessage(mzPushMessage.toString(), groupId, mzPushMessage.getTitle(), mzPushMessage.getContent(),
                PushSdkManagerConfig.PushType.PUSH_TYPE_MEIZU, mzPushMessage.getSelfDefineContentString());
    }

    @Override
    public void onNotificationClicked(Context context, MzPushMessage mzPushMessage) {
        //通知栏消息点击回调
        Logz.d(TAG + " onNotificationClicked " + mzPushMessage.toString());
        EventTasks.notificationPushMessageClicked(mzPushMessage.toString(), mzPushMessage.getNotifyId(),
                mzPushMessage.getTitle(), mzPushMessage.getContent(), PushSdkManagerConfig.PushType.PUSH_TYPE_MEIZU);
    }

    @Override
    public void onNotificationDeleted(Context context, MzPushMessage mzPushMessage) {
        //通知栏消息删除回调；flyme6基于android6.0以上不再回调
        Logz.d(TAG + " onNotificationDeleted " + mzPushMessage.toString());
        EventTasks.notificationPushMessageDeleted(mzPushMessage.toString());
    }

    @Override
    public void onNotifyMessageArrived(Context context, String message) {
        Logz.d(TAG + "onNotifyMessageArrived messsage " + message);
    }
}