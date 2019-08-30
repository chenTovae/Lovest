package com.digo.push.domestic.event;

public class PushMessageNotificationDeletedEvent {
    public String mMessage;

    public PushMessageNotificationDeletedEvent(String message) {
        this.mMessage = message;
    }
}
