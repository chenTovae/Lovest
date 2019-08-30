package com.digo.platform.executor;

import java.util.Date;
import java.util.concurrent.FutureTask;

class ExecutorSaveStateInstance {

    static class Execute extends ExecutorSaveStateInstance {

        Runnable task;

        Execute(Runnable task) {
            this.task = task;
        }
    }

    static class ScheduleDelay extends ExecutorSaveStateInstance {

        LVTimerTask task;

        long delay;

        ScheduleDelay(LVTimerTask task, long delay) {
            this.task = task;
            this.delay = delay;
        }
    }

    static class ScheduleDate extends ExecutorSaveStateInstance {

        LVTimerTask task;

        Date time;

        ScheduleDate(LVTimerTask task, Date time) {
            this.task = task;
            this.time = time;
        }
    }

    static class Submit extends ExecutorSaveStateInstance {

        FutureTask futureTask;

        Submit(FutureTask futureTask) {
            this.futureTask = futureTask;
        }
    }
}
