package com.digo.network.http.rx;

import io.reactivex.Scheduler;

public class RxThreadComposeUtil {
    /**
     * 主要用来切换线程
     *
     * @param <T>
     * @return
     */
    public static <T> RxThreadTransformer<T> applySchedulers() {
        return new RxThreadTransformer<>();
    }

    public static <T> RxThreadTransformer<T> applySchedulers(final Scheduler subscribeOnScheduler,
                                                             final Scheduler observeOnScheduler) {
        return new RxThreadTransformer<>(subscribeOnScheduler, observeOnScheduler);
    }
}
