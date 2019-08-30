package com.digo.platform.executor;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * 用于短期执行的异步线程池
 */

class AsyncThreadExecutor extends RxExecutor {

    @Override
    Scheduler scheduler() {
        return Schedulers.newThread();
    }
}
