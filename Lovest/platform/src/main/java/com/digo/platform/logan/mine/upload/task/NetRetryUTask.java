package com.digo.platform.logan.mine.upload.task;

import android.content.Context;

import com.digo.platform.logan.mine.Logz;
import com.digo.platform.logan.mine.upload.RealSendRunnable;
import com.digo.platform.logan.mine.upload.task.base.BaseRetryWrapper;
import com.digo.platform.logan.mine.upload.task.base.interfaces.IUTask;

/**
 * 网络中断情况下的文件重传
 * 备注：文件路径来自于内存缓存阻塞队列
 * <p>
 * needReUpload 重传文件路径数组
 */
public class NetRetryUTask extends BaseRetryWrapper implements IUTask {
    private String[] mNeedReUpload;

    public NetRetryUTask() {
        //Class Default Constructor
    }

    public void setNeedReUpload(String[] needReUpload) {
        this.mNeedReUpload = needReUpload;
    }

    public static final class Builder {
        private String[] needReUpload;

        public Builder setNeedReUpload(String[] needReUpload) {
            this.needReUpload = needReUpload;
            return this;
        }

        public NetRetryUTask build() {
            NetRetryUTask task = new NetRetryUTask();
            task.setNeedReUpload(needReUpload);
            return task;
        }
    }

    @Override
    public void runTask(final Context context, final RealSendRunnable runnable) {
        if (mNeedReUpload.length > 0) {
            try {
                //TODO 根据Cache队列获取到列表，可能存在数据库存在但是文件不存在的情况
                startRetry(mNeedReUpload, runnable);
            } catch (Exception e) {
                Logz.e(e.toString());
            }
        }
    }
}
