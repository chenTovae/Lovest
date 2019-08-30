package com.digo.platform.logan.mine.upload;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.digo.platform.logan.mine.Logz;
import com.digo.platform.logan.mine.common.LogzConstant;
import com.digo.platform.logan.meituan.util.LoganUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Author : Create by Linxinyuan on 2018/10/31
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */
public class LogSendHelper {
    /**
     * 初始化状态检测
     *
     * @return
     */
    public static boolean checkDueUploadBefore() {
        return Logz.getLogConfiger().getMeituLoganInit() && Logz.getLogConfiger().getAppConfigUpload();
    }

    /**
     * 获取上传文件列表
     *
     * @return
     */
    public static String[] collectFileNeedUpload(long timeStamp) {
        //遍历符合上传时间范围的文件夹
        String rootPath = Logz.getLogConfiger().getSavePath();
        //2018-11-22与2018-11-21
        String dateStr = LoganUtil.getDateStr(timeStamp);
        String dateBeforeStr = LoganUtil.getDateStr(timeStamp - LogzConstant.DAY_STAMP_LONG);
        File rootFile = new File(rootPath);
        //如果主文件夹不存在创建新的
        if (!rootFile.exists()) {
            rootFile.mkdirs();
            return null;
        }
        List<String> needUploadPath = new ArrayList<>();
        File[] childs = rootFile.listFiles();
        if (childs.length > 0) {
            for (File f : childs) {
                //上传前一天与今天的日志文件
                if (f.isDirectory() && (f.getName().equals(dateStr) || f.getName().equals(dateBeforeStr))) {
                    File[] depotChild = f.listFiles();
                    for (File c : depotChild) {
                        needUploadPath.add(c.getAbsolutePath());
                    }
                }
            }
        }
        return (needUploadPath.size() > 0) ? needUploadPath.toArray(new String[needUploadPath.size()]) : null;
    }

    /**
     * 获取上传文件列表
     *
     * @param start
     * @param end
     * @return
     */
    public static String[] collectFileNeedUpload(long start, long end) {
        //遍历符合上传时间范围的文件夹
        String rootPath = Logz.getLogConfiger().getSavePath();
        File rootFile = new File(rootPath);
        if (!rootFile.exists()) {
            rootFile.mkdirs();
        }
        List<String> needUploadPath = new ArrayList<>();
        File[] childs = rootFile.listFiles();
        if (childs.length > 0) {
            for (File f : childs) {
                if (f.isDirectory() && checkIsNeedUpload(start, end, f.getName(), true)) {
                    File[] depotChild = f.listFiles();
                    for (File c : depotChild) {
                        needUploadPath.add(c.getAbsolutePath());
                    }
                }
            }
        }
        return (needUploadPath.size() > 0) ? needUploadPath.toArray(new String[needUploadPath.size()]) : null;
    }

    /**
     * 判断文件是否需要上传
     *
     * @param start
     * @param end
     * @param fileName
     * @param isDir
     * @return
     */
    public static boolean checkIsNeedUpload(long start, long end, String fileName, boolean isDir) {
        boolean isNeed = false;
        try {
            if (isDir) {
                long ts = LoganUtil.getDateLong(fileName) / 1000;
                isNeed = (start <= ts && ts <= end);
            } else {
                long ts = Long.parseLong(fileName) / 1000;
                isNeed = (start <= ts && ts <= end);
            }
        } catch (Exception e) {
            Logz.e(e.toString());
        }
        return isNeed;
    }

    /**
     * 获取当前网络是否可用
     *
     * @return
     */
    public static boolean checkNetworkAvailable() {
        if (Logz.getContext() != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) Logz.getContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
