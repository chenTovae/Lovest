/*
 * Copyright (c) 2018-present, 美团点评
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.digo.platform.logan.meituan;

import com.digo.platform.logan.meituan.config.LoganConfig;
import com.digo.platform.logan.meituan.config.LoganControlCenter;
import com.digo.platform.logan.meituan.protocol.OnLoganProtocolStatus;
import com.digo.platform.logan.meituan.route.IFileArrangeCallback;
import com.digo.platform.logan.meituan.route.IFileModifyCallback;
import com.digo.platform.logan.meituan.route.IFileReOpenCallback;
import com.digo.platform.logan.meituan.send.SendLogRunnable;

public class Logan {
    public static OnLoganProtocolStatus sLoganProtocolStatus;
    public static IFileModifyCallback sFileModifyCallback;
    public static LoganControlCenter sLoganControlCenter;
    public static boolean sDebug = false;

    /**
     * @brief Logan初始化
     */
    public static void init(LoganConfig loganConfig) {
        sLoganControlCenter = LoganControlCenter.instance(loganConfig);
    }

    /**
     * @param log  表示日志内容
     * @param type 表示日志类型
     * @brief Logan写入日志
     */
    public static void w(String log, int type) {
        try {
            sLoganControlCenter.write(log, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 强制分片
     *
     * @param callback 回调是上传文件的前提
     */
    public static void r(IFileReOpenCallback callback) {
        try {
            sLoganControlCenter.reOpen(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件整理与清除
     *
     * @param callback
     */
    public static void a(IFileArrangeCallback callback) {
        try {
            sLoganControlCenter.arrange(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief 立即写入日志文件
     */
    public static void f() {
        try {
            sLoganControlCenter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param paths    文件路径数组
     * @param runnable 发送操作
     * @param tag      是否含有sdk-tag
     * @brief 发送日志
     */
    public static void s(String tag, String paths[], SendLogRunnable runnable) {
        if (null != paths || paths.length > 0) {
            sLoganControlCenter.send(tag, paths, runnable);
        }
    }

    /**
     * @brief Logan Debug开关
     */
    public static void setDebug(boolean debug) {
        Logan.sDebug = debug;
    }

    public static void onListenerLogWriteStatus(String name, int status) {
        if (sLoganProtocolStatus != null) {
            sLoganProtocolStatus.loganProtocolStatus(name, status);
        }
    }

    public static void setOnLoganProtocolStatus(OnLoganProtocolStatus listener) {
        sLoganProtocolStatus = listener;
    }

    public static void onSyncFileDeleteCall(String fn, String path) {
        if (sFileModifyCallback != null) {
            sFileModifyCallback.onSyncFileDelete(fn, path);
        }
    }

    public static void onSyncFileDeleteOnlyPathCall(String path) {
        if (sFileModifyCallback != null) {
            sFileModifyCallback.onSyncFileDeleteOnlyPath(path);
        }
    }

    public static int onQueryFileRetryTime(String name, String path) {
        if (sFileModifyCallback != null) {
            return sFileModifyCallback.onQueryFileRetryTime(name, path);
        }
        return -1;
    }

    public static void setFileModifyCallback(IFileModifyCallback listener) {
        sFileModifyCallback = listener;
    }
}
