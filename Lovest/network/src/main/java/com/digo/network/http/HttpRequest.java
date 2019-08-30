package com.digo.network.http;

import com.digo.network.http.okhttp.OkHttpDelegate;
import com.digo.network.http.rx.RxResponseListener;
import com.digo.network.http.rx.RxResponseObserver;
import com.digo.network.http.rx.RxThreadComposeUtil;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;

public class HttpRequest {

    public static final String GET = "GET";

    public static final String POST = "POST";

    public static final String CONTENT_TYPE_TEXT = "text/plain";

    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

    public static final String CONTENT_TYPE_FORM_UTF8 = "application/x-www-form-urlencoded;" +
            "charset=UTF-8";

    public static final String CONTENT_TYPE_JSON = "application/json";

    public static final String CONTENT_TYPE_JSON_UTF8 = "application/json;charset=UTF-8";

    public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";

    public static final String TAG = HttpRequest.class.getSimpleName();

    public static final Type DEFAULT_RETURN_TYPE = String.class;

    public final String url;
    public final String method;
    public final Map<String, String> headers;

    public final int bodyType;
    @Nullable
    public final byte[] body;
    @Nullable
    public final String strBody;
    @Nullable
    public final File fileBody;
    public final String contentType;

    public final Map<String, String> formBody;

    private HttpRequest(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.headers = builder.headers;
        this.bodyType = builder.bodyType;
        this.body = builder.body;
        this.strBody = builder.strBody;
        this.fileBody = builder.fileBody;
        this.contentType = builder.contentType;
        this.formBody = builder.formBody;
    }

    @SuppressWarnings("unchecked")
    public Observable<String> asObservable() {
        return (Observable<String>) asObservable(DEFAULT_RETURN_TYPE);
    }

    public <T> Observable<T> asObservable(Class<T> returnClass) {
        return new OkHttpDelegate<>().request(this, returnClass);
    }

    public Observable<?> asObservable(Type returnType) {
        return new OkHttpDelegate<>().request(this, returnType);
    }

    public Disposable newCall(@NonNull final RxResponseListener<String> callback) {
        return newCall(DEFAULT_RETURN_TYPE, callback);
    }

    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    public Disposable newCall(Type returnType, @NonNull RxResponseListener<?> callback) {
        return this.asObservable(returnType)
                .compose(RxThreadComposeUtil.applySchedulers())
                .subscribeWith(new RxResponseObserver(callback));
    }

    public static class Builder {
        String url;
        String method;
        Map<String, String> headers;
        int bodyType;
        byte[] body;
        String strBody;
        Map<String, String> formBody;
        File fileBody;
        String contentType;

        public Builder() {
            super();
            this.method = GET;
            this.headers = new HashMap<>();
            this.formBody = new HashMap<>();
            this.contentType = CONTENT_TYPE_FORM;
            this.bodyType = HttpConstants.body_type.none;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }


        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder rawBody(byte[] body) {
            if (body != null) {
                this.bodyType = HttpConstants.body_type.raw;
            } else {
                this.bodyType = HttpConstants.body_type.none;
            }
            this.body = body;
            return this;
        }

        public Builder addFormBody(String key, String val) {
            this.bodyType = HttpConstants.body_type.form;
            this.formBody.put(key, val);
            return this;
        }

        public Builder formBody(Map<String, String> formBody) {
            this.bodyType = HttpConstants.body_type.form;
            if (formBody != null) {
                this.formBody = formBody;
            } else {
                this.formBody.clear();
            }
            return this;
        }

        public Builder stringBody(String body) {
            if (body != null) {
                this.bodyType = HttpConstants.body_type.text;
            } else {
                this.bodyType = HttpConstants.body_type.none;
            }
            this.strBody = body;
            return this;
        }

        public Builder fileBody(File body) {
            if (body != null) {
                this.bodyType = HttpConstants.body_type.file;
            } else {
                this.bodyType = HttpConstants.body_type.none;
            }
            this.fileBody = body;
            return this;
        }

        public Builder headers(Map<String, String> header) {
            if (header != null) {
                this.headers = header;
            } else {
                this.headers.clear();
            }
            return this;
        }

        public Builder addHeader(String name, String val) {
            this.headers.put(name, val);
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(this);
        }

    }
}
