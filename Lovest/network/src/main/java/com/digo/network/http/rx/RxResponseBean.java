package com.digo.network.http.rx;

import com.digo.network.http.HttpConstants;
import com.google.gson.Gson;

import java.io.Serializable;

public class RxResponseBean<T> implements Serializable {
    /**
     * 返回code
     */
    private int rCode = -1;
    /**
     * 返回提示信息
     */
    private String msg;
    /**
     * 返回数据内容
     */
    private T data;

    public int getCode() {
        return rCode;
    }

    public void setCode(int code) {
        this.rCode = code;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String message) {
        this.msg = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return this.rCode == HttpConstants.code.SUCCESS;
    }

    public String toString(){
       return new Gson().toJson(this);
    }
}
