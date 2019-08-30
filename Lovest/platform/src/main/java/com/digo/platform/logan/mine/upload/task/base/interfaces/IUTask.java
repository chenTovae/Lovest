package com.digo.platform.logan.mine.upload.task.base.interfaces;

import android.content.Context;

import com.digo.platform.logan.mine.upload.RealSendRunnable;

/**
 * Author : Create by Linxinyuan on 2018/11/27
 * Email : linxinyuan@lizhi.fm
 * Desc : 上传任务统一接口
 */
public interface IUTask {
    void runTask(final Context context, final RealSendRunnable runnable);
}
