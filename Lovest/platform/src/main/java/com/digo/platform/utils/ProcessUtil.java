package com.digo.platform.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ProcessUtil {

    private ProcessUtil() {}

    private static String sProcessName;
    private static AtomicBoolean mainProcessChecked = new AtomicBoolean(false);
    private static boolean isInMainProcess = false;

    /**
     * @param context The context used to get process name.
     * @return Name of current process.
     */
    public static String getCurrProcessName(Context context) {
        if (!TextUtils.isEmpty(sProcessName)) {
            return sProcessName;
        }

        sProcessName = getCurrentProcessNameViaLinuxFile();
        if (!TextUtils.isEmpty(sProcessName)) {
            return sProcessName;
        }

        if (context != null) {
            sProcessName = getCurrentProcessNameViaActivityManager(context);
            return sProcessName;
        }

        return null;
    }

    /**
     * Check if current process is main process.
     *
     * @param context The context used check if main process.
     * @return True if current process is main process, false otherwise.
     */
    public static boolean isInMainProcess(Context context) {
        if (mainProcessChecked.get()) {
            return isInMainProcess;
        }
        String currentProcessName = getCurrProcessName(context);
        if (TextUtils.isEmpty(currentProcessName)) {
            return false;
        }
        String mainProcessName = context.getPackageName();
        if (TextUtils.isEmpty(mainProcessName)) {
            return false;
        }
        if (!mainProcessChecked.getAndSet(true)) {
            isInMainProcess = TextUtils.equals(currentProcessName, mainProcessName);
        }
        return isInMainProcess;
    }

    /***
     * @param context
     * @param processName
     * @return 进程名是否和当前进程名一致，符合返回{@code true}，否则返回{@code false}。
     */
    public static boolean isMatchProcess(Context context, String processName) {
        String currentProcessName = getCurrProcessName(context);
        return TextUtils.equals(processName, currentProcessName);
    }

    private static String getCurrentProcessNameViaLinuxFile() {
        int pid = android.os.Process.myPid();
        String line = "/proc/" + pid + "/cmdline";
        FileInputStream fis = null;
        String processName = null;
        byte[] bytes = new byte[1024];
        int read = 0;

        try {
            fis = new FileInputStream(line);
            read = fis.read(bytes);
        } catch (Exception e) {
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
            }
        }

        if (read > 0) {
            processName = new String(bytes, 0, read);
            processName = processName.trim();
        }

        return processName;
    }

    private static String getCurrentProcessNameViaActivityManager(Context context) {
        if (context == null) {
            return null;
        }
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (mActivityManager == null) {
            return null;
        }
        List<ActivityManager.RunningAppProcessInfo> processes = mActivityManager.getRunningAppProcesses();
        if (processes == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : processes) {
            if (appProcess != null && appProcess.pid == pid) {
                return appProcess.processName;
            }
        }

        return null;
    }
}
