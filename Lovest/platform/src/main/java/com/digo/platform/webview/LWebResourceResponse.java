package com.digo.platform.webview;

import java.io.InputStream;
import java.util.Map;

public abstract class LWebResourceResponse {

    public abstract void setMimeType(String var1);

    public abstract String getMimeType();

    public abstract void setEncoding(String var1);

    public abstract String getEncoding();

    public abstract void setStatusCodeAndReasonPhrase(int var1, String var2);

    public abstract int getStatusCode();

    public abstract String getReasonPhrase();

    public abstract void setResponseHeaders(Map<String, String> var1);

    public abstract Map<String, String> getResponseHeaders();

    public abstract void setData(InputStream var1);

    public abstract InputStream getData();

    @Override
    public String toString() {
        return "WebResourceResponse " +
                ", Encoding: " + getEncoding() +
                ", MimeType" + getMimeType() +
                ", ReasonPhrase" + getReasonPhrase() +
                ", StatusCode:" + getStatusCode();

    }
}
