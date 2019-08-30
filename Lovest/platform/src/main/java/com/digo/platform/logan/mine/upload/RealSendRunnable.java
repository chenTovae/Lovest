package com.digo.platform.logan.mine.upload;

import com.digo.platform.logan.meituan.send.SendLogRunnable;
import com.digo.platform.logan.mine.Logz;
import com.digo.platform.logan.mine.database.daos.LoganUFileDao;
import com.digo.platform.logan.mine.upload.bean.HttpPostFileModel;
import com.digo.platform.logan.mine.upload.http.OnPostFileHttpRequest;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.digo.platform.logan.mine.common.LogzConstant.LOGAN_TAG;

/**
 * Author : Create by Linxinyuan on 2018/10/19
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */
public class RealSendRunnable extends SendLogRunnable implements OnPostFileHttpRequest {
    public static int STATUS_UPLOAD_INITIAL = 0;//初始状态
    public static int STATUS_UPLOAD_COMPLETE = 1;//上传完成
    public static int STATUS_UPLOAD_DOING = 2;//正在上传中

    private Disposable mDisposable;//rx-delay-disposable
    private static int RETRY_TIME = 3;//设置连接与读写超时时间

    public static Set<String> CACHE_SET = new HashSet<String>();//网络监听缓存策略-网络中断则添加到该集合，网络恢复重新进行上传任务

    @Override
    public void sendLog(String tag, File file, int retry) {
        String baseRequestUrl = "";
        baseRequestUrl = HttpPostUrlBuilder.buildLizhiLogUploadUrl(file, tag);
        if (!baseRequestUrl.equals("")) {
            //开始进行文件上传操作
            HttpPostFileModel httpPostFileModel = new HttpPostFileModel.Builder()
                    .setTag(tag)//设置本次上传的日志Tag(LizhiTag/SdkTag)
                    .setLogFile(file)//设置本次上传的日志文件(单个)
                    .setHttpUrl(baseRequestUrl)//设置日志文件上传Url
                    .setRetry(retry)//设置本任务的内存中的重试次数
                    .build();
            //PriorityManager.addQueue(httpPostFileModel, this);
            doSendFileByAction(httpPostFileModel);
        }
    }

    public void doSendFileByAction(HttpPostFileModel httpPostFileModel) {
        if (!LogSendHelper.checkNetworkAvailable()) {
            try {
                if (!CACHE_SET.contains(httpPostFileModel.getLogFile().getAbsolutePath())) {
                    CACHE_SET.add(httpPostFileModel.getLogFile().getAbsolutePath());
                    Logz.tag(LOGAN_TAG).e("网络不可用，文件：%s >> 路径：%s >> 加入到缓存上传缓存集合成功!",
                            httpPostFileModel.getLogFile().getName(), httpPostFileModel.getLogFile().getAbsolutePath());
                }
            } catch (Exception e) {
                Logz.tag(LOGAN_TAG).e("网络不可用，文件：%s >> 路径：%s >> 加入到缓存上传缓存集合失败，放弃该任务!",
                        httpPostFileModel.getLogFile().getName(), httpPostFileModel.getLogFile().getAbsolutePath());
            }
            return;//本次任务中止返回网络不可用
        }
        try {
            File logFile = httpPostFileModel.getLogFile();
            if (LoganUFileDao.getInstance(Logz.getContext())
                    .queryUploadStatus(logFile.getName(), logFile.getAbsolutePath()) != STATUS_UPLOAD_DOING) {
                Logz.tag(LOGAN_TAG).i("网络连接正常，本次请求上传的Url: %s", httpPostFileModel.getHttpUrl());
                Logz.tag(LOGAN_TAG).i("网络连接正常，本次请求上传的FileName=%s >> FilePath=%s",
                        httpPostFileModel.getLogFile().getName(), httpPostFileModel.getLogFile().getAbsolutePath());
                //不是上传中的状态则开始上传并把这个单文件上传任务的标志位改成STATUS_UPLOAD_DOING
                LoganUFileDao.getInstance(Logz.getContext())
                        .updateStatus(logFile.getName(), logFile.getAbsolutePath(), STATUS_UPLOAD_DOING);
                //添加上传开始回调[包含用户上传日志文件的绝对路径+url]
                if (Logz.getGrobalUploadListener() != null) {
                    Logz.getGrobalUploadListener().onLogUploadStart(httpPostFileModel.getHttpUrl(), httpPostFileModel.getLogFile().getAbsolutePath());
                }
                //开始HTTP上传任务
                HttpPostRunnable.getInstance().doPostFileRequest(httpPostFileModel, this);
            } else {
                Logz.tag(LOGAN_TAG).i("网络连接正常，任务文件：%s, 已经在队列中了, 无需再次上传, 任务结束!", logFile.getName());
                return;
            }
        } catch (NullPointerException e) {
            Logz.tag(LOGAN_TAG).e(e.toString());
        }
    }

    /**
     * 上传成功回调接口
     */
    @Override
    public void onPostSuccess(HttpPostFileModel httpPostFileModel) {
        File logFile = httpPostFileModel.getLogFile();
        Logz.tag(LOGAN_TAG).i("日志文件：%s-上传成功 >> FilePath=%s", logFile.getName(), logFile.getAbsolutePath());
        //添加上传成功回调接口[包含用户上传日志文件的绝对路径+url]
        if (Logz.getGrobalUploadListener() != null) {
            Logz.getGrobalUploadListener().onLogUploadSuccess(httpPostFileModel.getHttpUrl(), httpPostFileModel.getLogFile().getAbsolutePath());
        }
        LoganUFileDao.getInstance(Logz.getContext()).updateStatus(logFile.getName(), logFile.getAbsolutePath(), STATUS_UPLOAD_COMPLETE);
    }

    /**
     * 上传失败回调接口
     */
    @Override
    public void onPostFail(final HttpPostFileModel httpPostFileModel, String exception) {
        File logFile = httpPostFileModel.getLogFile();
        Logz.tag(LOGAN_TAG).e("日志文件：%s-上传失败 >> FilePath=%s", logFile.getName(), logFile.getAbsolutePath());
        //添加上传失败回调接口[包含用户上传日志文件的绝对路径+url]
        if (Logz.getGrobalUploadListener() != null) {
            Logz.getGrobalUploadListener().onLogUploadFailure(httpPostFileModel.getHttpUrl(), httpPostFileModel.getLogFile().getAbsolutePath(), exception);
        }
        //恢复上传标志位为初始化状态0,避免下次重传因为标志位是正在进行中而无法上传
        LoganUFileDao.getInstance(Logz.getContext()).updateStatus(logFile.getName(), logFile.getAbsolutePath(), STATUS_UPLOAD_INITIAL);
        if (httpPostFileModel.getRetry() < RETRY_TIME) {
            //延时任务4/8/12
            Observable.timer(httpPostFileModel.getRetry() * 4, TimeUnit.SECONDS)
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(Disposable disposable) {
                            mDisposable = disposable;
                        }

                        @Override
                        public void onNext(Long aLong) {
                            sendLog(httpPostFileModel.getTag(), httpPostFileModel.getLogFile(), httpPostFileModel.getRetry() + 1);
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mDisposable != null && !mDisposable.isDisposed()) {
                                mDisposable.dispose();
                            }
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } else {
            //重试次数Retry+1，数据库标识本次批量上传任务文件上传状态
            LoganUFileDao.getInstance(Logz.getContext()).updateRetry(logFile.getName(), logFile.getAbsolutePath());
        }
    }
}
