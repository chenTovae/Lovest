package com.digo.platform.logan.meituan.action;


import com.digo.platform.logan.meituan.route.IFileArrangeCallback;

/**
 * Author : Create by Linxinyuan on 2018/10/30
 * Email : linxinyuan@lizhi.fm
 * Desc :
 * isStartUp变量的作用
 * 1.启动不做文件移动处理，上传的时候进行整理(规避多线程问题)
 * 2.行为表现为主文件夹下同时存在某一天的文件夹与当天最后一个文件
 */
public class ArrangeAction {
    public IFileArrangeCallback callback;

    public boolean isValid() {
        boolean valid = false;
        if (callback != null) {
            valid = true;
        }
        return valid;
    }
}
