package com.digo.network.http;

public class HttpConstants {

    public static class code {
        /**
         * 约定成功返回code
         */
        public static final int SUCCESS = 0;
        /**
         * prompt 拦截状态码
         */
        public static final int PROMPT = 1001;
    }

    public static class error {

        /**
         * 未授权
         */
        public static final int UNAUTHORIZED = 401;
        /**
         * 禁止
         */
        public static final int FORBIDDEN = 403;
        /**
         * 未找到
         */
        public static final int NOT_FOUND = 404;
        /**
         * 请求超时
         */
        public static final int REQUEST_TIMEOUT = 408;
        /**
         * 服务器内部错误
         */
        public static final int INTERNAL_SERVER_ERROR = 500;
        /**
         * 错误网关
         */
        public static final int BAD_GATEWAY = 502;
        /**
         * 服务不可用
         */
        public static final int SERVICE_UNAVAILABLE = 503;
        /**
         * 网关超时求
         */
        public static final int GATEWAY_TIMEOUT = 504;

        //自定义code
        //由于LZ目前没有公共约定的rCode区间，暂定66x，出了冲突再处理
        /**
         * 没有网络
         */
        public static final int NO_NET = 660;
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 661;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 662;
        /**
         * 网络错误
         */
        public static final int NETWORK_ERROR = 663;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = 664;
        /**
         * 证书出错
         */
        public static final int SSL_ERROR = 665;
    }

    /**
     * 内部使用的，body的类型
     */
    public static class body_type {
        /**
         * 没有body
         */
        public static final int none = 0;
        /**
         * 字节流
         */
        public static final int raw = 1;
        /**
         * 字符串
         */
        public static final int text = 2;
        /**
         * form 表单
         */
        public static final int form = 3;
        /**
         * 文件
         */
        public static final int file = 4;
    }
}
