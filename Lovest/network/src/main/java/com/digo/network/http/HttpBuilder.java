package com.digo.network.http;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class HttpBuilder<T> {
    Type entityClass;

    protected <T> HttpBuilder() {
        entityClass =
                (Type) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @SuppressWarnings("unchecked")
    protected Type getGenericTypeClass() {
        try {
            Type mySuperclass = getClass().getGenericSuperclass();
            return ((ParameterizedType) mySuperclass).getActualTypeArguments()[0];
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Class is not parametrized with generic type!!! " +
                    "Please use extends <> ");
        }
    }
}
