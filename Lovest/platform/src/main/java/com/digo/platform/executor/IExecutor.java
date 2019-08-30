package com.digo.platform.executor;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 线程执行器接口，注释见{@link ThreadExecutor}
 */
interface IExecutor {

    void execute(Runnable task);

    LVTimerTask schedule(Runnable task, long delay);

    LVTimerTask schedule(Runnable task, Date time);

    Future<?> submit(Runnable task);

    <T> Future<T> submit(Callable<T> task);

    <T> Future<T> submit(Runnable task, T result);
}
