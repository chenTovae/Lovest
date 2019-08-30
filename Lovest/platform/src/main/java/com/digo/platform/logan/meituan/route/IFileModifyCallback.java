package com.digo.platform.logan.meituan.route;

/**
 * Author : Create by Linxinyuan on 2018/10/25
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */
public interface IFileModifyCallback {
    void onSyncFileDelete(String fn, String path);//同步上传库文件删除信息

    void onSyncFileDeleteOnlyPath(String path);//同步上传库文件删除信息

    int onQueryFileRetryTime(String fn, String path);//查询某个文件的重试次数
}
