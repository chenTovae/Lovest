package com.digo.platform.logan.mine.config;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.digo.platform.logan.meituan.Logan;
import com.digo.platform.logan.meituan.config.LoganConfig;
import com.digo.platform.logan.meituan.protocol.OnLoganProtocolStatus;
import com.digo.platform.logan.meituan.route.IFileModifyCallback;
import com.digo.platform.logan.mine.Logz;
import com.digo.platform.logan.mine.common.LogzConstant;
import com.digo.platform.logan.mine.database.daos.LoganUFileDao;
import com.digo.platform.logan.mine.parser.IParser;
import com.digo.platform.logan.mine.utils.ConverLevelUtils;
import com.digo.platform.logan.mine.utils.FileDisposeUtils;

import java.util.ArrayList;
import java.util.List;

import static com.digo.platform.logan.mine.common.LogzConstant.DEFAULT_DAY;
import static com.digo.platform.logan.mine.common.LogzConstant.DEFAULT_FILE_NUM;
import static com.digo.platform.logan.mine.common.LogzConstant.DEFAULT_FILE_SIZE;
import static com.digo.platform.logan.mine.common.LogzConstant.DEFAULT_FILE_SIZE_FLAG;
import static com.digo.platform.logan.mine.common.LogzConstant.DEFAULT_LEVEL;
import static com.digo.platform.logan.mine.common.LogzConstant.DEFAULT_MIN_SDCARD_SIZE;
import static com.digo.platform.logan.mine.common.LogzConstant.DEFAULT_SAVE_FLAG;
import static com.digo.platform.logan.mine.common.LogzConstant.DEFAULT_UPLOAD_FLAG;
import static com.digo.platform.logan.mine.common.LogzConstant.LOGAN_TAG;

/**
 * Author : Create by Linxinyuan on 2018/08/02
 * Email : linxinyuan@lizhi.fm
 * Desc : logz日志系统配置器
 */
public class LogzConfiger implements ILogzConfig {
    private Context mContext;
    private boolean isNeed;//默认都要false
    private boolean isMain;//是否是主进程
    private String appKey;//配置APPKEY
    //========================== Custom ==================================
    private boolean isEnable = true;
    private boolean isDebug = false;
    private boolean isShowBorder = false;
    private int mimLogLevel = Log.VERBOSE;
    private List<IParser> mParserList;
    private int mParserLevel = LogzConstant.MAX_CHILD_LEVEL;
    //============================== meituan =============================
    private boolean mMeituLogInit = false;
    private String mCachePath; //mmap缓存路径
    private String mPathPath; //file文件路径
    private byte[] mEncryptKey16; //16位ase加密Key
    private byte[] mEncryptIv16; //16位aes加密IV

    private long mDay = DEFAULT_DAY; //删除天数
    private long mMaxFile = DEFAULT_FILE_SIZE; //删除文件最大值
    private long mMinSDCard = DEFAULT_MIN_SDCARD_SIZE; //最小sdk卡大小

    //日志文件操作全局监听
    private static GlobalLoganFileModify globalLoganFileModify;
    //日志写入全局监听变量
    private static GlobalLoganProtocolStatus globalStatusListener;
    //============================= AppConfig ============================
    private String mfileLogLevel = DEFAULT_LEVEL;
    private int mfileSave = DEFAULT_SAVE_FLAG;
    private int mfileUpload = DEFAULT_UPLOAD_FLAG;
    private int mfileNum = DEFAULT_FILE_NUM;
    private int mfileSize = DEFAULT_FILE_SIZE_FLAG;

