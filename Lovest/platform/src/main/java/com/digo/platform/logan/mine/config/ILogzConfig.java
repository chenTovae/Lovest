package com.digo.platform.logan.mine.config;

import android.content.Context;

import com.digo.platform.logan.mine.parser.IParser;

import java.util.List;

/**
 * Author : Create by Linxinyuan on 2018/08/02
 * Email : linxinyuan@lizhi.fm
 * Desc : logz系统配置接口类
 */
public interface ILogzConfig {
    //======================= Most Important ==========================

    ILogzConfig attchContext(Context context);

    //============================== Base ==============================

    //设置是否输出日志
    ILogzConfig configAllowLog(boolean allowLog);

    //设置当前包模式
    ILogzConfig configCurrentMode(boolean curMode);

    //设置是否显示排版线条
    ILogzConfig configShowBorders(boolean showBorder);

    //设置日志最小显示级别
    ILogzConfig configMimLogLevel(int mimLogLevel);

    //设置解析类(父类与类成员)层级(考虑到反射效率,取值范围限定是0-2,默认为1)
    ILogzConfig configClassParserLevel(int parserLevel);

    //============================== Base ===============================

    //============================== MeiTuan =============================

    //多应用适配
    ILogzConfig configLoganAppKey(String appKey);

    //是否需要初始化美团日志框架Logan
    ILogzConfig configIsNeedLogan(boolean isNeed);

    //当前是否是主进程
    ILogzConfig configIsMainProcess(boolean isMain);

    //设置日志缓存路径
    ILogzConfig configCachePath(String cachePath);

    //设置日志保存路径
    ILogzConfig configSavePath(String cachePath);

    //日志最大长度切片
    ILogzConfig configMaxFile(long maxFile);

    //日志保存时间：天
    ILogzConfig configSaveDay(long day);

    //磁盘大小检查最小阈值
    ILogzConfig configMinSDCard(long minSDCard);

    //16位ase加密Key
    ILogzConfig configEncryptKey16(byte[] encryptKey16);

    //16位ase加密IV
    ILogzConfig configEncryptIV16(byte[] encryptIv16);

    //============================== MeiTuan =============================

    //============================== AppConfig ==============================

    ILogzConfig extraConfigLevel(String level);

    ILogzConfig extraConfigSave(int save);

    ILogzConfig extraConfigUpload(int upload);

    ILogzConfig extraConfigFileSize(int fileSize);

    ILogzConfig extraConfigFileNum(int fileNum);

    //============================== AppConfig ==============================

    //============================== BuildFactory ==============================

    //配置器构造方法-smartBuild为默认最优配置
    void build();

    void smartBuild();

    //============================== BuildFactory ==============================

    boolean getAppConfigSave();

    boolean getAppConfigUpload();

    int getAppConfigLevel();

    int getAppConfigFileSize();

    int getAppConfigFileNum();

    //获取context
    Context getContext();

    //获取日志最小输出级别
    int getMimLogLevel();

    //获取日志解析类的上下最大层级
    int getParserLevel();

    //获取是否输出格式化日志标志位
    boolean isShowBorder();

    //获取是否输出日志标志位
    boolean isEnable();

    //获取自定义转换器列表
    List<IParser> getParserList();

    //获取美团Logan是否已经初始化
    boolean getMeituLoganInit();

    //获取日志缓存路径
    String getCachePath();

    //获取日志保存路径
    String getSavePath();

    //获取是否需要初始化Logan
    boolean getIsNeedLogan();

    //获取当前是否是主进程
    boolean getIsMainProcess();

    //获取当前包模式
    boolean getCurrentMode();

    //获取当前appkey
    String getLoganAppKey();
}
