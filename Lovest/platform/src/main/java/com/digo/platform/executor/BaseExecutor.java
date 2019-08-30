package com.digo.platform.executor;

import java.util.Date;
import java.util.Timer;

/**
 * 延迟执行基础类
 */
abstract class BaseExecutor {

    protected Timer timer;

    BaseExecutor() {
        timer = new Timer(true);
    }

    LVTimerTask scheduleTask(Runnable task, long delay) {
        LVTimerTask timerTask = getTimeTask(task);
        timer.schedule(timerTask, delay);
        return timerTask;
    }

    LVTimerTask scheduleTask(Runnable task, Date time){
        LVTimerTask timerTask = getTimeTask(task);
        timer.schedule(timerTask, time);
        return timerTask;
    }

    abstract LVTimerTask getTimeTask(Runnable task);
}
