package com.digo.platform.logan.mine.upload;

import android.os.Build;
import android.text.TextUtils;

import com.digo.platform.logan.mine.Logz;
import com.digo.platform.logan.mine.utils.MD5BuilderUtils;
import com.digo.platform.logan.mine.utils.SystemInfoUtils;

import java.io.File;
import java.io.IOException;

/**
 * Author : Create by Linxinyuan on 2018/11/27
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */

public final class HttpPostUrlBuilder {
    //服务器Http-Post上传Lizhi日志域名前地址
    public static String LOVEST_LOG_UPLOAD_URL_HEADER = "https://apprds.lizhifm.com/log_upload";

    /**
     * Log-Url拼接方法
     *
     * @param logFile
     * @return
     */
    public static String buildLizhiLogUploadUrl(File logFile, String tag) {
        try {
            String reqUrlParmas = LOVEST_LOG_UPLOAD_URL_HEADER + "?deviceId=" + Logz.getLogHDeviceId()
                    + "&phoneModel=" + SystemInfoUtils.getSystemModel()
                    + "&appVer=" + SystemInfoUtils.getAppVersion()
                    + "&systemVer=" + Build.VERSION.RELEASE
                    + "&platform=android"
                    + "&appKey=" + (TextUtils.isEmpty(Logz.getLogConfiger().getLoganAppKey()) ? "Lovest" : Logz.getLogConfiger().getLoganAppKey())
                    + "&md5=" + MD5BuilderUtils.getFileMD5String(logFile);
            if (Logz.getLogConfiger().getCurrentMode()) {
                //debug参数为1-releaselog/debug包日志上传到测试环境
                return reqUrlParmas + "&debug=1";
            } else {
                //debug参数为0-release包日志上传到正式环境
                return reqUrlParmas + "&debug=0";
            }
        } catch (IOException e) {
            Logz.e(e.toString());
        }
        return "";
    }
}
