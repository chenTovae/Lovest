package com.digo.platform.executor;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * IO线程池
 */
class IOThreadExecutor extends RxExecutor{

    @Override
    Scheduler scheduler() {
        return Schedulers.io();
    }
}
