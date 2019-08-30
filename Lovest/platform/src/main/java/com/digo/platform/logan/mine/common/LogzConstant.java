package com.digo.platform.logan.mine.common;

import android.content.Context;
import android.os.Environment;

import com.digo.platform.BuildConfig;
import com.digo.platform.R;
import com.digo.platform.logan.mine.Logz;
import com.digo.platform.logan.mine.config.ILogzConfig;
import com.digo.platform.logan.mine.parser.CollectionParser;
import com.digo.platform.logan.mine.parser.IParser;
import com.digo.platform.logan.mine.parser.MapParser;
import com.digo.platform.logan.mine.parser.intent.BundleParse;
import com.digo.platform.logan.mine.parser.intent.IntentParser;
import com.digo.platform.logan.mine.utils.SystemInfoUtils;

import java.util.List;


/**
 * Author : Create by Linxinyuan on 2018/08/02
 * Email : linxinyuan@lizhi.fm
 * Desc : Logz日志常量类
 */
public class LogzConstant {
    public static final String LOGAN_TAG = "LovestLogan";
    public static final String DEFALUT_ULOG_TAG = "Lovest";
    public static final String SD_ROOT_PATH = Environment.getExternalStorageDirectory().getPath();
    //================================== Custom-Logan-config ===================================

    // 分割线方位
    public static final int DIVIDER_TOP = 1;
    public static final int DIVIDER_BOTTOM = 2;
    public static final int DIVIDER_CENTER = 4;
    public static final int DIVIDER_NORMAL = 3;

    public static final int LINE_MAX = 3 * 1024;// 最大日志长度
    public static final int CALL_STACK_INDEX = 5;// 堆栈寻址下标
    public static final int JSON_PRINT_INDENT = 4;// Json输出缩进
    public static final int MAX_CHILD_LEVEL = 1;//Object最大解析层级(父子)
    public static final long DAY_STAMP_LONG = 24 * 60 * 60 * 1000;//一天的时间戳

    public static final String TIP_OBJECT_NULL = "Object[object is null]";//空类
    public static final String BR = System.getProperty("line.separator");// 换行符

    public static final String WRITING_RULE = "^\\d+$";//实时写文件正则匹配
    public static final String DEPOT_RULE = "^\\d{4}-{1}\\d{2}-{1}\\d{2}$";//日期文件夹正则匹配规则

    //解析器初始化集合数组
    public static final Class<? extends IParser>[] DEFAULT_PARSE_CLASS = new Class[]{
            CollectionParser.class, MapParser.class, IntentParser.class, BundleParse.class
    };

    public static List<IParser> getParserList(ILogzConfig configer) {
        return configer.getParserList();
    }

    //================================== Meituan-Logan-config ===================================

    //Logan框架type标志位(暂时废弃使用msg_json)
    public static final int LOGAN_TYPE = 1;
    public static final long DEFAULT_DAY = 7; //默认保存天数-7M
    public static final long DEFAULT_FILE_SIZE = 10;//默认最大文件大小-10M
    public static final long DEFAULT_MIN_SDCARD_SIZE = 50; //最小的SD卡小于这个大小不写入-50M

    public static byte[] getDefaultEncryptKey() {
        return new String(getBK1() + getBK2()).getBytes();
    }

    public static byte[] getDefaultEncryptIV() {
        return new String(getIV1() + getIV2()).getBytes();
    }

    public static String getBK1() {
        return BuildConfig.keyPre;
    }

    public static String getBK2() {
        return Logz.getContext().getResources().getString(R.string.key_end);
    }

    public static String getIV1() {
        return BuildConfig.ivPre;
    }

    public static String getIV2() {
        return Logz.getContext().getResources().getString(R.string.iv_end);
    }

    public static String getDefaultPathPath() {
        return SD_ROOT_PATH + "/Lovest/Logan";
    }

    public static String getDefaultCachePath(Context context) {
        return SD_ROOT_PATH + "/Lovest/Caches/logan/" + SystemInfoUtils.getProcessName(context);
    }

    //=============================== AppConfig-Logan-config =====================================

    public static final String DEFAULT_LEVEL = "I";//写入文件级别，D测试，I普通，W告警，E错误，默认I
    public static final int DEFAULT_SAVE_FLAG = 1;// 是否写入文件总开关，0关闭，1开启，返回值0-false/1-true
    public static final int DEFAULT_UPLOAD_FLAG = 1;// 是否上传文件总开关，0关闭，1开启，返回值0-false/1-true
    public static final int DEFAULT_FILE_SIZE_FLAG = 2 * 1024 * 1024;//分片大小，超过则重新分片，单位B，默认2MB
    public static final int DEFAULT_FILE_NUM = 10;//分片数目，默认10片

    //======================================== 业务相关 ==========================================
    //网络模式静态常量
    public static final int MODE_4G = 0x10;
    public static final int MODE_WIFI = 0x01;

    public static final long DEFAULT_UID = 0L;//默认的用户uid
    public static final String DEFAULT_DID = "Unknow";//默认用户deviceid
}
