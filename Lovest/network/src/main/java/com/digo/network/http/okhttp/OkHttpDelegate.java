package com.digo.network.http.okhttp;

import android.util.Log;

import com.digo.network.BuildConfig;
import com.digo.network.http.HttpConstants;
import com.digo.network.http.HttpRequest;
import com.digo.network.http.rx.IRxHttpRequest;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpDelegate<T> extends IRxHttpRequest<T> {

    private static ConnectionPool sConnectionPool;

    public <T> Observable<T> request(@NonNull final HttpRequest request, @NonNull final Class<T> tClass) {
        return Observable.just(request)
                .map(this::buildRequest)
                .map(r -> getOkHttpClient().newCall(r).execute())
                .map(response -> transformResponse(response, tClass));
    }

    @Override
    public Observable<T> request(@NonNull final HttpRequest request, @NonNull final Type returnType) {
        return Observable.just(request)
                .map(this::buildRequest)
                .map(r -> getOkHttpClient().newCall(r).execute())
                .map(response -> transformResponse(response, returnType));
    }

    private Request buildRequest(final HttpRequest request){
        RequestBody requestBody = null;
        if(request.method.equalsIgnoreCase(HttpRequest.POST)){
            switch (request.bodyType){
                case HttpConstants.body_type.raw:
                    requestBody = request.body == null ? null : RequestBody.create(MediaType.parse(request.contentType), request.body);
                    break;
                case HttpConstants.body_type.text:
                    requestBody = request.strBody == null ? null :RequestBody.create(MediaType.parse(request.contentType), request.strBody);
                    break;
                case HttpConstants.body_type.form:
                    FormBody.Builder builder = new FormBody.Builder(MediaType.parse(request.contentType)
                            .charset(Charset.forName("UTF-8")));
                    for (Map.Entry<String, String> body : request.formBody.entrySet()) {
                        builder.add(body.getKey(), body.getValue());
                    }
                    requestBody = builder.build();
                    break;
                case HttpConstants.body_type.file:
                    requestBody = request.fileBody == null ? null : RequestBody.create(MediaType.parse(request.contentType), request.fileBody);
                    break;
                default:
                        break;
            }
        }
        return new Request.Builder()
                .url(request.url)
                .method(request.method, requestBody)
                .headers(Headers.of(request.headers))
                .build();
    }

    @SuppressWarnings("unchecked")
    private <T> T transformResponse(Response response, final Class<T> tClass) throws IOException {
        if(tClass == byte[].class){
            return (T)response.body().bytes();
        }else if(tClass == Object.class || tClass == String.class){
            return (T) (response.body() != null ? response.body().string() : null);
        } else{
            return new Gson().fromJson(response.body() != null ? response.body().string() : null, tClass);
        }
    }

    @SuppressWarnings("unchecked")
    private T transformResponse(Response response, final Type returnType) throws IOException {
        if(returnType == byte[].class){
            return (T)response.body().bytes();
        }else if(returnType == Object.class || returnType == String.class){
            return (T) (response.body() != null ? response.body().string() : null);
        } else{
            return new Gson().fromJson(response.body() != null ? response.body().string() : null, returnType);
        }
    }

    private static OkHttpClient getOkHttpClient() {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();

        //所有service公用一个连接池
        if(sConnectionPool == null){
            sConnectionPool = new ConnectionPool();
        }
        builder.connectionPool(sConnectionPool);
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(15, TimeUnit.SECONDS);
        if(BuildConfig.DEBUG) {
            builder.addInterceptor(getInterceptor());
        }
        return builder.build();
    }

    private static HttpLoggingInterceptor getInterceptor(){
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
            Log.i("HttpRequest","http Request = "+message);
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }
}
