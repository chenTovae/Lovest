package com.digo.push.domestic.event;

public class PushMessageReceiveEvent {
    public String mMessage;
    public String mGroupId;
    public String mTitle;
    public String mContent;
    public int mPushType;
    public String mCustomContent;

    public PushMessageReceiveEvent(String message, String groupId, String title, String content, int pushType, String customContent) {
        this.mMessage = message;
        this.mGroupId = groupId;
        this.mTitle = title;
        this.mContent = content;
        this.mPushType = pushType;
        this.mCustomContent = customContent;
    }
}
