package com.digo.push.domestic.proxy;

import android.content.Context;
import android.text.TextUtils;

import com.digo.platform.logan.mine.Logz;
import com.digo.push.domestic.manager.EventTasks;
import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.huawei.hms.support.api.push.PushException;
import com.huawei.hms.support.api.push.TokenResult;

public class HuaWeiPushProxy extends BasePushProxy implements HuaweiApiClient.ConnectionCallbacks, HuaweiApiClient.OnConnectionFailedListener {
    public static final String TAG = " HuaWeiPushProxy ";
    private static HuaWeiPushProxy instance = null;

    private HuaWeiPushProxy() {
    }

    static {
        instance = new HuaWeiPushProxy();
        Logz.d(TAG + " create " + TAG);
    }

    public static HuaWeiPushProxy getInstance() {
        return instance;
    }

    private HuaweiApiClient client;

    /**
     * 华为推送连接
     *
     * @param context
     */
    @Override
    public void init(Context context) {
        Logz.d(TAG + "HuaweiApiClient connectting...");
        //创建华为移动服务client实例用以使用华为push服务  需要指定api为HuaweiId.PUSH_API  连接回调以及连接失败监听

        client = new HuaweiApiClient.Builder(context)
                .addApi(HuaweiPush.PUSH_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        //建议在oncreate的时候连接华为移动服务 业务可以根据自己业务的形态来确定client的连接和断开的时机，但是确保connect和disconnect必须成对出现
        client.connect();
    }

    /**
     * 华为推送连接成功回调
     */
    @Override
    public void onConnected() {
        //华为移动服务client连接成功，在这边处理业务自己的事件
        Logz.d(TAG + "HuaweiApiClient 连接成功");
        getTokenAsyn();//连接成功+获取token成功证明通道成功
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        Logz.d(TAG + "HuaweiApiClient连接失败，错误码：" + arg0.getErrorCode());
        EventTasks.updateStatus("HuaweiPush 连接失败", arg0.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        //HuaweiApiClient异常断开连接, if 括号里的条件可以根据需要修改
        Logz.d(TAG + "HuaweiApiClient 连接断开");
        if (client != null) {
            client.connect();
            Logz.d(TAG + "HuaweiApiClient 连接断开重连中");
        }
    }

    /**
     * 手动断开华为推送连接****谨慎调用
     */
    private void disconnect() {
        //建议在onDestroy的时候停止连接华为移动服务 业务可以根据自己业务的形态来确定client的连接和断开的时机，但是确保connect和disconnect必须成对出现
        client.disconnect();
    }

    /**
     * 获取华为推送token
     * 申请token会触发启动Push服务，token申请成功后，结果会通过广播的方式返回token给应用。token结果会在HuaWeiReceiver 里面的onToken里面返回
     * 调用getToken方法发起请求，返回申请token的PendingResult对象，根据对象可以获取接口调用是否成功，但是不直接返回token 结果。
     */
    private void getTokenAsyn() {
        if (!client.isConnected()) {
            Logz.d(TAG + "获取token失败，原因：HuaweiApiClient未连接");
            client.connect();
            return;
        }

        Logz.d(TAG + "异步接口获取push token");
        PendingResult<TokenResult> tokenResult = HuaweiPush.HuaweiPushApi.getToken(client);
        tokenResult.setResultCallback(new ResultCallback<TokenResult>() {
            @Override
            public void onResult(TokenResult result) {
                Logz.d(TAG + "TokenResult" + result);
            }
        });
    }

    /**
     * 删除华为服务器的华为token
     * 调用此接口必须确保华为移动服务（HMS）已经连接成功。
     *
     * @param token
     */
    public void unRegisterHuaWeiPush(final String token) {
        if (!client.isConnected()) {
            Logz.d(TAG + "注销token失败，原因：HuaweiApiClient未连接");
            client.connect();
            return;
        }
        //需要在子线程中执行删除token操作
        new Thread() {
            @Override
            public void run() {
                //调用删除token需要传入通过getToken接口获取到token，并且需要对token进行非空判断
                Logz.i(TAG + "删除Token：" + token);
                if (!TextUtils.isEmpty(token)) {
                    try {
                        HuaweiPush.HuaweiPushApi.deleteToken(client, token);
                    } catch (PushException e) {
                        Logz.d(TAG + "删除Token失败:" + e.getMessage());
                    }
                }
            }

        }.start();
    }
}
