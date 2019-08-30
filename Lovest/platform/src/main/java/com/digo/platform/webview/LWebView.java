package com.digo.platform.webview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.digo.platform.logan.mine.Logz;
import com.tencent.smtt.sdk.ValueCallback;
import java.util.HashMap;
import static com.digo.platform.webview.WebViewBussiness.WebViewTag;

public class LWebView extends FrameLayout {

    private HashMap<String, String> mTokenHashMap = new HashMap<>();

    private String mUdId = "";

    private IWebView webView;

    protected boolean isLoadJavascript;

    public LWebView(Context context) {
        super(context);
        initView(context);
    }

    public LWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        webView = new WebViewProvider().getWebView(context);
        addView(webView.getView(), new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setJavaScriptEnabled(true);
        setWebContentsDebuggingEnabled(true);
    }

    protected void loadJavascriptCallBack() {

    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        webView.getView().setBackgroundColor(color);
    }

    public void setHorizontalScrollBarEnabled(boolean enabled) {
        webView.getView().setHorizontalScrollBarEnabled(enabled);
    }

    public void setVerticalScrollBarEnabled(boolean enabled) {
        webView.getView().setVerticalScrollBarEnabled(enabled);
    }

    /**
     * 配置通过JS回调方式采集开关
     *
     * @param enabled
     */
    public void setWebContentsDebuggingEnabled(boolean enabled) {
        webView.setWebContentsDebuggingEnabled(enabled);
        Logz.tag(WebViewTag).i("LWebView WebView load config setWebContentsDebuggingEnabled enabled=%b", enabled);
    }

    public void setJavaScriptEnabled(boolean enabled) {
        getSettings().setJavaScriptEnabled(enabled);
    }

    public void evaluateJavascript(String javascript, final ValueCallback<String> callback) {
        webView.evaluateJavascript(javascript, callback);
        Logz.tag(WebViewTag).i("LWebView WebView call trigerEvent evaluateJavascript javascript=%s", javascript);
    }

    public void loadUrl(String javascript) {
        webView.loadUrl(javascript);
        Logz.tag(WebViewTag).i("WebView begin request start loadUrl : %s", javascript);
    }

    public String getUrl() {
        return webView.getUrl();
    }

    public void reload() {
        Logz.tag(WebViewTag).i("LWebView reload");
        webView.reload();
    }

    public void stopLoading() {
        webView.stopLoading();
    }

    public void goBack() {
        webView.goBack();
    }

    public boolean canGoBack() {
        return webView.canGoBack();
    }

    public void onPause() {
        webView.onPause();
    }

    public void onResume() {
        webView.onResume();
    }

    public void clearFormData() {
        webView.clearFormData();
    }

    public void clearMatches() {
        webView.clearMatches();
    }

    public void clearSslPreferences() {
        Logz.tag(WebViewTag).i("LWebView clearSslPreferences");
        webView.clearSslPreferences();
    }

    public void clearDisappearingChildren() {
        webView.clearDisappearingChildren();
    }

    public void clearCache(boolean includeDiskFiles) {
        Logz.tag(WebViewTag).i("LWebView clearCache includeDiskFiles=%b", includeDiskFiles);
        webView.clearCache(includeDiskFiles);
    }

    public void clearHistory() {
        Logz.tag(WebViewTag).i("LWebView clearHistory");
        webView.clearHistory();
    }

    public void destroy() {
        Logz.tag(WebViewTag).i("LWebView destroy");
        webView.destroy();
    }

    public void freeMemory() {
        Logz.tag(WebViewTag).i("LWebView freeMemory");
        webView.freeMemory();
    }

    public void removeAllViews() {
        Logz.tag(WebViewTag).i("LWebView removeAllViews");
        webView.removeAllViews();
    }

    public void removeJavascriptInterface(String name) {
        Logz.tag(WebViewTag).i("LWebView removeJavascriptInterface name=%s", name);
        webView.removeJavascriptInterface(name);
    }

    public LHitTestResult getHitTestResult() {
        return webView.getHitTestResult();
    }

    public LWebSettings getSettings() {
        return webView.getSettings();
    }

    public void setWebChromeClient(LWebChromeClient webChromeClient) {
        Logz.tag(WebViewTag).i("LWebView WebView load config setWebChromeClient");
        webView.setWebChromeClient(this, webChromeClient);
    }

    public void setWebViewClient(LWebViewClient webViewClient) {
        Logz.tag(WebViewTag).i("LWebView WebView load config setWebViewClient");
        webView.setWebViewClient(this, webViewClient);
    }

    public void setDownloadListener(LDownloadListener listener) {
        webView.setDownloadListener(listener);
    }

    public void saveLizhiToken(String url, String token) {
        mTokenHashMap.put(url, token);
    }

    public String getLizhiToken(String url) {
        return mTokenHashMap.get(url);
    }

    public String getUdId() {
        return mUdId;
    }

    public void setUdid(String udId) {
        mUdId = udId;
    }

    public void triggerJsEvent(String eventName, String jsonParam) {
        triggerJsEvent(eventName, jsonParam, null);
    }

    public void triggerJsEvent(String eventName, String jsonParam, ValueCallback<String> callback) {
        evaluateJavascript(new StringBuilder()
                .append("javascript:LizhiJSBridge._triggerEventsByNameAndArg(")
                .append("\"")
                .append(eventName)
                .append("\"")
                .append(",")
                .append(jsonParam)
                .append(")")
                .toString(), callback
        );
        Logz.tag(WebViewTag).i(new StringBuilder()
                .append("javascript:LizhiJSBridge._triggerEventsByNameAndArg(")
                .append("\"")
                .append(eventName)
                .append("\"")
                .append(",")
                .append(jsonParam)
                .append(")")
                .toString());
    }

    /**
     * 由于构造函数可能调用toString，请勿在此调用webView实例方法
     *
     * @return
     */
    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * 判断是否为X5WebView
     *
     * @return
     */
    public boolean isX5WebView() {
        if (webView instanceof X5WebViewWrapper) {
            return true;
        }
        return false;
    }

    /**
     * 获取WebView
     *
     * @return
     */
    public View getWebView() {
        return webView.getView();
    }
}
