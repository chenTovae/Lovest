package com.digo.platform.logan.meituan.action;

import com.digo.platform.logan.meituan.route.IFileReOpenCallback;

/**
 * Author : Create by Linxinyuan on 2018/10/30
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */
public class ReOpenAction {
    public IFileReOpenCallback callback;

    public boolean isValid() {
        boolean valid = false;
        if (callback != null) {
            valid = true;
        }
        return valid;
    }
}
