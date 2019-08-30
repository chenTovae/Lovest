package com.digo.push.domestic.receiver;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.digo.platform.logan.mine.Logz;
import com.digo.push.domestic.event.PushConnectionStatusEvent;
import com.digo.push.domestic.manager.EventTasks;
import com.digo.push.domestic.manager.PushSdkManager;
import com.digo.push.domestic.manager.PushSdkManagerConfig;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class XinGeReceiver extends XGPushBaseReceiver {
    private Intent intent = new Intent("com.qq.xgdemo.activity.UPDATE_LISTVIEW");
    public static final String TAG = "XinGeReceiver ";

    // 通知展示
    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult notifiShowedRlt) {
        if (context == null || notifiShowedRlt == null) {
            return;
        }
        String groupId = "";
        if (!TextUtils.isEmpty(notifiShowedRlt.getCustomContent())){
            try{
                JSONObject obj = new JSONObject(notifiShowedRlt.getCustomContent());
                groupId = obj.getString("groupId");//通过name字段获取其所包含的字符串
            }catch (Exception e){
                Logz.e(e);
            }
        }

        EventTasks.receivePushMessage(notifiShowedRlt.toString(), groupId, notifiShowedRlt.getTitle(),
                notifiShowedRlt.getContent(), PushSdkManagerConfig.PushType.PUSH_TYPE_XINGE, notifiShowedRlt.getCustomContent());
        context.sendBroadcast(intent);
        Logz.d(TAG + " onNotifactionShowedResult " + notifiShowedRlt.toString());
    }

    @Override
    public void onUnregisterResult(Context context, int errorCode) {
        if (context == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "反注册成功";
        } else {
            text = "反注册失败" + errorCode;
        }
        Logz.d(TAG + " onUnregisterResult " + text);
    }

    @Override
    public void onSetTagResult(Context context, int errorCode, String tagName) {
        if (context == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + tagName + "\"设置成功";
        } else {
            text = "\"" + tagName + "\"设置失败,错误码：" + errorCode;
        }
        Logz.d(TAG + " onSetTagResult " + text);
    }

    @Override
    public void onDeleteTagResult(Context context, int errorCode, String tagName) {
        if (context == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + tagName + "\"删除成功";
        } else {
            text = "\"" + tagName + "\"删除失败,错误码：" + errorCode;
        }
        Logz.d(TAG + " onDeleteTagResult " + text);
    }

    // 通知点击回调 actionType=1为该消息被清除，actionType=0为该消息被点击
    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult message) {
        if (context == null || message == null) {
            return;
        }
        String text = "";
        if (message.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
            // 通知在通知栏被点击
            // APP自己处理点击的相关动作
            // 这个动作可以在activity的onResume也能监听，请看第3点相关内容
            text = "通知被打开 :" + message;
            EventTasks.notificationPushMessageClicked(message.getCustomContent().toString(), message.getMsgId(), message.getTitle(), message.getContent(), PushSdkManagerConfig.PushType.PUSH_TYPE_XINGE);
        } else if (message.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
            // 通知被清除啦。。。。
            // APP自己处理通知被清除后的相关动作
            text = "通知被清除 :" + message;
            EventTasks.notificationPushMessageDeleted(message.toString());
        }
        // 获取自定义key-value
        String customContent = message.getCustomContent();
        if (customContent != null && customContent.length() != 0) {
            try {
                JSONObject obj = new JSONObject(customContent);
                // key1为前台配置的key
                if (!obj.isNull("key")) {
                    String value = obj.getString("key");
                    Logz.d(TAG + " onNotifactionClickedResult get custom value: " + value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // APP自主处理的过程。。。
        Logz.d(TAG + " onNotifactionClickedResult " + text);
    }

    @Override
    public void onRegisterResult(Context context, int errorCode, XGPushRegisterResult message) {
        // TODO Auto-generated method stub
        if (context == null || message == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = " XinGePush 连接成功  " + message;
            // 在这里拿token
            String token = message.getToken();
            PushSdkManager.getInstance().setOpenPushEntry(PushSdkManagerConfig.PushType.PUSH_TYPE_XINGE, token);
            errorCode = PushSdkManagerConfig.PUSH_CONNECTION_RESULTCODE_SUCCESS;
        } else {
            text = " XinGePush 连接失败  " + message;
        }
        Logz.d(TAG + " onRegisterResult " + text);
        EventBus.getDefault().post(new PushConnectionStatusEvent(text, errorCode));
    }

    // 消息透传
    @Override
    public void onTextMessage(Context context, XGPushTextMessage message) {
        // TODO Auto-generated method stub
        String text = "收到消息:" + message.toString();
        // 获取自定义key-value
        String customContent = message.getCustomContent();
        if (customContent != null && customContent.length() != 0) {
            try {
                JSONObject obj = new JSONObject(customContent);
                // key1为前台配置的key
                if (!obj.isNull("key")) {
                    String value = obj.getString("key");
                    Logz.d(TAG + " onTextMessage get custom value: " + value);
                }
                // ...
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Logz.d(TAG + " onTextMessage " + text);
    }
}
