package com.digo.platform.webview;

public abstract class LWebResourceError {

    public abstract int getErrorCode();

    public abstract CharSequence getDescription();

    @Override
    public String toString() {
        return "WebResourceError ErrorCode:" + getErrorCode() + ", Description:" + getDescription();
    }
}
