package com.digo.platform.logan.mine.upload.task.base;

import com.digo.platform.logan.meituan.Logan;
import com.digo.platform.logan.mine.Logz;
import com.digo.platform.logan.mine.upload.RealSendRunnable;
import com.digo.platform.logan.mine.utils.FileDisposeUtils;

import static com.digo.platform.logan.mine.common.LogzConstant.DEFALUT_ULOG_TAG;
import static com.digo.platform.logan.mine.common.LogzConstant.LOGAN_TAG;

/**
 * Author : Create by Linxinyuan on 2018/11/29
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */
public abstract class BaseRetryWrapper {
    /**
     * 文件重传
     *
     * @param needReUpload
     * @param runnable
     */
    protected void startRetry(String[] needReUpload, final RealSendRunnable runnable) {
        try {
            //区分为Log重传任务队列与Zip重传任务队列//TODO [进行实体文件不存在的删除操作]
            String[] needReUploadLizhiLog = FileDisposeUtils.filterLizhiRetryItem(needReUpload);
            //普通日志上传队列非空且AppConfig配置可以上传Logan生成日志文件
            retryUploadLizhiLog(needReUploadLizhiLog, runnable);
        } catch (Exception e) {
            Logz.tag(LOGAN_TAG).i("网络连接正常，存在缓存任务且重试上传失败!");
        }
    }

    /**
     * lizhi-Log上传
     *
     * @param needReUploadLizhiLog
     * @param runnable
     */
    protected void retryUploadLizhiLog(String[] needReUploadLizhiLog, RealSendRunnable runnable) {
        if (needReUploadLizhiLog != null && needReUploadLizhiLog.length > 0
                && Logz.getLogConfiger().getAppConfigUpload()) {
            Logz.tag(LOGAN_TAG).i("Log文件AppConfig配置可上传");
            for (String needs : needReUploadLizhiLog) {
                Logz.tag(LOGAN_TAG).i("Log文件：%s >> 已添加到启动重传任务", needs);
            }
            try {
                Logan.s(DEFALUT_ULOG_TAG, needReUploadLizhiLog, runnable);
            } catch (Exception e) {
                Logz.e(e.toString());
            }
        }
    }
}
