package com.digo.push.domestic.receiver;

import android.content.Context;

import com.coloros.mcssdk.PushService;
import com.coloros.mcssdk.mode.AppMessage;
import com.coloros.mcssdk.mode.CommandMessage;
import com.coloros.mcssdk.mode.SptDataMessage;
import com.digo.platform.logan.mine.Logz;
import com.google.gson.Gson;

public class OppoPushService extends PushService {
    private static final String TAG = "OppoPushService";

    @Override
    public void processMessage(Context context, AppMessage appMessage) {
        super.processMessage(context, appMessage);

        if (appMessage != null) {
            Gson gson = new Gson();
            Logz.d(TAG + " appMessage: " + gson.toJson(appMessage));
        }
    }

    /**
     * 通知栏消息
     *
     * @param context
     * @param sptDataMessage
     */
    @Override
    public void processMessage(Context context, SptDataMessage sptDataMessage) {
        super.processMessage(context, sptDataMessage);
        if (sptDataMessage != null) {
            Gson gson = new Gson();
            Logz.d(TAG + " sptDataMessage: " + gson.toJson(sptDataMessage));
        }
    }

    /**
     * 透传消息
     *
     * @param context
     * @param commandMessage
     */
    @Override
    public void processMessage(Context context, CommandMessage commandMessage) {
        super.processMessage(context, commandMessage);
        if (commandMessage != null) {
            Gson gson = new Gson();
            Logz.d(TAG + " CommandMessage: " + gson.toJson(commandMessage));
        }
    }

}
