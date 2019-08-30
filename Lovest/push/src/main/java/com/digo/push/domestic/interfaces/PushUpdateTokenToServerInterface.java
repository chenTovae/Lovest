package com.digo.push.domestic.interfaces;

import com.digo.push.domestic.model.UpdatePushToken;

public interface PushUpdateTokenToServerInterface {
    /**
     * @param updatePushToken token封装
     */
    void updateTokenToServerCallBack(UpdatePushToken updatePushToken);
}
