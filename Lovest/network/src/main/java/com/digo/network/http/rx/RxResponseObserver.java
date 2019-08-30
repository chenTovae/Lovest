package com.digo.network.http.rx;

import com.digo.network.http.exception.BaseException;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;

public class RxResponseObserver<T> extends DisposableObserver<T> {

    private RxResponseListener<T> mResponseListener;

    public RxResponseObserver(@NonNull RxResponseListener<T> responseListener) {
        this.mResponseListener = responseListener;
    }

    @Override
    public void onNext(T response) {
        mResponseListener.onSuccess(response);
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        BaseException baseException = BaseException.handleException(e);
        mResponseListener.onError(baseException.getCode(), baseException.getMessage());
    }

    @Override
    public void onComplete() {

    }
}
