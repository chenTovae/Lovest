package com.digo.platform.webview;

import android.webkit.DownloadListener;

/**
 * 下周监听
 * Create by shipei on 2018/12/12
 */
public abstract class LDownloadListener  implements DownloadListener, com.tencent.smtt.sdk.DownloadListener{

    @Override
    public abstract void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength);
}
