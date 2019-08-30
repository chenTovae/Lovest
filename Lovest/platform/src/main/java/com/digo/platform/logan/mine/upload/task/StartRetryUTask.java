package com.digo.platform.logan.mine.upload.task;

import android.content.Context;

import com.digo.platform.logan.mine.Logz;
import com.digo.platform.logan.mine.database.daos.LoganUFileDao;
import com.digo.platform.logan.mine.upload.RealSendRunnable;
import com.digo.platform.logan.mine.upload.task.base.BaseRetryWrapper;
import com.digo.platform.logan.mine.upload.task.base.interfaces.IUTask;
import com.digo.platform.logan.meituan.route.IFileArrangeCallback;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * 应用启动触发的文件重传任务
 * 备注：文件路径来自于数据库
 */
public class StartRetryUTask extends BaseRetryWrapper implements IUTask {
    private Disposable mDisposableRetry;//rx-delay

    public StartRetryUTask() {
        //Class Default Constructor
    }

    public static final class Builder {
        public StartRetryUTask build() {
            return new StartRetryUTask();
        }
    }

    @Override
    public void runTask(final Context context, final RealSendRunnable runnable) {
        Observable.timer(1500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mDisposableRetry = disposable;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Logz.arrange(new IFileArrangeCallback() {
                            @Override
                            public void onArrangeFile() {
                                realStartUpReSendLoganFile(context, runnable);
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        cancel();
                    }

                    @Override
                    public void onComplete() {
                        cancel();
                    }
                });
    }

    private void realStartUpReSendLoganFile(Context context, RealSendRunnable runnable) {
        try {
            //将数据库中正在进行中的任务状态重置
            LoganUFileDao.getInstance(Logz.getContext()).refreshIllegalStatus();
            //数据库检索出满足上传条件的文件路径数组
            //TODO 根据数据库记录获取到列表，可能存在数据库存在但是文件不存在的情况
            String[] needReUpload = LoganUFileDao.getInstance(context).queryNeedRetry();
            if (needReUpload != null && needReUpload.length > 0) {
                startRetry(needReUpload, runnable);
            }
        } catch (Exception e) {
            Logz.e(e.toString());
        }
    }

    private void cancel() {
        if (mDisposableRetry != null && !mDisposableRetry.isDisposed()) {
            mDisposableRetry.dispose();
        }
    }
}
