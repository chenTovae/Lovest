package com.digo.platform.webview;

import android.content.Context;

import com.digo.platform.logan.mine.Logz;

import static com.digo.platform.webview.WebViewBussiness.WebViewTag;

public class WebViewProvider {

    public IWebView getWebView(Context context) {
        Logz.tag(WebViewTag).i("LWebView WebView load config Init View add Tencent-X5 WebView");
        return new X5WebViewWrapper(context);
        //Logz.tag(WebViewTag).i("LWebView WebView load config Init View add Android-webkit WebView");
        //return new AndroidWebViewWrapper(context);
    }
}
