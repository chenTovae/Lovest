package com.digo.platform.executor;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * 后台线程，用于执行较小任务
 */
class BackgroundThreadExecutor extends BaseExecutor implements IExecutor, Handler.Callback{

    private Handler handler;

    private List<ExecutorSaveStateInstance> mementoList = Collections.synchronizedList(new ArrayList<ExecutorSaveStateInstance>(4));

    BackgroundThreadExecutor() {
        HandlerThread handlerThread = new HandlerThread("BackgroundThreadExecutor", Process.THREAD_PRIORITY_BACKGROUND) {

            @Override
            protected void onLooperPrepared() {
                handler = new Handler(getLooper(), BackgroundThreadExecutor.this);
                if (!mementoList.isEmpty()) {
                    for (ExecutorSaveStateInstance instance : mementoList) {
                        if (instance instanceof  ExecutorSaveStateInstance.Execute) {
                            handler.post(((ExecutorSaveStateInstance.Execute) instance).task);
                        } else if (instance instanceof  ExecutorSaveStateInstance.ScheduleDelay) {
                            ExecutorSaveStateInstance.ScheduleDelay scheduleDelay = (ExecutorSaveStateInstance.ScheduleDelay) instance;
                            if (!scheduleDelay.task.isCancelled()) {
                                timer.schedule(scheduleDelay.task, scheduleDelay.delay);
                            }
                        } else if (instance instanceof ExecutorSaveStateInstance.ScheduleDate) {
                            ExecutorSaveStateInstance.ScheduleDate scheduleDate = (ExecutorSaveStateInstance.ScheduleDate) instance;
                            if (!scheduleDate.task.isCancelled()) {
                                timer.schedule(scheduleDate.task, scheduleDate.time);
                            }
                        } else if (instance instanceof ExecutorSaveStateInstance.Submit) {
                            ExecutorSaveStateInstance.Submit submit = (ExecutorSaveStateInstance.Submit) instance;
                            if (!submit.futureTask.isCancelled()) {
                                Message msg = Message.obtain();
                                msg.obj = submit.futureTask;
                                handler.sendMessage(msg);
                            }
                        }
                    }
                    mementoList.clear();
                }
            }
        };
        handlerThread.start();
    }

    @Override
    public void execute(Runnable task) {
        if (handler != null) {
            handler.post(task);
        } else {
            mementoList.add(new ExecutorSaveStateInstance.Execute(task));
        }
    }

    @Override
    public LVTimerTask schedule(Runnable task, long delay) {
        if (handler != null) {
            return scheduleTask(task, delay);
        } else {
            LVTimerTask timerTask = getTimeTask(task);
            mementoList.add(new ExecutorSaveStateInstance.ScheduleDelay(timerTask, delay));
            return timerTask;
        }
    }

    @Override
    public LVTimerTask schedule(Runnable task, Date time) {
        if (handler != null) {
            return scheduleTask(task, time);
        } else {
            LVTimerTask timerTask = getTimeTask(task);
            mementoList.add(new ExecutorSaveStateInstance.ScheduleDate(timerTask, time));
            return timerTask;
        }
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
        FutureTask<T> futureTask = new FutureTask<>(task);
        if (handler != null) {
            Message msg = Message.obtain();
            msg.obj = futureTask;
            handler.sendMessage(msg);
        } else {
            mementoList.add(new ExecutorSaveStateInstance.Submit(futureTask));
        }
        return futureTask;
    }

    @Override
    public Future<?> submit(Runnable task) {
        FutureTask<Void> futureTask = new FutureTask<>(task, null);
        if (handler != null) {
            Message msg = Message.obtain();
            msg.obj = futureTask;
            handler.sendMessage(msg);
        } else {
            mementoList.add(new ExecutorSaveStateInstance.Submit(futureTask));
        }
        return futureTask;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        FutureTask<T> futureTask = new FutureTask<>(task, result);
        if (handler != null) {
            Message msg = Message.obtain();
            msg.obj = futureTask;
            handler.sendMessage(msg);
        } else {
            mementoList.add(new ExecutorSaveStateInstance.Submit(futureTask));
        }
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
