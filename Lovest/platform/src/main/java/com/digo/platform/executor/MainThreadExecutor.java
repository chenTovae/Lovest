package com.digo.platform.executor;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * 可以使任务跑在MainThread的执行器
 */
class MainThreadExecutor extends BaseExecutor implements IExecutor, Handler.Callback{

    private Handler handler = new Handler(Looper.getMainLooper(), this);

    @Override
    public void execute(Runnable task) {
        handler.post(task);
    }

    @Override
    public LVTimerTask schedule(Runnable task, long delay) {
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
                execute(task);
            }
        };
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        Message msg = Message.obtain();
        FutureTask<T> futureTask = new FutureTask<>(task);
        msg.obj = futureTask;
        handler.sendMessage(msg);
        return futureTask;
    }

    @Override
    public Future<?> submit(Runnable task) {
        Message msg = Message.obtain();
        FutureTask<Void> futureTask = new FutureTask<>(task, null);
        msg.obj = futureTask;
        handler.sendMessage(msg);
        return futureTask;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        Message msg = Message.obtain();
        FutureTask<T> futureTask = new FutureTask<>(task, result);
        msg.obj = futureTask;
        handler.sendMessage(msg);
        return futureTask;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.obj instanceof FutureTask) {
            FutureTask futureTask = (FutureTask) msg.obj;
            futureTask.run();
            return true;
        }
        return false;
    }
}
