package com.digo.platform.webview;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;

import com.digo.platform.logan.mine.Logz;

import static com.digo.platform.webview.WebViewBussiness.WebViewTag;

/**
 * 原始内核实现
 * Create by shipei on 2018/12/12
 */

class AndroidWebChromeClientProxy extends WebChromeClient{

    private LWebView lWebView;
    private LWebChromeClient lClient;

    AndroidWebChromeClientProxy(LWebView lWebView, LWebChromeClient lClient) {
        this.lWebView = lWebView;
        this.lClient = lClient;
    }

    @Override
    public void onProgressChanged(android.webkit.WebView webView, int i) {
        Logz.tag(WebViewTag).d("LWebView AChromeWebClient onProgressChanged process=%d", i);
        lClient.onProgressChanged(lWebView, i);
    }

    @Override
    public void onReceivedTitle(android.webkit.WebView webView, String s) {
        Logz.tag(WebViewTag).i("LWebView AChromeWebClient onReceivedTitle onReceivedTitle title=%s", s);
        lClient.onReceivedTitle(lWebView, s);
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        AndroidConsoleMessage androidConsoleMessage = new AndroidConsoleMessage(consoleMessage);
        Logz.tag(WebViewTag).d("LWebView AChromeWebClient onConsoleMessage onConsoleMessage %s", androidConsoleMessage.toString());
        return lClient.onConsoleMessage(androidConsoleMessage);
    }

    @Override
    public boolean onJsPrompt(android.webkit.WebView webView, String s, String s1, String s2, JsPromptResult jsPromptResult) {
        Logz.tag(WebViewTag).i("LWebView AChromeWebClient onJsPrompt onJsPrompt url=%s, message=%s, defaultValue=%s",
                s, s1, s2);
        return lClient.onJsPrompt(lWebView, s, s1, s2, new AndroidJsPromptResult(jsPromptResult));
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean onShowFileChooser(android.webkit.WebView webView, android.webkit.ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        Logz.tag(WebViewTag).i("LWebView AChromeWebClient onShowFileChooser");
        return lClient.onShowFileChooser(lWebView, filePathCallback, new AndroidFileChooserParams(fileChooserParams));
    }

    static class AndroidConsoleMessage extends LConsoleMessage {

        ConsoleMessage message;

        AndroidConsoleMessage(ConsoleMessage message) {
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

    static class AndroidJsPromptResult extends LJsPromptResult {

        private JsPromptResult promptResult;

        AndroidJsPromptResult(JsPromptResult promptResult) {
            this.promptResult = promptResult;
        }

        @Override
        public void confirm(String s) {
            if (promptResult != null) {
                promptResult.confirm(s);
            }
        }
    }

    static class AndroidFileChooserParams extends LFileChooserParams{

        FileChooserParams fileChooserParams;

        public AndroidFileChooserParams(FileChooserParams fileChooserParams) {
            this.fileChooserParams = fileChooserParams;
        }

        @Override
        public int getMode() {
            if (fileChooserParams != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return fileChooserParams.getMode();
            }
            return 0;
        }

        @Override
        public String[] getAcceptTypes() {
            if (fileChooserParams != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return fileChooserParams.getAcceptTypes();
            }
            return new String[0];
        }

        @Override
        public boolean isCaptureEnabled() {
            if (fileChooserParams != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return fileChooserParams.isCaptureEnabled();
            }
            return false;
        }

        @Override
        public CharSequence getTitle() {
            if (fileChooserParams != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return fileChooserParams.getTitle();
            }
            return null;
        }

        @Override
        public String getFilenameHint() {
            if (fileChooserParams != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return fileChooserParams.getFilenameHint();
            }
            return null;
        }

        @Override
        public Intent createIntent() {
            if (fileChooserParams != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return fileChooserParams.createIntent();
            }
            return null;
        }
    }
}
