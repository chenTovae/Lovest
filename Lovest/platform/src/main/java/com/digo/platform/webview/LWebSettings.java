package com.digo.platform.webview;

public abstract class LWebSettings {

    public abstract String getUserAgentString();

    public abstract void setUserAgentString(String ua);

    public abstract void setCacheMode(int mode);

    public abstract void setJavaScriptCanOpenWindowsAutomatically(boolean automatically);

    public abstract void setLoadWithOverviewMode(boolean automatically);

    public abstract void setJavaScriptEnabled(boolean enabled);

    public abstract void setDisplayZoomControls(boolean enabled);

    public abstract void setLoadsImagesAutomatically(boolean automatically);

    public abstract void setBlockNetworkImage(boolean block);

    public abstract void setSupportZoom(boolean supportZoom);

    public abstract void setMixedContentMode(int mode);

    public abstract void setBuiltInZoomControls(boolean zoomControls);

    public abstract void setUseWideViewPort(boolean useWideViewPort);

    public abstract void setDomStorageEnabled(boolean domStorageEnabled);

    public abstract void setTextSize(int textSize);

    public abstract void setDatabaseEnabled(boolean databaseEnabled);

    public abstract void setMediaPlaybackRequiresUserGesture(boolean requiresUserGesture);

    public abstract void setLayoutAlgorithm(LayoutAlgorithm layoutAlgorithm);

    public static enum LayoutAlgorithm {
        NORMAL,
        SINGLE_COLUMN,
        NARROW_COLUMNS
    }

    public static final class TextSize {
        public static final int SMALLEST = 50;
        public static final int SMALLER = 75;
        public static final int NORMAL = 100;
        public static final int LARGER = 120;
        public static final int LARGEST = 150;
    }
}
