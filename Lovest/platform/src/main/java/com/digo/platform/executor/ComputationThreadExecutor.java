package com.digo.platform.executor;


import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * 运算线程池
 */
class ComputationThreadExecutor extends RxExecutor{

    @Override
    Scheduler scheduler() {
        return Schedulers.computation();
    }
}
