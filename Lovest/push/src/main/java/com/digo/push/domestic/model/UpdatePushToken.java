package com.digo.push.domestic.model;

/**
 * @author: JackSong .
 * describe: Update Push Token
 * on 2017/12/21.
 */

public class UpdatePushToken {
    /**
     * 初始值 标识为未初始化
     */
    public int mAct = -1;
    /**
     * 初始值 标识为未初始化
     */
    public int mPushType = -1;
    public String mToken;
    public String mModel;
    public long mTime;

    public int getAct() {
        return mAct;
    }

    public void setAct(int mAct) {
        this.mAct = mAct;
    }

    public int getPushType() {
        return mPushType;
    }

    public void setPushType(int mPushType) {
        this.mPushType = mPushType;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String mToken) {
        this.mToken = mToken;
    }

    public String getModel() {
        return mModel;
    }

    public void setModel(String mModel) {
        this.mModel = mModel;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        this.mTime = time;
    }

    /**
     * @param act
     * @param pushType
     * @param token
     * @param model
     * @param time
     */
    public UpdatePushToken(int act, int pushType, String token, String model, long time) {
        this.mAct = act;
        this.mModel = model;
        this.mPushType = pushType;
        this.mToken = token;
        this.mTime = time;
    }

    public UpdatePushToken() {
    }

    @Override
    public String toString() {
        return "UpdatePushToken{" +
                "mAct=" + mAct +
                ", mPushType=" + mPushType +
                ", mToken='" + mToken + '\'' +
                ", mModel='" + mModel + '\'' +
                ", mTime='" + mTime + '\'' +
                '}';
    }
}
