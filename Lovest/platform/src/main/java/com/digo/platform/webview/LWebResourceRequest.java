package com.digo.platform.webview;

import android.net.Uri;

import java.util.Map;

public abstract class LWebResourceRequest {

    public abstract Uri getUrl();

    public abstract String getUrlString();

    public abstract boolean isForMainFrame();

    public abstract boolean isRedirect();

    public abstract boolean hasGesture();

    public abstract String getMethod();

    public abstract Map<String, String> getRequestHeaders();

    @Override
    public String toString() {
        return "WebResourceRequest url:" + getUrl() + ", method:" + getMethod();
    }
}
