package com.digo.platform.executor;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * RxJava封装的线程池
 */
abstract class RxExecutor extends BaseExecutor implements IExecutor{

    abstract Scheduler scheduler();

    @Override
    public void execute(Runnable task) {
        rxRun(task);
    }

    @Override
    public LVTimerTask schedule(final Runnable task, long delay) {
        return scheduleTask(task, delay);
    }

    @Override
    public LVTimerTask schedule(Runnable task, Date time) {
        return scheduleTask(task, time);
    }

    @Override
    LVTimerTask getTimeTask(final Runnable task) {
        return new LVTimerTask() {
            @Override
            public void run() {
                this.disposable = rxRun(task);
            }
        };
    }

    public Future<?> submit(Runnable task) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<Void> ftask = new FutureTask(task, null);
        execute(ftask);
        return ftask;
    }

    public <T> Future<T> submit(Callable<T> task){
        if (task == null) throw new NullPointerException();
        RunnableFuture<T> ftask = new FutureTask(task);
        execute(ftask);
        return ftask;
    }

    public <T> Future<T> submit(Runnable task, T result) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<T> ftask = new FutureTask(task, result);
        execute(ftask);
        return ftask;
    }

    private Disposable rxRun(Runnable task) {
        return Observable.just(task)
                .observeOn(scheduler())
                .subscribe(new Consumer<Runnable>() {
                    @Override
                    public void accept(Runnable runnable) throws Exception {
                        runnable.run();
                    }
                });
    }

}
