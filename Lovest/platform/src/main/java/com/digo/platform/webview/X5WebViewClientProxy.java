package com.digo.platform.webview;

import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslCertificate;

import com.digo.platform.logan.mine.Logz;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.InputStream;
import java.util.Map;

import static com.digo.platform.webview.WebViewBussiness.WebViewTag;

class X5WebViewClientProxy extends WebViewClient {

    private LWebView lWebView;
    private LWebViewClient client;

    X5WebViewClientProxy(LWebView lWebView, LWebViewClient client) {
        this.lWebView = lWebView;
        this.client = client;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Logz.tag(WebViewTag).i("LWebView X5WebViewClient onPageStarted url=%s", url);
        client.onPageStarted(lWebView, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        Logz.tag(WebViewTag).i("LWebView X5WebViewClient onPageFinished url=%s", url);
        client.onPageFinished(lWebView, url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Logz.tag(WebViewTag).i("LWebView X5WebViewClient shouldOverrideUrlLoading url=%s", url);
        return client.shouldOverrideUrlLoading(lWebView, url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
        Logz.tag(WebViewTag).i("LWebView X5WebViewClient shouldOverrideUrlLoading request=%s", webResourceRequest.toString());
        return client.shouldOverrideUrlLoading(lWebView, new X5WebResourceRequest(webResourceRequest));
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        X5SslError x5SslError = new X5SslError(error, view.getUrl());
        Logz.tag(WebViewTag).e("LWebView X5WebViewClient onReceivedSslError error=%s", x5SslError.toString());
        client.onReceivedSslError(lWebView, new X5SslErrorHandler(handler), x5SslError);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        Logz.tag(WebViewTag).e("LWebView X5WebViewClient onReceivedError description=%s, failUrl=%s", description, failingUrl);
        client.onReceivedError(lWebView, errorCode, description, failingUrl);
    }

    @Override
    public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
        X5WebResourceRequest x5WebResourceRequest = new X5WebResourceRequest(webResourceRequest);
        X5WebResourceError x5WebResourceError = new X5WebResourceError(webResourceError);
        Logz.tag(WebViewTag).e("LWebView X5WebViewClient onReceivedError request=%s, error=%s", x5WebResourceRequest.toString(), x5WebResourceError.toString());
        client.onReceivedError(lWebView, x5WebResourceRequest, x5WebResourceError);
    }

    @Override
    public void onReceivedHttpError(WebView webView, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {
        X5WebResourceRequest x5WebResourceRequest = new X5WebResourceRequest(webResourceRequest);
        X5WebResourceResponse x5WebResourceResponse = new X5WebResourceResponse(webResourceResponse);
        Logz.tag(WebViewTag).e("LWebView X5WebViewClient onReceivedError request=%s, Response=%s", x5WebResourceRequest.toString(), x5WebResourceResponse.toString());
        client.onReceivedHttpError(lWebView, x5WebResourceRequest, x5WebResourceResponse);
    }

    static class X5SslErrorHandler extends LSslErrorHandler {

        private SslErrorHandler handler;

        X5SslErrorHandler(SslErrorHandler handler) {
            this.handler = handler;
        }

        @Override
        public void proceed() {
            if (handler != null) {
                handler.proceed();
            }
        }

        @Override
        public void cancel() {
            if (handler != null) {
                handler.cancel();
            }
        }
    }

    static class X5SslError extends LSslError{

        private SslError sslError;

        private String url;

        X5SslError(SslError sslError, String url) {
            this.sslError = sslError;
        }

        @Override
        public SslCertificate getCertificate() {
            return sslError == null ? null : sslError.getCertificate();
        }

        @Override
        public boolean addError(int var1) {
            return sslError != null && sslError.addError(var1);
        }

        @Override
        public boolean hasError(int var1) {
            return sslError != null && sslError.hasError(var1);
        }

        @Override
        public int getPrimaryError() {
            return sslError == null ? 0 : sslError.getPrimaryError();
        }

        @Override
        public String getUrl() {
            return url;
        }
    }

    static class X5WebResourceError extends LWebResourceError{

        private WebResourceError error;

        public X5WebResourceError(WebResourceError error) {
            this.error = error;
        }

        @Override
        public int getErrorCode() {
            return error == null ? 0 : error.getErrorCode();
        }

        @Override
        public CharSequence getDescription() {
            return error == null ? "" : error.getDescription();
        }
    }

    static class X5WebResourceRequest extends LWebResourceRequest {

        private WebResourceRequest request;

        public X5WebResourceRequest(WebResourceRequest request) {
            this.request = request;
        }

        @Override
        public Uri getUrl() {
            return request == null ? null : request.getUrl();
        }

        @Override
        public String getUrlString() {
            if (request != null && request.getUrl() != null) {
                return request.getUrl().toString();
            }
            return null;
        }

        @Override
        public boolean isForMainFrame() {
            return request != null && request.isForMainFrame();
        }

        @Override
        public boolean isRedirect() {
            return request != null && request.isRedirect();
        }

        @Override
        public boolean hasGesture() {
            return request != null && request.hasGesture();
        }

        @Override
        public String getMethod() {
            return request == null ? null : request.getMethod();
        }

        @Override
        public Map<String, String> getRequestHeaders() {
            return request == null ? null : request.getRequestHeaders();
        }
    }

    static class X5WebResourceResponse extends LWebResourceResponse {

        private WebResourceResponse response;

        X5WebResourceResponse(WebResourceResponse response) {
            this.response = response;
        }

        @Override
        public void setMimeType(String var1) {
            if (response != null) {
                response.setMimeType(var1);
            }
        }

        @Override
        public String getMimeType() {
            return response == null ? null : response.getMimeType();
        }

        @Override
        public void setEncoding(String var1) {
            if (response != null) {
                response.setEncoding(var1);
            }
        }

        @Override
        public String getEncoding() {
            return response == null ? null : response.getEncoding();
        }

        @Override
        public void setStatusCodeAndReasonPhrase(int var1, String var2) {
            if (response != null) {
                response.setStatusCodeAndReasonPhrase(var1, var2);
            }
        }

        @Override
        public int getStatusCode() {
            return response == null ? 0 : response.getStatusCode();
        }

        @Override
        public String getReasonPhrase() {
            return response == null ? null : response.getReasonPhrase();
        }

        @Override
        public void setResponseHeaders(Map<String, String> var1) {
            if (response != null) {
                response.setResponseHeaders(var1);
            }
        }

        @Override
        public Map<String, String> getResponseHeaders() {
            return response == null ? null : response.getResponseHeaders();
        }

        @Override
        public void setData(InputStream var1) {
            if (response != null) {
                response.setData(var1);
            }
        }

        @Override
        public InputStream getData() {
            return response == null ? null : response.getData();
        }
    }
}
