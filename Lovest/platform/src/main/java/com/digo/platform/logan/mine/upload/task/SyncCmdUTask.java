//package com.digo.platform.logz.mine.upload.task;
//
//import android.content.Context;
//
//import com.digo.platform.logz.mine.Logz;
//import com.digo.platform.logz.mine.database.daos.LoganUFileDao;
//import com.digo.platform.logz.mine.upload.LogSendHelper;
//import com.digo.platform.logz.mine.upload.RealSendRunnable;
//import com.digo.platform.logz.mine.upload.task.base.interfaces.IUTask;
//import com.digo.platform.logz.meituan.Logan;
//import com.digo.platform.logz.meituan.action.SendLogAction;
//import com.digo.platform.logz.meituan.route.IFileArrangeCallback;
//import com.digo.platform.logz.meituan.route.IFileReOpenCallback;
//
//import java.util.concurrent.TimeUnit;
//
//import io.reactivex.Observable;
//import io.reactivex.Observer;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.Disposable;
//
//import static com.digo.platform.logz.mine.common.LogzConstant.DEFALUT_ULOG_TAG;
//
///**
// * Sync方式下的上传任务
// * <p>
// * start 开始时间-时间戳
// * end   结束时间-时间戳
// * mode  允许在哪些网络制式上传 0x01 Wifi 0x10 4G
// * force 是否强制上传，默认false不强制，true为强制
// */
//public class SyncCmdUTask implements IUTask {
//    private long start;//开始时间
//    private long end;//结束时间
//    private int mode;//网络模式
//    private boolean force;//强制上传位
//    private Disposable mDisposableSync;//rx-delay
//
//    public SyncCmdUTask() {
//        //Class Default Constructor
//    }
//
//    public void setStart(long start) {
//        this.start = start;
//    }
//
//    public void setEnd(long end) {
//        this.end = end;
//    }
//
//    public void setMode(int mode) {
//        this.mode = mode;
//    }
//
//    public void setForce(boolean force) {
//        this.force = force;
//    }
//
//    public static final class Builder {
//        private long start;//开始时间
//        private long end;//结束时间
//        private int mode;//网络模式
//        private boolean force;//强制上传位
//
//        public Builder setMode(int mode) {
//            this.mode = mode;
//            return this;
//        }
//
//        public Builder setForce(boolean force) {
//            this.force = force;
//            return this;
//        }
//
//        public Builder setStart(long start) {
//            this.start = start;
//            return this;
//        }
//
//        public Builder setEnd(long end) {
//            this.end = end;
//            return this;
//        }
//
//        public SyncCmdUTask build() {
//            SyncCmdUTask task = new SyncCmdUTask();
//            task.setStart(start);
//            task.setEnd(end);
//            task.setMode(mode);
//            task.setForce(force);
//            return task;
//        }
//    }
//
//    @Override
//    public void runTask(final Context context, final RealSendRunnable runnable) {
//        //由于底层的多进程flush没有完成回调,所以执行延时任务1000毫秒之后进行主线程的flush并进行切片上传
//        Observable.timer(1500, TimeUnit.MILLISECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Long>() {
//                    @Override
//                    public void onSubscribe(Disposable disposable) {
//                        mDisposableSync = disposable;
//                    }
//
//                    @Override
//                    public void onNext(Long aLong) {
//                        Logz.arrange(new IFileArrangeCallback() {
//                            @Override
//                            public void onArrangeFile() {
//                                realSendLoganFileSync(context, runnable);//4G && !force
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        cancelSync();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        cancelSync();
//                    }
//                });
//    }
//
//    private void realSendLoganFileSync(final Context context, final RealSendRunnable runnable) {
//        //进行当前文件的移动
//        Logan.r(new IFileReOpenCallback() {
//            @Override
//            public void onReOpenFile() {
//                try {
//                    //TODO 根据实体文件获取到列表，不存在数据库存在但是文件不存在的情况
//                    String[] arrPath = LogSendHelper.collectFileNeedUpload(start, end);
//                    if (null != arrPath && arrPath.length > 0) {
//                        //上传数据条目入库,由于unique限制相同文件名与路径的条目不会被重复入库
//                        LoganUFileDao.getInstance(context).insertNew(arrPath);
//                        //force==0过滤条件：上传失败3次的不传，上传过的不传
//                        //force==1过滤条件：上传失败3次的不传，上传过的再次上传
//                        String[] filterPath = LoganUFileDao.getInstance(context).queryUploadFilter(arrPath, force);
//                        //APPCONFIG判断是否可以上传
//                        if (Logz.getLogConfiger().getAppConfigUpload() && runnable != null
//                                && filterPath != null && filterPath.length > 0) {
//                            Logan.s(SendLogAction.TYPE_LIZHI_LOG, DEFALUT_ULOG_TAG, filterPath, runnable);//开始批量日志文件上传任务
//                        }
//                    }
//                } catch (Exception e) {
//                    Logz.e(e.toString());
//                }
//            }
//        });
//    }
//
//    private void cancelSync() {
//        if (mDisposableSync != null && !mDisposableSync.isDisposed()) {
//            mDisposableSync.dispose();
//        }
//    }
//}
