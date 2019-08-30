package com.digo.platform.webview;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.webkit.WebView;

import com.digo.platform.ApplicationUtils;
import com.tencent.smtt.sdk.ValueCallback;

/**
 * 原始内核实现
 * Create by shipei on 2018/12/12
 */
class AndroidWebViewWrapper implements IWebView{

    private WebView webView;

    AndroidWebViewWrapper(Context context) {
        webView = new WebView(context);
    }

    @Override
    public View getView() {
        return webView;
    }

    @Override
    public void setWebContentsDebuggingEnabled(boolean enabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(enabled);
        }
    }

    @Override
    public void evaluateJavascript(String javascript, final ValueCallback<String> callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(javascript, callback);
        } else {
            webView.loadUrl(javascript);
            if (callback != null) {
                ApplicationUtils.mMainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onReceiveValue("");
                        }
                    }
                }, 100);
            }
        }
    }

    @Override
    public void loadUrl(String javascript) {
        webView.loadUrl(javascript);
    }

    @Override
    public String getUrl() {
        return webView.getUrl();
    }

    @Override
    public void reload() {
        webView.reload();
    }

    @Override
    public void stopLoading() {
        webView.stopLoading();
    }

    @Override
    public void goBack() {
        webView.goBack();
    }

    @Override
    public boolean canGoBack() {
        return webView.canGoBack();
    }

    @Override
    public void onPause() {
        webView.onPause();
    }

    @Override
    public void clearFormData() {
        webView.clearFormData();
    }

    @Override
    public void clearMatches() {
        webView.clearMatches();
    }

    @Override
    public void clearSslPreferences() {
        webView.clearSslPreferences();
    }

    @Override
    public void clearDisappearingChildren() {
        webView.clearDisappearingChildren();
    }

    @Override
    public void clearCache(boolean includeDiskFiles) {
        webView.clearCache(includeDiskFiles);
    }

    @Override
    public void clearHistory() {
        webView.clearHistory();
    }

    @Override
    public void destroy() {
        webView.destroy();
    }

    @Override
    public void freeMemory() {
        webView.freeMemory();
    }

    @Override
    public void removeAllViews() {
        webView.removeAllViews();
    }

    @Override
    public void removeJavascriptInterface(String name) {
        webView.removeJavascriptInterface(name);
    }

    @Override
    public LHitTestResult getHitTestResult() {
        return new HitTestResult(webView.getHitTestResult());
    }

    @Override
    public LWebSettings getSettings() {
        return new AndroidWebSettingsWrapper(webView.getSettings());
    }

    @Override
    public void setWebChromeClient(LWebView lWebView, LWebChromeClient webChromeClient) {
        if (webChromeClient != null) {
            webView.setWebChromeClient(new AndroidWebChromeClientProxy(lWebView, webChromeClient));
        }
    }

    @Override
    public void setWebViewClient(LWebView lWebView, LWebViewClient webViewClient) {
        if (webViewClient != null) {
            webView.setWebViewClient(new AndroidWebViewClientProxy(lWebView, webViewClient));
        }
    }

    @Override
    public void setDownloadListener(LDownloadListener listener) {
        webView.setDownloadListener(listener);
    }

    @Override
    public void onResume() {
        webView.onResume();
    }

    static class HitTestResult extends LHitTestResult{

        WebView.HitTestResult result;

        public HitTestResult(WebView.HitTestResult result) {
            this.result = result;
        }

        @Override
        public int getType() {
            return result == null ? getUNKNOWNTYPE() : result.getType();
        }

        @Override
        public String getExtra() {
            return result == null ? "" : result.getExtra();
        }

        @Override
        public int getUNKNOWNTYPE() {
            return WebView.HitTestResult.UNKNOWN_TYPE;
        }
    }
}
