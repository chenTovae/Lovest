package com.digo.push.domestic.event;

public class PushConnectionStatusEvent {
    public String mStatus;
    public int mResultCode;

    public PushConnectionStatusEvent(String status, int resultCode) {
        this.mStatus = status;
        this.mResultCode = resultCode;
    }
}
