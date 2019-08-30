package com.digo.platform.logan.mine;


import android.content.Context;
import android.text.TextUtils;

import com.digo.platform.logan.meituan.Logan;
import com.digo.platform.logan.meituan.route.IFileArrangeCallback;
import com.digo.platform.logan.meituan.route.IFileReOpenCallback;
import com.digo.platform.logan.mine.base.Tree;
import com.digo.platform.logan.mine.common.LogzConstant;
import com.digo.platform.logan.mine.config.ILogzConfig;
import com.digo.platform.logan.mine.config.LogzConfiger;
import com.digo.platform.logan.mine.tree.ITree;
import com.digo.platform.logan.mine.tree.SoulsTree;
import com.digo.platform.logan.mine.upload.LogSendProxy;
import com.digo.platform.logan.mine.upload.RealSendRunnable;
import com.digo.platform.logan.mine.upload.callback.ILogzUploadListener;
import com.digo.platform.logan.mine.upload.task.FeedBackUTask;
import com.digo.platform.logan.mine.upload.task.NetRetryUTask;
import com.digo.platform.logan.mine.upload.task.StartRetryUTask;

import java.util.ArrayList;
import java.util.List;

import static com.digo.platform.logan.mine.common.LogzConstant.LOGAN_TAG;
import static java.util.Collections.unmodifiableList;

/**
 * Author : Create by Linxinyuan on 2018/08/02
 * Email : linxinyuan@lizhi.fm
 * Desc : 日志输出工具类//git
 */
public final class Logz {
    //全局带全参默认值配置器,其他地方只可以引用不可以设置,否则会导致所有Tree配置都受到影响
    private static LogzConfiger LOG_DEFALUT_CONFIG = new LogzConfiger();
    private static final Tree TREE_OF_SOULS = new SoulsTree();
    private static final List<Tree> FOREST = new ArrayList<>();
    private static final int STACK_CLASS_INDEX = 4;
    //全局上传回调接口
    private static ILogzUploadListener grobalUploadListener;

    private Logz() {
        throw new AssertionError("No instances.");
    }

    // default config change
    public static ILogzConfig getLogConfiger() {
        return LOG_DEFALUT_CONFIG;
    }

    public static Context getContext() {
        return LOG_DEFALUT_CONFIG.getContext();
    }

    // temp tag use only once
    public static ITree tag(String tempTag) {
        return ((SoulsTree) TREE_OF_SOULS).tag(tempTag);
    }

    // set grobal listener
    public static void setGrobalUploadListener(ILogzUploadListener listener){
        grobalUploadListener = listener;
    }

    public static ILogzUploadListener getGrobalUploadListener(){
        return grobalUploadListener;
    }

    // add new log tree
    public static void plant(Tree tree) {
        if (tree == null) {
            throw new NullPointerException("tree == null");
        }
        if (tree == TREE_OF_SOULS) {
            throw new IllegalArgumentException("Cannot plant souls tree");
        }
        synchronized (FOREST) {
            FOREST.add(tree);
            ((SoulsTree) TREE_OF_SOULS).setForestAsArray(FOREST.toArray(new Tree[FOREST.size()]));
        }
    }

    // adds new log trees
    public static void plant(Tree... trees) {
        if (trees == null) {
            throw new NullPointerException("trees == null");
        }
        for (Tree tree : trees) {
            plant(tree);
        }
    }

    // remove log tree
    public static void uproot(Tree tree) {
        synchronized (FOREST) {
            if (!FOREST.remove(tree)) {
                throw new IllegalArgumentException("Cannot uproot tree which is not planted: " + tree);
            }
            ((SoulsTree) TREE_OF_SOULS).setForestAsArray(FOREST.toArray(new Tree[FOREST.size()]));
        }
    }

    // remove all log tree
    public static void uprootAll() {
        synchronized (FOREST) {
            FOREST.clear();
            ((SoulsTree) TREE_OF_SOULS).setForestAsArray(new Tree[0]);
        }
    }

    // get log forset
    public static List<Tree> forset() {
        synchronized (FOREST) {
            return unmodifiableList(new ArrayList<>(FOREST));
        }
    }

    // get log tree count
    public static int treeCount() {
        synchronized (FOREST) {
            return FOREST.size();
        }
    }

    // return log root tree
    public static Tree asTree() {
        return TREE_OF_SOULS;
    }

    //==================================== log_level ===============================================

    public static void v(String message, Object... args) {
        tag(getTagScope(STACK_CLASS_INDEX)).v(message, args);
    }

    public static void v(Throwable t, String message, Object... args) {
        tag(getTagScope(STACK_CLASS_INDEX)).v(t, message, args);
    }

    public static void v(Throwable t) {
        tag(getTagScope(STACK_CLASS_INDEX)).v(t);
    }

    public static void v(Object o) {
        tag(getTagScope(STACK_CLASS_INDEX)).v(o);
    }

    public static void d(String message, Object... args) {
        tag(getTagScope(STACK_CLASS_INDEX)).d(message, args);
    }

    public static void d(Throwable t, String message, Object... args) {
        tag(getTagScope(STACK_CLASS_INDEX)).d(t, message, args);
    }

    public static void d(Throwable t) {
        tag(getTagScope(STACK_CLASS_INDEX)).d(t);
    }

    public static void d(Object o) {
        tag(getTagScope(STACK_CLASS_INDEX)).d(o);
    }

    public static void i(String message, Object... args) {
        tag(getTagScope(STACK_CLASS_INDEX)).i(message, args);
    }

