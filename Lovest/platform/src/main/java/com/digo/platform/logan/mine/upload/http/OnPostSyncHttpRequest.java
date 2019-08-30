package com.digo.platform.logan.mine.upload.http;

/**
 * Author : Create by Linxinyuan on 2019/01/18
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */
public interface OnPostSyncHttpRequest {
    void onPostSyncHttpSuccess(byte[] b);

    void onPostSyncHttpFail(Exception e);
}