    /*Constructor*/
    public LogzConfiger() {
        addLogzParserClass(LogzConstant.DEFAULT_PARSE_CLASS);
        globalStatusListener = new GlobalLoganProtocolStatus();
        globalLoganFileModify = new GlobalLoganFileModify();
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public ILogzConfig attchContext(Context context) {
        if (mContext == null) {
            this.mContext = context;
        }
        return this;
    }

    @Override
    public ILogzConfig configIsNeedLogan(boolean isNeed) {
        this.isNeed = isNeed;
        return this;
    }

    @Override
    public ILogzConfig configIsMainProcess(boolean isMain) {
        this.isMain = isMain;
        return this;
    }

    @Override
    public ILogzConfig configAllowLog(boolean allowLog) {
        this.isEnable = allowLog;
        return this;
    }

    @Override
    public ILogzConfig configCurrentMode(boolean curMode) {
        this.isDebug = curMode;
        return this;
    }

    @Override
    public ILogzConfig configShowBorders(boolean showBorder) {
        this.isShowBorder = showBorder;
        return this;
    }

    @Override
    public ILogzConfig configMimLogLevel(int mimLogLevel) {
        this.mimLogLevel = mimLogLevel;
        return this;
    }

    @Override
    public ILogzConfig configClassParserLevel(int parserLevel) {
        if (parserLevel < 0 || parserLevel > 2) {
            mParserLevel = LogzConstant.MAX_CHILD_LEVEL;
        } else {
            mParserLevel = parserLevel;
        }
        return this;
    }

    @Override
    public ILogzConfig configLoganAppKey(String appKey) {
        this.appKey = appKey;
        return this;
    }

    public void addLogzParserClass(Class<? extends IParser>... parsers) {
        mParserList = new ArrayList<IParser>();//list init
        for (Class<? extends IParser> cla : parsers) {
            try {
                mParserList.add(0, cla.newInstance());
            } catch (Exception e) {
                Logz.e(e.toString());
            }
        }
    }

    @Override
    public ILogzConfig configCachePath(String cachePath) {
        this.mCachePath = cachePath;
        return this;
    }

    @Override
    public ILogzConfig configSavePath(String path) {
        this.mPathPath = path;
        return this;
    }

    @Override
    public ILogzConfig configMaxFile(long maxFile) {
        this.mMaxFile = maxFile;
        return this;
    }

    @Override
    public ILogzConfig configSaveDay(long day) {
        this.mDay = day;
        return this;
    }

    @Override
    public ILogzConfig configMinSDCard(long minSDCard) {
        this.mMinSDCard = minSDCard;
        return this;
    }

    @Override
    public ILogzConfig configEncryptKey16(byte[] encryptKey16) {
        this.mEncryptKey16 = encryptKey16;
        return this;
    }

    @Override
    public ILogzConfig configEncryptIV16(byte[] encryptIv16) {
        this.mEncryptIv16 = encryptIv16;
        return this;
    }

    @Override
    public ILogzConfig extraConfigLevel(String level) {
        this.mfileLogLevel = level;
        return this;
    }

    @Override
    public ILogzConfig extraConfigSave(int save) {
        this.mfileSave = save;
        return this;
    }

    @Override
    public ILogzConfig extraConfigUpload(int upload) {
        this.mfileUpload = upload;
        return this;
    }

    @Override
    public ILogzConfig extraConfigFileSize(int fileSize) {
        this.mfileSize = fileSize;
        return this;
    }

    @Override
    public ILogzConfig extraConfigFileNum(int fileNum) {
        this.mfileNum = fileNum;
        return this;
    }

    @Override
    public boolean getIsNeedLogan() {
        return isNeed;
    }

    @Override
    public boolean getIsMainProcess() {
        return isMain;
    }

    @Override
    public boolean getCurrentMode() {
        return isDebug;
    }

    @Override
    public String getLoganAppKey() {
        return this.appKey;
    }

    @Override
    public int getAppConfigLevel() {
        return ConverLevelUtils.StringLevel2Int(mfileLogLevel);
    }

    @Override
    public boolean getAppConfigSave() {
        return mfileSave == 0 ? false : true;
    }

    @Override
    public boolean getAppConfigUpload() {
        return mfileUpload == 0 ? false : true;
    }

    @Override
    public int getAppConfigFileSize() {
        return mfileSize;
    }

    @Override
    public int getAppConfigFileNum() {
        return mfileNum;
    }

    public boolean isEnable() {
        return this.isEnable;
    }

    public boolean getCurMode() {
        return this.isDebug;
    }

    public int getMimLogLevel() {
        return this.mimLogLevel;
    }

    public boolean isShowBorder() {
        return isShowBorder;
    }

    public List<IParser> getParserList() {
        return mParserList;
    }

    public int getParserLevel() {
        return mParserLevel;
    }

    public boolean getMeituLoganInit() {
        return mMeituLogInit;
    }

    @Override
    public String getCachePath() {
        return mCachePath;
    }

    @Override
    public String getSavePath() {
        return mPathPath;
    }

    @Override
    public void build() {
        if (mContext == null) {
            throw new IllegalArgumentException("Must call method attchContext()!");
        }
        if (TextUtils.isEmpty(mCachePath) || TextUtils.isEmpty(mPathPath)
                || mEncryptKey16 == null || mEncryptIv16 == null) {
            //非SmartBuild方式不使用默认配置值,使用者必须自己配置各个输出路径与密钥,否则异常
            throw new IllegalArgumentException("Some Important Params can not be empty!");
        } else {
            //输出路径检测,如果未检测到重要目标文件夹应创建(Logan输出目录)
            FileDisposeUtils.checkPathFileExist(new String[]{mPathPath, mCachePath});
        }

        if (getIsNeedLogan()) {
            LoganConfig config = new LoganConfig.Builder()
                    .setDay(mDay)//int
                    .setPath(mPathPath)//String
                    .setCachePath(mCachePath)//String
                    .setMaxFile(mMaxFile)//int
                    .setMinSDCard(mMinSDCard)//int
                    .setEncryptIV16(mEncryptIv16)//byte[]
                    .setEncryptKey16(mEncryptKey16)//byte[]
                    .setmDepotFileNum(mfileNum)//int
                    .setmSimpleSize(mfileSize)//long
                    .setIsMainProcess(isMain)//boolean
                    .build();
            Logan.init(config);
            Logan.setDebug(true);
            Logan.setOnLoganProtocolStatus(globalStatusListener);
            Logan.setFileModifyCallback(globalLoganFileModify);
            mMeituLogInit = true;//Logan框架初始化完成标志位
        }
    }

    @Override
    public void smartBuild() {
        if (mContext == null) {
            throw new IllegalArgumentException("Important context can not be empty!");
        }
        //帮助你配置默认的密钥以及Logan相关存储路径
        if (mEncryptKey16 == null)
            mEncryptKey16 = LogzConstant.getDefaultEncryptKey();
        if (mEncryptIv16 == null)
            mEncryptIv16 = LogzConstant.getDefaultEncryptIV();
        if (TextUtils.isEmpty(mPathPath))
            mPathPath = LogzConstant.getDefaultPathPath();
        if (TextUtils.isEmpty(mCachePath))
            mCachePath = LogzConstant.getDefaultCachePath(mContext);
        build();
    }

    class GlobalLoganProtocolStatus implements OnLoganProtocolStatus {
        @Override
        public void loganProtocolStatus(String cmd, int code) {
            Logz.tag(LOGAN_TAG).d("GlobalLoganProtocolStatus > cmd : " + cmd + " | " + "code : " + code);
        }
    }

    class GlobalLoganFileModify implements IFileModifyCallback {
        @Override
        public void onSyncFileDelete(String fn, String path) {
            Logz.tag(LOGAN_TAG).d("GlobalLoganFileModify >>> onSyncFileDelete >> fn:%s >> path:%s", fn, path);
            LoganUFileDao.getInstance(mContext).syncDelete(fn, path);//上传库过期文件记录删除同步
        }

        @Override
        public void onSyncFileDeleteOnlyPath(String path) {
            Logz.tag(LOGAN_TAG).d("GlobalLoganFileModify >>> onSyncFileDeleteOnlyPath >> path:%s", path);
            LoganUFileDao.getInstance(mContext).syncDeleteOnlyPath(path);//上传库过期文件记录删除同步
        }

        @Override
        public int onQueryFileRetryTime(String name, String path) {
            Logz.tag(LOGAN_TAG).d("GlobalLoganFileModify >>> onQueryFileRetryTime >> fn:%s >> path:%s", name, path);
            return LoganUFileDao.getInstance(mContext).queryRetryColumn(name, path);
        }
    }
}
