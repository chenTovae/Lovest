package com.digo.network.http.rx;

public interface RxResponseListener<T> {
    /**
     * 成功的回调
     *
     * @param data 返回的数据
     */
    void onSuccess(T data);


    /**
     * 失败的回调
     *
     * @param code 失败code
     * @param msg  失败提示
     */
    void onError(int code, String msg);
}
