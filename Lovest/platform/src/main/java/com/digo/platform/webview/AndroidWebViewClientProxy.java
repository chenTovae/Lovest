package com.digo.platform.webview;

import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.os.Build;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.digo.platform.logan.mine.Logz;

import java.io.InputStream;
import java.util.Map;

import static com.digo.platform.webview.WebViewBussiness.WebViewTag;

/**
 * 原始内核实现
 * Create by shipei on 2018/12/12
 */
class AndroidWebViewClientProxy extends WebViewClient {

    private LWebView lWebView;
    private LWebViewClient client;

    AndroidWebViewClientProxy(LWebView lWebView, LWebViewClient client) {
        this.lWebView = lWebView;
        this.client = client;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Logz.tag(WebViewTag).i("LWebView AWebViewClient onPageStarted url=%s", url);
        client.onPageStarted(lWebView, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        Logz.tag(WebViewTag).i("LWebView AWebViewClient onPageFinished url=%s", url);
        client.onPageFinished(lWebView, url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Logz.tag(WebViewTag).i("LWebView AWebViewClient shouldOverrideUrlLoading url=%s", url);
        return client.shouldOverrideUrlLoading(lWebView, url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        AndroidWebResourceRequest androidWebResourceRequest =new AndroidWebResourceRequest(request);
        Logz.tag(WebViewTag).i("LWebView AWebViewClient shouldOverrideUrlLoading request=%s", androidWebResourceRequest.toString());
        return client.shouldOverrideUrlLoading(lWebView, androidWebResourceRequest);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        AndroidSslError androidSslError = new AndroidSslError(error);
        Logz.tag(WebViewTag).e("LWebView AWebViewClient onReceivedSslError error=%s", androidSslError.toString());
        client.onReceivedSslError(lWebView, new AndroidSslErrorHandler(handler), androidSslError);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        Logz.tag(WebViewTag).e("LWebView AWebViewClient onReceivedError description=%s, failUrl=%s", description, failingUrl);
        client.onReceivedError(lWebView, errorCode, description, failingUrl);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        AndroidWebResourceRequest androidWebResourceRequest = new AndroidWebResourceRequest(request);
        AndroidWebResourceError androidWebResourceError = new AndroidWebResourceError(error);
        Logz.tag(WebViewTag).e("LWebView AWebViewClient onReceivedError request=%s, error=%s", androidWebResourceRequest.toString(), androidWebResourceError.toString());
        client.onReceivedError(lWebView, androidWebResourceRequest, androidWebResourceError);
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        AndroidWebResourceRequest androidWebResourceRequest = new AndroidWebResourceRequest(request);
        AndroidWebResourceResponse androidWebResourceResponse = new AndroidWebResourceResponse(errorResponse);
        Logz.tag(WebViewTag).e("LWebView AWebViewClient onReceivedError request=%s, Response=%s", androidWebResourceRequest.toString(), androidWebResourceResponse.toString());
        client.onReceivedHttpError(lWebView, androidWebResourceRequest, androidWebResourceResponse);
    }

    static class AndroidSslErrorHandler extends LSslErrorHandler {

        private SslErrorHandler handler;

        AndroidSslErrorHandler(SslErrorHandler handler) {
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

    static class AndroidSslError extends LSslError {

        private SslError sslError;

        AndroidSslError(SslError sslError) {
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
            return sslError == null ? null : sslError.getUrl();
        }
    }

    static class AndroidWebResourceError extends LWebResourceError {

        WebResourceError error;

        AndroidWebResourceError(WebResourceError error) {
            this.error = error;
        }

        @Override
        public int getErrorCode() {
            if (error != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return error.getErrorCode();
            }
            return 0;
        }

        @Override
        public CharSequence getDescription() {
            if (error != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                error.getDescription();
            }
            return null;
        }
    }

    static class AndroidWebResourceRequest extends LWebResourceRequest {

        private WebResourceRequest request;

        AndroidWebResourceRequest(WebResourceRequest request) {
            this.request = request;
        }

        @Override
        public Uri getUrl() {
            if (request != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return request.getUrl();
            }
            return null;
        }

        @Override
        public String getUrlString() {
            if (request != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && request.getUrl() != null) {
                return request.getUrl().toString();
            }
            return null;
        }

        @Override
        public boolean isForMainFrame() {
            return request != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && request.isForMainFrame();
        }

        @Override
        public boolean isRedirect() {
            return request != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && request.isRedirect();
        }

        @Override
        public boolean hasGesture() {
            return request != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && request.hasGesture();
        }

        @Override
        public String getMethod() {
            if (request != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return request.getMethod();
            }
            return null;
        }

        @Override
        public Map<String, String> getRequestHeaders() {
            if (request != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return request.getRequestHeaders();
            }
            return null;
        }
    }

    static class AndroidWebResourceResponse extends LWebResourceResponse {

        private WebResourceResponse response;

        AndroidWebResourceResponse(WebResourceResponse response) {
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
            if (response != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                response.setStatusCodeAndReasonPhrase(var1, var2);
            }
        }

        @Override
        public int getStatusCode() {
            if (response != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return response.getStatusCode();
            }
            return 0;
        }

        @Override
        public String getReasonPhrase() {
            if (response != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return response.getReasonPhrase();
            }
            return null;
        }

        @Override
        public void setResponseHeaders(Map<String, String> var1) {
            if (response != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                response.setResponseHeaders(var1);
            }
        }

        @Override
        public Map<String, String> getResponseHeaders() {
            if (response != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return response.getResponseHeaders();
            }
            return null;
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
