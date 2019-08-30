package com.digo.platform.logan.mine.upload.task;

import android.content.Context;

import com.digo.platform.logan.meituan.Logan;
import com.digo.platform.logan.meituan.route.IFileArrangeCallback;
import com.digo.platform.logan.meituan.route.IFileReOpenCallback;
import com.digo.platform.logan.mine.Logz;
import com.digo.platform.logan.mine.database.daos.LoganUFileDao;
import com.digo.platform.logan.mine.upload.LogSendHelper;
import com.digo.platform.logan.mine.upload.RealSendRunnable;
import com.digo.platform.logan.mine.upload.task.base.interfaces.IUTask;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static com.digo.platform.logan.mine.common.LogzConstant.DEFALUT_ULOG_TAG;

/**
 * 用户反馈上传任务
 * <p>
 * mode  允许在哪些网络制式上传 0x01 Wifi 0x10 4G
 * force 是否强制上传，默认false不强制，true为强制
 * carry 是否携带第三方Sdk日志
 */
public class FeedBackUTask implements IUTask {
    private int mode;//网络模式
    private boolean force; //是否强制上传
    private long curTimeStamp; //上传触发点时间戳
    private Disposable mDisposableMine;//rx-delay

    public FeedBackUTask() {
        //Class Default Constructor
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public void setCurTimeStamp(long curTimeStamp) {
        this.curTimeStamp = curTimeStamp;
    }

    public static final class Builder {
        private int mode;//网络模式
        private boolean force; //是否强制上传
        private long curTimeStamp; //上传触发点时间戳

        public Builder setMode(int mode) {
            this.mode = mode;
            return this;
        }

        public Builder setForce(boolean force) {
            this.force = force;
            return this;
        }

        public Builder setCurTimeStamp(long curTimeStamp) {
            this.curTimeStamp = curTimeStamp;
            return this;
        }

        public FeedBackUTask build() {
            FeedBackUTask task = new FeedBackUTask();
            task.setCurTimeStamp(curTimeStamp);
            task.setForce(force);
            task.setMode(mode);
            return task;
        }
    }

    @Override
    public void runTask(final Context context, final RealSendRunnable runnable) {
        //由于底层的多进程flush没有完成回调,所以执行延时任务1000毫秒之后进行主线程的flush并进行切片上传
        Observable.timer(1500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mDisposableMine = disposable;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Logz.arrange(new IFileArrangeCallback() {
                            @Override
                            public void onArrangeFile() {
                                realSendLoganFileMine(context, runnable);//4G && !force
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        cancelMine();
                    }

                    @Override
                    public void onComplete() {
                        cancelMine();
                    }
                });
    }

    private void realSendLoganFileMine(final Context context, final RealSendRunnable runnable) {
        //进行当前文件的移动
        Logan.r(new IFileReOpenCallback() {
            @Override
            public void onReOpenFile() {
                try {
                    //TODO 根据实体文件获取到列表，不存在数据库存在但是文件不存在的情况
                    String[] arrPath = LogSendHelper.collectFileNeedUpload(curTimeStamp);
                    if (null != arrPath && arrPath.length > 0) {
                        //上传数据条目入库,由于unique限制相同文件名与路径的条目不会被重复入库
                        LoganUFileDao.getInstance(context).insertNew(arrPath);
                        //force==0过滤条件：上传失败3次的不传，上传过的不传
                        //force==1过滤条件：上传失败3次的不传，上传过的再次上传
                        String[] filterPath = LoganUFileDao.getInstance(context).queryUploadFilter(arrPath, force);
                        //APPCONFIG判断是否可以上传
                        if (Logz.getLogConfiger().getAppConfigUpload() && runnable != null
                                && filterPath != null && filterPath.length > 0) {
                            Logan.s(DEFALUT_ULOG_TAG, filterPath, runnable);//开始批量日志文件上传任务
                        }
                    }
                } catch (Exception e) {
                    Logz.e(e.toString());
                }
            }
        });
    }

    private void cancelMine() {
        if (mDisposableMine != null && !mDisposableMine.isDisposed()) {
            mDisposableMine.dispose();
        }
    }
}
