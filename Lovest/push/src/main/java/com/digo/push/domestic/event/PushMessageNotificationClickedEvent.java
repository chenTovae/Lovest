package com.digo.push.domestic.event;

/**
 * author: JackSong .
 * describe:Notification onClicked
 * on 2017/12/13.
 */

public class PushMessageNotificationClickedEvent {
    public String mMessage;
    public long mNotifyId;
    public String mTitle;
    public String mContent;
    public int mPushType;

    public PushMessageNotificationClickedEvent(String message, long notifyId, String title, String content, int pushType) {
        this.mMessage = message;
        this.mNotifyId = notifyId;
        this.mTitle = title;
        this.mContent = content;
        this.mPushType = pushType;
    }
}
