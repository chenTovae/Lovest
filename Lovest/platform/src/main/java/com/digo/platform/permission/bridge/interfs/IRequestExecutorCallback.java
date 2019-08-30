package com.digo.platform.permission.bridge.interfs;

/**
 * Author : Create by Linxinyuan on 2019/03/12
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */
public interface IRequestExecutorCallback {
    void onExecute();

    void onCancel(String[] pers);
}
