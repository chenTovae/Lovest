package com.digo.platform.webview;

/**
 * Created by kaient on 2017/1/9.
 */

public class LConsoleMessage {

    public int lineNumber() {
        return 0;
    }

    public String message() {
        return "";
    }

    @Override
    public String toString() {
        return "lineNumber:" + lineNumber() + ", message:" + message();
    }
}
