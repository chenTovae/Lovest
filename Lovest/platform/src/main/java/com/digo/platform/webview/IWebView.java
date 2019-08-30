package com.digo.platform.webview;

import android.view.View;

import com.tencent.smtt.sdk.ValueCallback;

/**
 * WebView接口层
 * Create by shipei on 2018/12/12
 */
interface IWebView {

    View getView();

    void setWebContentsDebuggingEnabled(boolean enabled);

    void evaluateJavascript(String javascript, final ValueCallback<String> callback);

    void loadUrl(String javascript);

    String getUrl();

    void reload();

    void stopLoading();

    void goBack();

    boolean canGoBack();

    void onPause();

    void clearFormData();

    void clearMatches();

    void clearSslPreferences();

    void clearDisappearingChildren();

    void clearCache(boolean includeDiskFiles);

    void clearHistory();

    void destroy();

    void freeMemory();

    void removeAllViews();

    void removeJavascriptInterface(String name);

    LHitTestResult getHitTestResult();

    LWebSettings getSettings();

    void setWebChromeClient(LWebView webView, LWebChromeClient webChromeClient);

    void setWebViewClient(LWebView webView, LWebViewClient webViewClient);

    void setDownloadListener(LDownloadListener listener);

    void onResume();
}
