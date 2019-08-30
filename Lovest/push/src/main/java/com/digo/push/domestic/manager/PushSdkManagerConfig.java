package com.digo.push.domestic.manager;

/**
 * author:  JackSong .
 * describe:PushSdkManager's config file
 * on 2017/12/4.
 *
 * @author JackSong
 */

public class PushSdkManagerConfig {
    /**
     * 推送连接返回码成功
     */
    public static final int PUSH_CONNECTION_RESULTCODE_SUCCESS = 0;
    /**
     * 推送本地网络错误
     */
    public static final int PUSH_CONNECTION_NET_ERROR = -1;


    public class PushType {
        /**
         * 信鸽推送 10
         */
        public static final int PUSH_TYPE_XINGE = 10;
        /**
         * 小米推送 30
         */
        public static final int PUSH_TYPE_XIAOMI = 30;
        /**
         * 华为推送 31
         */
        public static final int PUSH_TYPE_HUAWEI = 31;
        /**
         * 魅族推送 32
         */
        public static final int PUSH_TYPE_MEIZU = 32;
        /**
         * oppo推送 33
         */
        public static final int PUSH_TYPE_OPPO = 33;
        /**
         * vivo推送 34
         */
        public static final int PUSH_TYPE_VIVO = 34;
        /**
         * 个推推送 34
         */
        public static final int PUSH_TYPE_GETUI = 35;
    }

    /**
     * 0：更新 Token
     * 1：删除Token
     * 100：重置所有token并可选择是否设置一个新token
     */
    public class PushAct {
        public static final int PUSHACT_UPDATE = 0;
        public static final int PUSHACT_DELETE = 1;
        public static final int PUSHACT_RESET = 100;
    }
}
