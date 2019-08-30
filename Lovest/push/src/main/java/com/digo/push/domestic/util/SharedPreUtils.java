package com.digo.push.domestic.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.digo.platform.ApplicationContext;

public class SharedPreUtils {
    //vivo push token (由于vivo的推送token只在首次注册回调，所以需要保存起来)
    private static final String VIVO_TOKEN = "vivo_token";
    private static final String FINAL_TYPE = "final_type";
    private static final String FINAL_TOKEN = "final_token";
    private static final String FINAL_ACT = "final_act";
    private static final String FINAL_MODEL = "final_model";
    private static final String FINAL_TIME = "final_time";

    private static SharedPreferences getSharedPreferences() {
        return ApplicationContext.getContext().getSharedPreferences(ApplicationContext.getPreferencesName() + "_push", Context.MODE_PRIVATE);
    }

    public static void setVivoToken(String token) {
        getSharedPreferences().edit().putString(VIVO_TOKEN, token).apply();
    }

    public static String getVivoToken() {
        return getSharedPreferences().getString(VIVO_TOKEN, "");
    }

    public static void setFinalType(int finalType) {
        getSharedPreferences().edit().putInt(FINAL_TYPE, finalType).apply();
    }

    public static int getFinalType() {
        return getSharedPreferences().getInt(FINAL_TYPE, -1);
    }

    public static void setFinalToken(String finalToken) {
        getSharedPreferences().edit().putString(FINAL_TOKEN, finalToken).apply();
    }

    public static String getFinalToken() {
        return getSharedPreferences().getString(FINAL_TOKEN, "");
    }

    public static void setFinalModel(String finalMode) {
        getSharedPreferences().edit().putString(FINAL_MODEL, finalMode).apply();
    }

    public static String getFinalModel() {
        return getSharedPreferences().getString(FINAL_MODEL, "");
    }

    public static void setFinalAct(int finalAct) {
        getSharedPreferences().edit().putInt(FINAL_ACT, finalAct).apply();
    }

    public static int getFinalAct() {
        return getSharedPreferences().getInt(FINAL_ACT, -1);
    }

    public static void setFinalTime(long timeStamp) {
        getSharedPreferences().edit().putLong(FINAL_TIME, timeStamp).apply();
    }

    public static Long getFinalTime() {
        return getSharedPreferences().getLong(FINAL_TIME, 0L);
    }
}
