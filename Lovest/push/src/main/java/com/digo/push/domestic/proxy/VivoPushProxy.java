package com.digo.push.domestic.proxy;

import android.content.Context;
import android.text.TextUtils;

import com.digo.platform.logan.mine.Logz;
import com.digo.push.domestic.manager.EventTasks;
import com.digo.push.domestic.manager.PushSdkManager;
import com.digo.push.domestic.manager.PushSdkManagerConfig;
import com.digo.push.domestic.util.SharedPreUtils;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;

public class VivoPushProxy extends BasePushProxy {
    public static final String TAG = " VivoPushProxy ";
    private static volatile VivoPushProxy instance = null;

    private VivoPushProxy() {
    }

    public static VivoPushProxy getInstance() {
        if (instance == null) {
            synchronized (VivoPushProxy.class) {
                if (instance == null) {
                    instance = new VivoPushProxy();
                }
            }
        }
        return instance;
    }

    @Override
    public void init(Context context) {
        PushClient.getInstance(context).initialize();
        PushClient.getInstance(context).turnOnPush(new IPushActionListener() {
            @Override
            public void onStateChanged(int state) {
                String text = "";
                if (state != 0) {
                    text = "vivo push 注册失败[" + state + "]";
                } else {
                    text = "vivo push 注册成功";
                    //由于vivo token只回调一次，需要手动取出上传服务端
                    if (!TextUtils.isEmpty(SharedPreUtils.getVivoToken())) {
                        PushSdkManager.getInstance().setOpenPushEntry(PushSdkManagerConfig.PushType.PUSH_TYPE_VIVO, SharedPreUtils.getVivoToken());
                    }
                }
                Logz.d(text);
                EventTasks.updateStatus(text, state);
            }
        });
    }

    /**
     * 获取RegId
     *
     * @param context
     * @return
     */
    public String getRegId(Context context) {
        return PushClient.getInstance(context).getRegId();
    }

    /**
     * 当前系统是否支持 PUSH
     *
     * @param context
     * @return
     */
    public boolean isSupport(Context context) {
        return PushClient.getInstance(context).isSupport();
    }

    /**
     * 注销推送
     */
    public void unRegisterVivoPush(Context context) {
        PushClient.getInstance(context).turnOffPush(new IPushActionListener() {
            @Override
            public void onStateChanged(int state) {
                if (state != 0) {
                    Logz.d("vivo push 注销异常[" + state + "]");
                } else {
                    Logz.d("vivo push 注销成功");
                }
            }
        });
    }
}
