package com.digo.network.http.rx;

import com.digo.network.http.HttpRequest;

import java.lang.reflect.Type;

import io.reactivex.Observable;

public abstract class IRxHttpRequest<T> {
    public abstract Observable<T> request(HttpRequest request, Type returnType);
}
