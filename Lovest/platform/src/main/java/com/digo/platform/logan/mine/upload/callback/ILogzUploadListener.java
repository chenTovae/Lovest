package com.digo.platform.logan.mine.upload.callback;

/**
 * Author : Create by Linxinyuan on 2019/01/14
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */
public interface ILogzUploadListener {
    /**
     * 上传开始回调接口
     *
     * @param url  上传的url地址
     * @param path 上传文件的本地绝对路径
     */
    void onLogUploadStart(String url, String path);

    /**
     * 上传成功回调接口
     *
     * @param url  上传的url地址
     * @param path 上传的本地文件绝对路径
     */
    void onLogUploadSuccess(String url, String path);

    /**
     * 上传失败回调接口
     *
     * @param url  上传的url地址
     * @param path 上传文件的本地绝对路径
     * @param e    上传异常堆栈信息
     */
    void onLogUploadFailure(String url, String path, String e);
}
