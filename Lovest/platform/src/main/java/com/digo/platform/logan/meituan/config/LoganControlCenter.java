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

package com.digo.platform.logan.meituan.config;

import android.os.Looper;
import android.text.TextUtils;

import com.digo.platform.logan.meituan.Logan;
import com.digo.platform.logan.meituan.LoganThread;
import com.digo.platform.logan.meituan.action.ArrangeAction;
import com.digo.platform.logan.meituan.action.LoganModel;
import com.digo.platform.logan.meituan.action.ReOpenAction;
import com.digo.platform.logan.meituan.action.SendLogAction;
import com.digo.platform.logan.meituan.action.WriteLogAction;
import com.digo.platform.logan.meituan.route.IFileArrangeCallback;
import com.digo.platform.logan.meituan.route.IFileReOpenCallback;
import com.digo.platform.logan.meituan.send.SendLogRunnable;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LoganControlCenter {
    private static LoganControlCenter sLoganControlCenter;

    private ConcurrentLinkedQueue<LoganModel> mCacheLogQueue = new ConcurrentLinkedQueue<>();
    private String mCachePath; // 缓存文件路径
    private String mPath; //文件路径
    private long mSaveTime; //存储时间
    private long mMaxLogFile;//最大文件大小
    private long mMinSDCard;
    private long mMaxQueue; //最大队列数
    private String mEncryptKey16;
    private String mEncryptIv16;
    private long mSimpleSize;//单个分片的最大限制
    private int mDepotFileNum;//单天仓库的日志文件数量
    private boolean mIsMainProcess;//是否是主进程
    private LoganThread mLoganThread;

    private LoganControlCenter(LoganConfig config) {
        if (!config.isValid()) {
            throw new NullPointerException("config's param is invalid");
        }

        mPath = config.mPathPath;
        mCachePath = config.mCachePath;
        mSaveTime = config.mDay;
        mMinSDCard = config.mMinSDCard;
        mMaxLogFile = config.mMaxFile;
        mMaxQueue = config.mMaxQueue;
        mSimpleSize = config.mSimpleSize;
        mDepotFileNum = config.mDepotFileNum;
        mEncryptKey16 = new String(config.mEncryptKey16);
        mEncryptIv16 = new String(config.mEncryptIv16);
        mIsMainProcess = config.isMainProcess;

        init();
    }

    private void init() {
        if (mLoganThread == null) {
            mLoganThread = new LoganThread(mCacheLogQueue, mCachePath, mPath, mSaveTime,
                    mMaxLogFile, mMinSDCard, mSimpleSize, mDepotFileNum, mEncryptKey16, mEncryptIv16, mIsMainProcess);
            mLoganThread.start();
        }
    }

    public static LoganControlCenter instance(LoganConfig config) {
        if (sLoganControlCenter == null) {
            synchronized (LoganControlCenter.class) {
                if (sLoganControlCenter == null) {
                    sLoganControlCenter = new LoganControlCenter(config);
                }
            }
        }
        return sLoganControlCenter;
    }

    public void write(String log, int flag) {
        if (TextUtils.isEmpty(log)) {
            return;
        }
        LoganModel model = new LoganModel();
        model.action = LoganModel.Action.WRITE;
        WriteLogAction action = new WriteLogAction();
        String threadName = Thread.currentThread().getName();
        long threadLog = Thread.currentThread().getId();
        boolean isMain = false;
        if (Looper.getMainLooper() == Looper.myLooper()) {
            isMain = true;
        }
        action.log = log;
        action.localTime = System.currentTimeMillis();
        action.flag = flag;
        action.isMainThread = isMain;
        action.threadId = threadLog;
        action.threadName = threadName;
        model.writeLogAction = action;
        if (mCacheLogQueue.size() < mMaxQueue) {
            mCacheLogQueue.add(model);
            if (mLoganThread != null) {
                mLoganThread.notifyRun();
            }
        }
    }

    public void send(String tag, String paths[], SendLogRunnable runnable) {
        if (TextUtils.isEmpty(mPath) || paths == null || paths.length == 0) {
            return;
        }
        for (String path : paths) {
            if (TextUtils.isEmpty(path)) {
                continue;
            }
            File waitUpload = new File(mPath);
            if (!waitUpload.exists()) {
                //数据库存在该任务但是真实文件已经不存在了
                Logan.onSyncFileDeleteOnlyPathCall(mPath);
                continue;
            }
            LoganModel model = new LoganModel();
            SendLogAction action = new SendLogAction();
            model.action = LoganModel.Action.SEND;
            action.tag = tag;
            action.uploadPath = path;
            action.sendLogRunnable = runnable;
            model.sendLogAction = action;
            mCacheLogQueue.add(model);
            if (mLoganThread != null) {
                mLoganThread.notifyRun();
            }
        }
    }

    public void reOpen(IFileReOpenCallback callback) {
        LoganModel model = new LoganModel();
        ReOpenAction action = new ReOpenAction();
        model.action = LoganModel.Action.REOPEN;
        action.callback = callback;
        model.reOpenAction = action;
        mCacheLogQueue.add(model);
        if (mLoganThread != null) {
            mLoganThread.notifyRun();
        }
    }

    public void arrange(IFileArrangeCallback callback) {
        LoganModel model = new LoganModel();
        ArrangeAction action = new ArrangeAction();
        model.action = LoganModel.Action.ARRANGE;
        action.callback = callback;
        model.arrangeAction = action;
        mCacheLogQueue.add(model);
        if (mLoganThread != null) {
            mLoganThread.notifyRun();
        }
    }

    public void flush() {
        if (TextUtils.isEmpty(mPath)) {
            return;
        }
        LoganModel model = new LoganModel();
        model.action = LoganModel.Action.FLUSH;
        mCacheLogQueue.add(model);
        if (mLoganThread != null) {
            mLoganThread.notifyRun();
        }
    }
}
