package com.digo.platform.logan.mine.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.digo.platform.logan.mine.Logz;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Author : Create by Linxinyuan on 2018/10/29
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */
public class SystemInfoUtils {
    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return 语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     *
     * @return 手机IMEI
     */
    public static String getIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm != null) {
            return tm.getDeviceId();
        }
        return null;
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        String systemVersion = "unknow";
        try {
            systemVersion = android.os.Build.VERSION.RELEASE;
        } catch (Exception e) {
            Logz.e(e.toString());
        }
        if (!TextUtils.isEmpty(systemVersion))
            systemVersion = systemVersion.replaceAll(" +", "-");
        return systemVersion;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        String phoneMode = "unknow";
        try {
            phoneMode = android.os.Build.MODEL;
        } catch (Exception e) {
            Logz.e(e.toString());
        }
        if (!TextUtils.isEmpty(phoneMode))
            phoneMode = phoneMode.replaceAll(" +", "-");
        return phoneMode;
    }

    /**
     * 获取应用版本号
     *
     * @return
     */
    public static String getAppVersion() {
        String appVersion = "unknow";
        try {
            appVersion = Logz.getContext().getPackageManager().getPackageInfo(Logz.getContext().getPackageName(), 0).versionName;
        } catch (NullPointerException e) {
            Logz.e(e.toString());
        } catch (PackageManager.NameNotFoundException e) {
            Logz.e(e.toString());
        }
        if (!TextUtils.isEmpty(appVersion))
            appVersion = appVersion.replaceAll(" +", "-");
        return appVersion;
    }

    /**
     * 获取当前进程名
     *
     * @param context
     * @return
     */
    public static String getProcessName(Context context) {
        //如果获取不到进程名使用随机字符串代替
        UUID uuid = UUID.randomUUID();
        String random = uuid.toString().substring(0, 8);

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List list = activityManager.getRunningAppProcesses();
        Iterator i = list.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pid) {
                    // 根据进程的信息获取当前进程的名字
                    return info.processName;
                } else {
                    return "com.yibasan.lizhifm:" + random;
                }
            } catch (Exception e) {
                return "com.yibasan.lizhifm:" + random;
            }
        }
        return "com.yibasan.lizhifm:" + random;
    }
}
