package com.digo.push.domestic.receiver;

import android.content.Context;

import com.digo.platform.logan.mine.Logz;
import com.digo.push.domestic.manager.EventTasks;
import com.digo.push.domestic.manager.PushSdkManager;
import com.digo.push.domestic.manager.PushSdkManagerConfig;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class XiaoMiReceiver extends PushMessageReceiver {
    private static final String TAG = "XiaoMiReceiver";
    private String mRegId;
    private String mTopic;
    private String mAlias;
    private String mAccount;
    private String mStartTime;
    private String mEndTime;

    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        Logz.d(TAG + " onReceivePassThroughMessage " + message.toString());
        JSONObject jsonObject = new JSONObject();
        Map<String, String> map = message.getExtra();
        for (String key : map.keySet()) {
            try {
                jsonObject.put(key, map.get(key));
            } catch (JSONException e) {
                Logz.e(e);
            }
        }
    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        Logz.d(TAG + " onNotificationMessageClicked " + message.toString());
        EventTasks.notificationPushMessageClicked(message.toString(), message.getNotifyId(),
                message.getTitle(), message.getContent(), PushSdkManagerConfig.PushType.PUSH_TYPE_XIAOMI);
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        Logz.d(TAG + " onNotificationMessageArrived " + message.toString());
        JSONObject jsonObject = new JSONObject();
        Map<String, String> map = message.getExtra();
        for (String key : map.keySet()) {
            try {
                jsonObject.put(key, map.get(key));
            } catch (JSONException e) {
                Logz.e(e);
            }
        }
        EventTasks.receivePushMessage(message.toString(), message.getExtra().get("groupId"),
                message.getTitle(), message.getContent(), PushSdkManagerConfig.PushType.PUSH_TYPE_XIAOMI, jsonObject.toString());
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        Logz.d(TAG + " onCommandResult " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        String log;
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                Logz.d(TAG + " onCommandResult register-success " + mRegId);
            } else {
                Logz.d(TAG + " onCommandResult FAIL " + MiPushClient.COMMAND_REGISTER);
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
                Logz.d(TAG + " onCommandResult set-alias-success " + mAlias);
            } else {
                log = message.getReason();
                Logz.d(TAG + " onCommandResult FAIL " + MiPushClient.COMMAND_SET_ALIAS + log);
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
                Logz.d(TAG + " onCommandResult unset-alias SUCCESS " + mAlias);
            } else {
                log = message.getReason();
                Logz.d(TAG + " onCommandResult FAIL " + MiPushClient.COMMAND_UNSET_ALIAS + log);
            }
        } else if (MiPushClient.COMMAND_SET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAccount = cmdArg1;
                Logz.d(TAG + " onCommandResult set-account SUCCESS " + mAccount);
            } else {
                log = message.getReason();
                Logz.d(TAG + " onCommandResult FAIL " + MiPushClient.COMMAND_SET_ACCOUNT + log);
            }
        } else if (MiPushClient.COMMAND_UNSET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAccount = cmdArg1;
                Logz.d(TAG + " onCommandResult unset-account SUCCESS " + mAccount);
            } else {
                log = message.getReason();
                Logz.d(TAG + " onCommandResult FAIL " + MiPushClient.COMMAND_UNSET_ACCOUNT + log);
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
                Logz.d(TAG + " onCommandResult subscribe-topic SUCCESS " + mTopic);
            } else {
                log = message.getReason();
                Logz.d(TAG + " onCommandResult FAIL " + MiPushClient.COMMAND_SUBSCRIBE_TOPIC + log);
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
                Logz.d(TAG + " onCommandResult unsubscibe-topic SUCCESS " + mTopic);
            } else {
                log = message.getReason();
                Logz.d(TAG + "vonCommandResult FAIL " + MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC + log);
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
                Logz.d(TAG + " onCommandResult accept-time SUCCESS " + mStartTime + "    " + mEndTime);
            } else {
                log = message.getReason();
                Logz.d(TAG + " onCommandResult FAIL " + MiPushClient.COMMAND_SET_ACCEPT_TIME + log);
            }
        } else {
            log = message.getReason();
            Logz.d(TAG + " onCommandResult FAIL " + log);
        }
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        Logz.d(TAG + " onReceiveRegisterResult " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String reason = message.getReason();
        long ResultCode = message.getResultCode();

        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                PushSdkManager.getInstance().setOpenPushEntry(PushSdkManagerConfig.PushType.PUSH_TYPE_XIAOMI, mRegId);
                EventTasks.updateStatus("XiaoMiPush 连接成功", PushSdkManagerConfig.PUSH_CONNECTION_RESULTCODE_SUCCESS);
            } else {
                EventTasks.updateStatus("XiaoMiPush 连接失败" + reason, (int) ResultCode);
            }
        } else {
            reason = message.getReason();
            EventTasks.updateStatus("XiaoMiPush 连接失败" + reason, (int) ResultCode);
        }
    }
}

