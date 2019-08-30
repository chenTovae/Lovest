package com.digo.platform.logan.mine.upload;

import com.digo.platform.logan.mine.Logz;
import com.digo.platform.logan.mine.upload.task.base.interfaces.IUTask;

/**
 * Author : Create by Linxinyuan on 2018/10/23
 * Email : linxinyuan@lizhi.fm
 * Desc : 日志上传控制中心
 */
public class LogSendProxy {
    private RealSendRunnable mRealSendLogRunable;
    private static volatile LogSendProxy mInstance;

    private LogSendProxy() {
        mRealSendLogRunable = new RealSendRunnable();
    }

    public static LogSendProxy getInstance() {
        if (mInstance == null) {
            synchronized (LogSendProxy.class) {
                if (mInstance == null) {
                    mInstance = new LogSendProxy();
                }
            }
        }
        return mInstance;
    }

    public void runTask(IUTask task) {
        //TODO 只有主进程能进行上传,避免并发读写数据库问题
        if (LogSendHelper.checkDueUploadBefore() && Logz.getLogConfiger().getIsMainProcess()
                && Logz.getContext() != null && mRealSendLogRunable != null && task != null) {
            task.runTask(Logz.getContext(), mRealSendLogRunable);
        }
    }
}