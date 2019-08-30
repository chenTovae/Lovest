package com.digo.platform.logan.mine.utils;

import android.util.Log;

/**
 * Author : Create by Linxinyuan on 2018/10/24
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev (日志等级String -> int 映射)
 */
public class ConverLevelUtils {
    public static int StringLevel2Int(String level) {
        int l = Log.INFO;
        if ("V".equals(level) || "v".equals(level)) {
            l = Log.VERBOSE;
        } else if ("D".equals(level) || "d".equals(level)) {
            l = Log.DEBUG;
        } else if ("I".equals(level) || "i".equals(level)) {
            l = Log.INFO;
        } else if ("W".equals(level) || "w".equals(level)) {
            l = Log.WARN;
        } else if ("E".equals(level) || "e".equals(level)) {
            l = Log.ERROR;
        }
        return l;
    }

    public static String IntLevel2String(int level) {
        String l = "I";
        if (level == Log.VERBOSE) {
            l = "V";
        } else if (level == Log.DEBUG) {
            l = "D";
        } else if (level == Log.INFO) {
            l = "I";
        } else if (level == Log.WARN) {
            l = "W";
        } else if (level == Log.ERROR) {
            l = "E";
        }
        return l;
    }
}
