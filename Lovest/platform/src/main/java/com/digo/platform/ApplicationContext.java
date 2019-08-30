package com.digo.platform;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.digo.platform.utils.ProcessUtil;

/**
 * 应用程序上下文.
 *
 * @author squll
 */
public class ApplicationContext {

    private static Context context;

    private static Application mApplication;

    private static String packageName = "com.yibasan.lizhifm";

    private static String TAG = ApplicationContext.class.getSimpleName();

    private static long appSTime;
    /**
     * 初始化方法.
     *
     * @param con Context
     */
    public static void init(Context con) {
        context = con;
        packageName = con.getPackageName();
        Log.i(TAG, "setup application context for package : " + packageName);
        long currentTimeMillis = System.currentTimeMillis();
        Log.i(TAG, "application start time : " + currentTimeMillis);
    }

    public static void setApplication(Application application){
        mApplication = application;
    }

    public static Context getContext() {
        return context;
    }

    public static Application getApplication(){
        return mApplication;
    }

    public static String getPackageName() {
        return packageName;
    }

    public static String getPreferencesName() {
        return packageName + "_preferences";
    }

    /**
     * 根据mode获取SharedPreferences.
     *
     * @param mode MODE_PRIVATE
     * @return SharedPreferences
     */
    public static SharedPreferences getSharedPreferences(int mode) {
        return null;//TODO Use MMKV SharePreference
    }

    /**
     * 获取当前进程名.
     *
     * @return String
     */
    public static String getCurProcessName() {
        if (context == null) {
            return null;
        }
        try {
            return ProcessUtil.getCurrProcessName(context);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isInMainProcess() {
        if (context == null) {
            return false;
        }
        try {
            return ProcessUtil.isInMainProcess(context);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isMatchProcess(String processName){
        if (context == null) {
            return false;
        }
        try {
            return ProcessUtil.isMatchProcess(context, processName);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取进程启动时间
     * @return
     */
    public static long getAppSTime() {
        if (appSTime == 0){
            appSTime = System.currentTimeMillis();
        }
        return appSTime;
    }
}
