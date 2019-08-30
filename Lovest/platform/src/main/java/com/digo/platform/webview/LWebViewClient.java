package com.digo.platform.webview;

import android.graphics.Bitmap;

public abstract class LWebViewClient {

    public void onPageStarted(LWebView view, String url, Bitmap favicon) {}

    public void onPageFinished(LWebView view, String url) {}

    public boolean shouldOverrideUrlLoading(LWebView view, String url) {
        return false;
    }

    public boolean shouldOverrideUrlLoading(LWebView view, LWebResourceRequest request) {
        return false;
    }

    public void onReceivedSslError(LWebView view, LSslErrorHandler handler, LSslError error) {}

    public void onReceivedError(LWebView view, int errorCode, String description, String failingUrl) {}

    public void onReceivedError(LWebView view, LWebResourceRequest request, LWebResourceError error) {}

    public void onReceivedHttpError(LWebView view, LWebResourceRequest request, LWebResourceResponse errorResponse) {}
}