    public static void i(Throwable t, String message, Object... args) {
        tag(getTagScope(STACK_CLASS_INDEX)).i(t, message, args);
    }

    public static void i(Throwable t) {
        tag(getTagScope(STACK_CLASS_INDEX)).i(t);
    }

    public static void i(Object o) {
        tag(getTagScope(STACK_CLASS_INDEX)).i(o);
    }

    public static void w(String message, Object... args) {
        tag(getTagScope(STACK_CLASS_INDEX)).w(message, args);
    }

    public static void w(Throwable t, String message, Object... args) {
        tag(getTagScope(STACK_CLASS_INDEX)).w(t, message, args);
    }

    public static void w(Throwable t) {
        tag(getTagScope(STACK_CLASS_INDEX)).w(t);
    }

    public static void w(Object o) {
        tag(getTagScope(STACK_CLASS_INDEX)).w(o);
    }

    public static void e(String message, Object... args) {
        tag(getTagScope(STACK_CLASS_INDEX)).e(message, args);
    }

    public static void e(Throwable t, String message, Object... args) {
        tag(getTagScope(STACK_CLASS_INDEX)).e(t, message, args);
    }

    public static void e(Throwable t) {
        tag(getTagScope(STACK_CLASS_INDEX)).e(t);
    }

    public static void e(Object o) {
        tag(getTagScope(STACK_CLASS_INDEX)).e(o);
    }

    public static void wtf(String message, Object... args) {
        tag(getTagScope(STACK_CLASS_INDEX)).wtf(message, args);
    }

    public static void wtf(Throwable t, String message, Object... args) {
        tag(getTagScope(STACK_CLASS_INDEX)).wtf(t, message, args);
    }

    public static void wtf(Throwable t) {
        tag(getTagScope(STACK_CLASS_INDEX)).wtf(t);
    }

    public static void wtf(Object o) {
        tag(getTagScope(STACK_CLASS_INDEX)).wtf(o);
    }

    //log json
    public static void json(String j) {
        tag(getTagScope(STACK_CLASS_INDEX)).json(j);
    }

    //log xml
    public static void xml(String x) {
        tag(getTagScope(STACK_CLASS_INDEX)).xml(x);
    }

    private static String getTagScope(int skipDepth) {
        String scopeTag = "Lizhi_Logz";
        try {
            final int MAX_TAG_LENGTH = 23;
            StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
            if (skipDepth > stacks.length - 1)
                skipDepth = stacks.length - 1;
            final StackTraceElement trace = stacks[skipDepth];
            String debugGetClassTag = trace.getFileName();
            String releaseGetClassTag = trace.getClassName();
            //debug模式下使用FileName作为tag名
            if (LOG_DEFALUT_CONFIG.getCurMode()) {
                if (!TextUtils.isEmpty(debugGetClassTag)) {
                    scopeTag = debugGetClassTag;
                }
            } else {
                if (!TextUtils.isEmpty(releaseGetClassTag)) {
                    scopeTag = releaseGetClassTag;
                }
            }
        } catch (Exception e) {
        }
        return scopeTag;
    }

    //======================================== Upload ==============================================

    //send log feed back
    public static void send(final long stamp, final int mode, final boolean force, final boolean carry) {
        FeedBackUTask task = new FeedBackUTask.Builder()
                .setCurTimeStamp(stamp)//设置时间戳
                .setMode(mode)//设置上传网络模式
                .setForce(force)//设置是否强制上传
                .build();//开始构建任务实体
        LogSendProxy.getInstance().runTask(task);
    }

    //retry when app start
    public static void retryStartUp() {
        StartRetryUTask task = new StartRetryUTask.Builder().build();
        LogSendProxy.getInstance().runTask(task);
    }

    //retry when network get
    public static void retryLostNet() {
        String[] queueA = RealSendRunnable.CACHE_SET.toArray(new String[]{});
        RealSendRunnable.CACHE_SET.clear();//避免Runable检测到正常网络重复上传
        NetRetryUTask task = new NetRetryUTask.Builder()
                .setNeedReUpload(queueA)//设置重传路径数组
                .build();//开始构建任务实体
        LogSendProxy.getInstance().runTask(task);
    }

    //======================================= Logan Call ===========================================

    public static void flush() {
        if (LOG_DEFALUT_CONFIG.getMeituLoganInit()) {
            Logan.f();
        }
    }

    public static void write(String log, int type) {
        if (LOG_DEFALUT_CONFIG.getMeituLoganInit()) {
            Logan.w(log, type);
        }
    }

    public static void open(IFileReOpenCallback callback) {
        if (LOG_DEFALUT_CONFIG.getMeituLoganInit()) {
            Logan.r(callback);
        }
    }

    public static void arrange(IFileArrangeCallback callback) {
        if (LOG_DEFALUT_CONFIG.getMeituLoganInit()) {
            Logan.a(callback);
        }
    }

    //========================================= UID ================================================
    public static long USER_ID = LogzConstant.DEFAULT_UID;
    public static String DEVICE_ID = LogzConstant.DEFAULT_DID;

    public static void setLogHUid(long uid) {
        USER_ID = uid;
        Logz.tag(LOGAN_TAG).i("Logan set userid : %s in memory success!", String.valueOf(uid));
    }

    public static long getLogHUid() {
        return USER_ID;
    }

    public static void setLogHDeviceId(String did) {
        DEVICE_ID = did;
        Logz.tag(LOGAN_TAG).i("Logan set deviceid : %s in memory success!", String.valueOf(did));
    }

    public static String getLogHDeviceId() {
        return DEVICE_ID;
    }
}
