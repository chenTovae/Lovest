package com.digo.platform.logan.mine.combine;

/**
 * Author : Create by Linxinyuan on 2018/10/23
 * Email : linxinyuan@lizhi.fm
 * Desc : 日志写文件json格式化实体类
 */
public class SimpleLogout {
    private String msg = "Null";//消息内容
    private String tag = "Null";//功能标签
    private String lv = "Null";//日志等级

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getLv() {
        return lv;
    }

    public void setLv(String lv) {
        this.lv = lv;
    }

    @Override
    public String toString() {
        return "SimpleLogout{" +
                "msg='" + msg + '\'' +
                ", tag='" + tag + '\'' +
                ", lv='" + lv + '\'' +
                '}';
    }
}
