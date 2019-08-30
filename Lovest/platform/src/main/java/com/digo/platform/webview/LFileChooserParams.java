package com.digo.platform.webview;

import android.content.Intent;
import android.net.Uri;

/**
 * Author : Create by Linxinyuan on 2018/10/23
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */
public abstract class LFileChooserParams {

    public static Uri[] parseResult(int resultCode, Intent data) {
        return com.tencent.smtt.sdk.WebChromeClient.FileChooserParams.parseResult(resultCode, data);
    }

    public abstract int getMode();

    public abstract String[] getAcceptTypes();

    public abstract boolean isCaptureEnabled();

    public abstract CharSequence getTitle();

    public abstract String getFilenameHint();

    public abstract Intent createIntent();
}
