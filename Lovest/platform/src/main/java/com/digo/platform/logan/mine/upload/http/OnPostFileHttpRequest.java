package com.digo.platform.logan.mine.upload.http;

import com.digo.platform.logan.mine.upload.bean.HttpPostFileModel;

/**
 * Author : Create by Linxinyuan on 2018/11/27
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */
public interface OnPostFileHttpRequest {
    void onPostSuccess(HttpPostFileModel httpPostFileModel);

    void onPostFail(HttpPostFileModel httpPostFileModel, String exception);
}
