package com.digo.platform.logan.mine.tree;

import android.text.TextUtils;
import android.util.Log;

import com.digo.platform.logan.mine.Logz;
import com.digo.platform.logan.mine.base.Tree;
import com.digo.platform.logan.mine.config.ILogzConfig;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Author : Create by Linxinyuan on 2018/08/02
 * Email : linxinyuan@lizhi.fm
 * Desc : 输出到Logcat的日志树节点(默认日志输出级别为VERBOSE)
 */
public class DebugTree extends Tree {
    private ExecutorService singleThreadExecutor;

    public DebugTree() {
        //DebugTree Constructor
        singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    protected ILogzConfig configer() {
        return Logz.getLogConfiger();//使用全局默认配置
    }

    @Override
    protected void log(final int type, final String tag, final String message) {
        //TODO 使用线程进行异步打印-避免阻塞主进程
        if (!TextUtils.isEmpty(tag) && !TextUtils.isEmpty(message)) {
            if (singleThreadExecutor != null) {
                Observable.just(1)
                        .observeOn(Schedulers.from(singleThreadExecutor))
                        .map(new Function<Integer, Boolean>() {
                            @Override
                            public Boolean apply(Integer s) throws Exception {
                                LogOutput(type, tag, message);
                                return true;
                            }
                        }).subscribe();
            }
        }
    }

    protected void LogOutput(int type, String tag, String message) {
        switch (type) {
            case Log.VERBOSE:
                Log.v(tag, message);
                break;
            case Log.INFO:
                Log.i(tag, message);
                break;
            case Log.DEBUG:
                Log.d(tag, message);
                break;
            case Log.WARN:
                Log.w(tag, message);
                break;
            case Log.ERROR:
                Log.e(tag, message);
                break;
            case Log.ASSERT:
                Log.wtf(tag, message);
                break;
            default:
                //默认输出为D级别
                Log.d(tag, message);
                break;
        }
    }
}