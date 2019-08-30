package com.digo.platform.webview;

import androidx.annotation.StringDef;

import static com.digo.platform.webview.WebViewBussiness.WebViewTag;

@StringDef(WebViewTag)
public @interface WebViewBussiness {
    String WebViewTag = "WebView";
}
