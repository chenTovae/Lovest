package com.digo.platform.webview;

import android.net.http.SslCertificate;

/**
 * Created by kaient on 2017/1/9.
 */

public abstract class LSslError {

    public abstract SslCertificate getCertificate();

    public abstract boolean addError(int var1);

    public abstract boolean hasError(int var1);

    public abstract int getPrimaryError();

    public abstract String getUrl();

    public String toString() {
        return "primary error: " + getPrimaryError() +
                " certificate: " + getCertificate() +
                " on URL: " + getUrl();
    }
}
