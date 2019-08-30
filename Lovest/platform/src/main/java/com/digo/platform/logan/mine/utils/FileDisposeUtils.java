package com.digo.platform.logan.mine.utils;

import android.text.TextUtils;

import com.digo.platform.logan.mine.Logz;
import com.digo.platform.logan.mine.database.daos.LoganUFileDao;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.digo.platform.logan.mine.common.LogzConstant.LOGAN_TAG;

/**
 * Author : Create by Linxinyuan on 2018/11/26
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */
public class FileDisposeUtils {
    private static final int BUFFEREDSIZE = 1024;

    /**
     * 目标路径是否存在
     *
     * @param path
     * @return
     */
    public static boolean isFileExist(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * 目标文件夹是否存在
     *
     * @param paths
     * @return
     */
    public static void checkPathFileExist(String[] paths) {
        for (String path : paths) {
            try {
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdirs();
                }
            } catch (Exception e) {
                Logz.e(e.toString());
            }
        }
    }

    /**
     * 目标路径是否是文件夹
     *
     * @param path
     * @return
     */
    public static boolean isDirectory(String path) {
        File file = new File(path);
        return file.isDirectory();
    }

    /**
     * Sdk文件最大限制
     *
     * @param path
     * @param maxSize
     * @return
     */
    public static boolean reachZipConfigMax(String path, long maxSize) {
        File zip = new File(path);
        return (zip.length() > maxSize);
    }

    /**
     * 文件拷贝到固定路径并执行Zip压缩
     *
     * @param mutiplyPaths 准备执行压缩的批文件
     * @param zipFilePath  生成的Zip路径
     * @return
     */
    public static boolean zipSDKLogFile(String zipFilePath, List<String> mutiplyPaths) {
        if (mutiplyPaths == null || mutiplyPaths.size() == 0)
            return false;

        boolean zipResult = false;
        ZipOutputStream out = null;
        BufferedOutputStream bo = null;
        try {
            File zipFile = new File(zipFilePath);
            if (!zipFile.exists()) {
                zipFile.createNewFile();
            }
            out = new ZipOutputStream(new FileOutputStream(zipFilePath));
            bo = new BufferedOutputStream(out);
            zipResult = zip(out, bo, mutiplyPaths);
            bo.close();
            out.close();

            return zipResult;
        } catch (Exception e) {
            Logz.tag(LOGAN_TAG).e(e.toString());
        }
        return zipResult;
    }

    /**
     * 非递归处理多文件联和压缩
     *
     * @param out
     * @param bo
     * @param mutiplyPaths
     * @return
     */
    private static boolean zip(ZipOutputStream out, BufferedOutputStream bo, List<String> mutiplyPaths) {
        try {
            for (String path : mutiplyPaths) {
                File waitingZipFile = new File(path);
                if (!waitingZipFile.exists())
                    continue;

                // 创建zip压缩进入点base
                out.putNextEntry(new ZipEntry(waitingZipFile.getName()));
                FileInputStream in = new FileInputStream(waitingZipFile);
                int len;
                byte[] buff = new byte[BUFFEREDSIZE];
                while ((len = in.read(buff)) != -1) {
                    out.write(buff, 0, len);
                }
                out.closeEntry();
                in.close();
            }
            return true;
        } catch (IOException e) {
            Logz.tag(LOGAN_TAG).e(e.toString());
        }
        return false;
    }

    /**
     * 根据路径进行文件删除
     *
     * @param zipFilePath
     */
    public static void deleteFileByPath(String zipFilePath) {
        if (!TextUtils.isEmpty(zipFilePath)) {
            File waitDelete = new File(zipFilePath);
            if (waitDelete.exists())
                waitDelete.delete();
        }
    }

    /**
     * 根据zip的绝对路径获取打包的tag
     *
     * @param zipAbsPath
     * @return
     */
    public static String getZipFileTag(String zipAbsPath) {
        String tag = null;
        try {
            if (!TextUtils.isEmpty(zipAbsPath)) {
                String[] pathSplit = zipAbsPath.split("/");
                if (pathSplit != null && pathSplit.length > 0) {
                    String zipName = pathSplit[pathSplit.length - 1];
                    String[] nameSplit = zipName.split("\\.");
                    if (nameSplit != null && nameSplit.length > 0)
                        tag = nameSplit[0];
                }
            }
        } catch (Exception e) {
            Logz.tag(LOGAN_TAG).e(e.toString());
        }
        return tag;
    }

    /**
     * 过滤Lizhi-Logan生成的日志文件上传任务
     *
     * @param needReUpload
     * @return
     */
    public static String[] filterLizhiRetryItem(String[] needReUpload) {
        List<String> filterResult = new ArrayList<>();
        for (int i = 0; i < needReUpload.length; i++) {
            if (!needReUpload[i].contains(".zip")) {
                if (new File(needReUpload[i]).exists()) {
                    filterResult.add(needReUpload[i]);
                } else {
                    //找不到该文件直接删除数据库作废记录
                    LoganUFileDao.getInstance(Logz.getContext()).syncDeleteOnlyPath(needReUpload[i]);
                }
            }
        }
        return (filterResult.size() > 0) ? filterResult.toArray(new String[filterResult.size()]) : null;
    }

    /**
     * 过滤Lizhi-第三方Sdk生成的日志文件上传任务
     *
     * @param needReUpload
     * @return
     */
    public static String[] filterSdkZipRetryItem(String[] needReUpload) {
        List<String> filterResult = new ArrayList<>();
        for (int i = 0; i < needReUpload.length; i++) {
            if (needReUpload[i].contains(".zip")) {
                if (new File(needReUpload[i]).exists()) {
                    filterResult.add(needReUpload[i]);
                } else {
                    //找不到该文件直接删除数据库作废记录
                    LoganUFileDao.getInstance(Logz.getContext()).syncDeleteOnlyPath(needReUpload[i]);
                }
            }
        }
        return (filterResult.size() > 0) ? filterResult.toArray(new String[filterResult.size()]) : null;
    }
}
