package com.digo.network.http.exception;

import com.digo.network.http.HttpConstants;
import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;

import retrofit2.HttpException;

public class BaseException extends Exception {

    private int code;

    private String message;

    public BaseException(Throwable throwable, String message, int code) {
        super(throwable);
        this.message = message;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static BaseException handleException(Throwable e) {
        String message;
        int errorCode;

        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            switch (httpException.code()) {
                case HttpConstants.error.UNAUTHORIZED:
                case HttpConstants.error.FORBIDDEN:
                case HttpConstants.error.NOT_FOUND:
                case HttpConstants.error.REQUEST_TIMEOUT:
                case HttpConstants.error.GATEWAY_TIMEOUT:
                case HttpConstants.error.INTERNAL_SERVER_ERROR:
                case HttpConstants.error.BAD_GATEWAY:
                case HttpConstants.error.SERVICE_UNAVAILABLE:
                default:
                    errorCode = HttpConstants.error.HTTP_ERROR;
                    message = "网络错误\n" + e.getMessage();
                    break;
            }
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            errorCode = HttpConstants.error.PARSE_ERROR;
            message = "解析错误\n" + e.getMessage();
        } else if (e instanceof ConnectException) {
            errorCode = HttpConstants.error.NETWORK_ERROR;
            message = "连接失败\n" + e.getMessage();

        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            errorCode = HttpConstants.error.SSL_ERROR;
            message = "证书验证失败\n" + e.getMessage();
        } else if (e instanceof UnknownHostException) {
            errorCode = HttpConstants.error.NO_NET;
            message = "没有网络\n" + e.getMessage();
        } else if (e instanceof SocketTimeoutException) {
            errorCode = HttpConstants.error.REQUEST_TIMEOUT;
            message = "请求超时\n" + e.getMessage();
        } else if (e instanceof BaseException) {
            return (BaseException) e;
        } else {
            errorCode = HttpConstants.error.UNKNOWN;
            message = "未知错误\n" + e.getMessage();
        }

        return new BaseException(e, message, errorCode);
    }

    /**
     * 把自定义的message过滤掉
     *
     * @param code    返回的code
     * @param messgae 返回的message
     * @return
     */
    public static String getCommonTip(int code, String messgae) {
        switch (code) {
            case HttpConstants.error.NO_NET:
            case HttpConstants.error.UNKNOWN:
            case HttpConstants.error.PARSE_ERROR:
            case HttpConstants.error.NETWORK_ERROR:
            case HttpConstants.error.HTTP_ERROR:
            case HttpConstants.error.SSL_ERROR:
                return "网络开小差了…";

            default:
                return messgae;
        }
    }
}
