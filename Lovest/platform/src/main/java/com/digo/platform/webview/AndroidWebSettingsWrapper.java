package com.digo.platform.webview;

import android.os.Build;
import android.webkit.WebSettings;

/**
 * 原始内核实现
 * Create by shipei on 2018/12/12
 */
class AndroidWebSettingsWrapper extends LWebSettings{

    private WebSettings settings;

    AndroidWebSettingsWrapper(WebSettings settings) {
        this.settings = settings;
    }

    @Override
    public String getUserAgentString() {
        return settings.getUserAgentString();
    }

    @Override
    public void setUserAgentString(String ua) {
        settings.setUserAgentString(ua);
    }

    @Override
    public void setCacheMode(int mode) {
        settings.setCacheMode(mode);
    }

    @Override
    public void setJavaScriptCanOpenWindowsAutomatically(boolean automatically) {
        settings.setJavaScriptCanOpenWindowsAutomatically(automatically);
    }

    @Override
    public void setLoadWithOverviewMode(boolean automatically) {
        settings.setLoadWithOverviewMode(automatically);
    }

    @Override
    public void setJavaScriptEnabled(boolean enabled) {
        settings.setJavaScriptEnabled(enabled);
    }

    @Override
    public void setDisplayZoomControls(boolean enabled) {
        settings.setDisplayZoomControls(enabled);
    }

    @Override
    public void setLoadsImagesAutomatically(boolean automatically) {
        settings.setLoadsImagesAutomatically(automatically);
    }

    @Override
    public void setBlockNetworkImage(boolean block) {
        settings.setBlockNetworkImage(block);
    }

    @Override
    public void setSupportZoom(boolean supportZoom) {
        settings.setSupportZoom(supportZoom);
    }

    @Override
    public void setMixedContentMode(int mode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(mode);
        }
    }

    @Override
    public void setBuiltInZoomControls(boolean zoomControls) {
        settings.setBuiltInZoomControls(zoomControls);
    }

    @Override
    public void setUseWideViewPort(boolean useWideViewPort) {
        settings.setUseWideViewPort(useWideViewPort);
    }

    @Override
    public void setDomStorageEnabled(boolean domStorageEnabled) {
        settings.setDomStorageEnabled(domStorageEnabled);
    }

    @Override
    public void setTextSize(int textSize) {
        settings.setTextSize(getATextSize(textSize));
    }

    @Override
    public void setDatabaseEnabled(boolean databaseEnabled) {
        settings.setDatabaseEnabled(databaseEnabled);
    }

    @Override
    public void setMediaPlaybackRequiresUserGesture(boolean requiresUserGesture) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.setMediaPlaybackRequiresUserGesture(requiresUserGesture);
        }
    }

    @Override
    public void setLayoutAlgorithm(LWebSettings.LayoutAlgorithm layoutAlgorithm) {
        settings.setLayoutAlgorithm(getALayoutAlgorithm(layoutAlgorithm));
    }

    @Override
    public String toString() {
        return "mAndroidWebSettings set parma like this > "
                + "JavaScriptEnabled：" + settings.getJavaScriptEnabled() + ";"
                + "LoadsImagesAutomatically：" + settings.getLoadsImagesAutomatically() + ";"
                + "BlockNetworkImage：" + settings.getBlockNetworkImage() + ";"
                + "LayoutAlgorithm：" + settings.getLayoutAlgorithm() + ";"
                + "SupportZoom：" + settings.supportZoom() + ";"
                + "BuiltInZoomControls：" + settings.getBuiltInZoomControls() + ";"
                + "UseWideViewPort：" + settings.getUseWideViewPort() + ";"
                + "DomStorageEnabled：" + settings.getDomStorageEnabled() + ";"
                + "DatabaseEnabled：" + settings.getDatabaseEnabled() + ";"
                + "LoadWithOverviewMode：" + settings.getLoadWithOverviewMode() + ";"
                + "TextSize：" + settings.getTextSize();
    }

    private WebSettings.LayoutAlgorithm getALayoutAlgorithm(LayoutAlgorithm layoutAlgorithm) {
        switch (layoutAlgorithm) {
            case NORMAL:
                return WebSettings.LayoutAlgorithm.NORMAL;
            case SINGLE_COLUMN:
                return WebSettings.LayoutAlgorithm.SINGLE_COLUMN;
            case NARROW_COLUMNS:
                return WebSettings.LayoutAlgorithm.NARROW_COLUMNS;
            default:
                return WebSettings.LayoutAlgorithm.NORMAL;
        }
    }

    private WebSettings.TextSize getATextSize(int textSize) {
        switch (textSize) {
            case TextSize.SMALLEST:
                return WebSettings.TextSize.SMALLEST;
            case TextSize.SMALLER:
                return WebSettings.TextSize.SMALLER;
            case TextSize.NORMAL:
                return WebSettings.TextSize.NORMAL;
            case TextSize.LARGER:
                return WebSettings.TextSize.LARGER;
            case TextSize.LARGEST:
                return WebSettings.TextSize.LARGEST;
            default:
                return WebSettings.TextSize.SMALLER;
        }
    }
}
