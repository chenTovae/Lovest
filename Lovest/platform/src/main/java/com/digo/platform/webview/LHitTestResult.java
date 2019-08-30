package com.digo.platform.webview;

/**
 * Created by kaient on 2017/1/9.
 */

public abstract class LHitTestResult {

    public int getType() {
        return getUNKNOWNTYPE();
    }

    public String getExtra() {
        return "";
    }

    public abstract int getUNKNOWNTYPE();
}
