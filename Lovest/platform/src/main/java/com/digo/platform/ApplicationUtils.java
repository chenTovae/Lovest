package com.digo.platform;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by bujun on 2017/2/27.
 */

public class ApplicationUtils {
    public static boolean IS_DEBUG = true;
    public static Thread mMainThread = Looper.getMainLooper().getThread();
    public static Handler mMainHandler = new Handler(Looper.getMainLooper());
}
