package com.digo.platform.webview;

import android.net.Uri;
import android.webkit.ValueCallback;

public abstract class LWebChromeClient {
    public void onProgressChanged(LWebView view, int newProgress) {
    }

    public void onReceivedTitle(LWebView view, String title) {
    }

    public boolean onConsoleMessage(LConsoleMessage lConsoleMessage) {
        return false;
    }

    public boolean onJsPrompt(LWebView view, String url, String message, String defaultValue, LJsPromptResult lResult) {
        return false;
    }

    public boolean onShowFileChooser(LWebView view, ValueCallback<Uri[]> lc, LFileChooserParams var3) {
        return false;
    }

}
