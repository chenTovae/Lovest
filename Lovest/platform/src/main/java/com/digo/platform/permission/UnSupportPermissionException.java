package com.digo.platform.permission;

/**
 * Author : Create by Linxinyuan on 2019/03/26
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */
public class UnSupportPermissionException extends Exception{
    private String[] unSupport;

    public UnSupportPermissionException() {
        super();
    }

    public UnSupportPermissionException(String msg, String[] unSupport) {
        super(msg);
        this.unSupport = unSupport;
    }

    public String[] getUnSupport() {
        return unSupport;
    }
}
