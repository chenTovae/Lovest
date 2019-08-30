package com.digo.platform.executor;

import java.util.TimerTask;

import io.reactivex.disposables.Disposable;

public abstract class LVTimerTask extends TimerTask{

    Disposable disposable;

    private boolean isCancelled = false;

    @Override
    public boolean cancel() {
        isCancelled = true;
        if (disposable != null) {
            disposable.dispose();
            return disposable.isDisposed();
        }
        return super.cancel();
    }

    public boolean isCancelled() {
        return isCancelled;
    }
}
