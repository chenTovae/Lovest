package com.digo.platform.logan.mine.database.tables;

/**
 * Author : Create by Linxinyuan on 2018/10/19
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */
public class LoganUFileModel {
    private int id;//文件ID
    private String name;//文件名
    private String path;//文件路径
    private int retry;//失败次数，每失败一次加1
    private int status;//0未上传，1已上传，-1上传失败

    public static final int DEFAULT_RETRY = 0;
    public static final int DEFAULT_STATUS = 0;

    public LoganUFileModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "LoganUFileModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", retry=" + retry +
                ", status=" + status +
                '}';
    }
}
