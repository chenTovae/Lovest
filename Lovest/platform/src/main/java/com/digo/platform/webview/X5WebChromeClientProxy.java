package com.digo.platform.webview;

import android.content.Intent;
import android.net.Uri;

import com.digo.platform.logan.mine.Logz;
import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

import static com.digo.platform.webview.WebViewBussiness.WebViewTag;

class X5WebChromeClientProxy extends WebChromeClient{

    private LWebView lWebView;
    private LWebChromeClient lClient;

    X5WebChromeClientProxy(LWebView lWebView, LWebChromeClient lClient) {
        this.lWebView = lWebView;
        this.lClient = lClient;
    }

    @Override
    public void onProgressChanged(WebView webView, int i) {
        Logz.tag(WebViewTag).d("LWebView X5ChromeWebClient onProgressChanged process=%d", i);
        lClient.onProgressChanged(lWebView, i);
    }

    @Override
    public void onReceivedTitle(WebView webView, String s) {
        Logz.tag(WebViewTag).i("LWebView X5ChromeWebClient onReceivedTitle title=%s", s);
        lClient.onReceivedTitle(lWebView, s);
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        X5ConsoleMessage x5ConsoleMessage = new X5ConsoleMessage(consoleMessage);
        Logz.tag(WebViewTag).d("LWebView X5ChromeWebClient onConsoleMessage %s", x5ConsoleMessage.toString());
        return lClient.onConsoleMessage(x5ConsoleMessage);
    }

    @Override
    public boolean onJsPrompt(WebView webView, String s, String s1, String s2, JsPromptResult jsPromptResult) {
        Logz.tag(WebViewTag).i("LWebView X5ChromeWebClient onJsPrompt url=%s, message=%s, defaultValue=%s",
                s, s1, s2);
        return lClient.onJsPrompt(lWebView, s, s1, s2, new X5JsPromptResult(jsPromptResult));
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
        Logz.tag(WebViewTag).i("LWebView X5ChromeWebClient onShowFileChooser");
        return lClient.onShowFileChooser(lWebView, valueCallback, new X5FileChooserParams(fileChooserParams));
    }

    static class X5ConsoleMessage extends LConsoleMessage {

        ConsoleMessage message;

        X5ConsoleMessage(ConsoleMessage message) {
            this.message = message;
        }

        @Override
        public int lineNumber() {
            return message == null ? super.lineNumber() : message.lineNumber();
        }

        @Override
        public String message() {
            return message == null ? super.message() : message.message();
        }
    }

    static class X5JsPromptResult extends LJsPromptResult {

        private JsPromptResult promptResult;

        X5JsPromptResult(JsPromptResult promptResult) {
            this.promptResult = promptResult;
        }

        @Override
        public void confirm(String s) {
            if (promptResult != null) {
                promptResult.confirm(s);
            }
        }
    }

    static class X5FileChooserParams extends LFileChooserParams {

        private FileChooserParams params;

        public X5FileChooserParams(FileChooserParams fileChooserParams) {
            this.params = fileChooserParams;
        }

        @Override
        public int getMode() {
            return params == null ? 0 : params.getMode();
        }

        @Override
        public String[] getAcceptTypes() {
            return params == null ? new String[0] : params.getAcceptTypes();
        }

        @Override
        public boolean isCaptureEnabled() {
            return params != null && params.isCaptureEnabled();
        }

        @Override
        public CharSequence getTitle() {
            return params == null ? null : params.getTitle();
        }

        @Override
        public String getFilenameHint() {
            return params == null ? null : params.getFilenameHint();
        }

        @Override
        public Intent createIntent() {
            return params == null ? null : params.createIntent();
        }
    }
}
