package com.digo.platform.logan.mine.upload.bean;

import java.io.File;

/**
 * Author : Create by Linxinyuan on 2018/11/27
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */
public class HttpPostFileModel {
    private int retry;
    private String tag;
    private File logFile;
    private String httpUrl;

    public HttpPostFileModel() {
        //Class Default Constructor
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setLogFile(File logFile) {
        this.logFile = logFile;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public String getTag() {
        return tag;
    }

    public File getLogFile() {
        return logFile;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public int getRetry() {
        return retry;
    }

    public static final class Builder {
        private int retry;
        private String tag;
        private File logFile;
        private String httpUrl;

        private int priority;

        public Builder setRetry(int retry) {
            this.retry = retry;
            return this;
        }

        public Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder setLogFile(File logFile) {
            this.logFile = logFile;
            return this;
        }

        public Builder setHttpUrl(String httpUrl) {
            this.httpUrl = httpUrl;
            return this;
        }

        public HttpPostFileModel build() {
            HttpPostFileModel httpPostFileModel = new HttpPostFileModel();
            httpPostFileModel.setTag(tag);
            httpPostFileModel.setLogFile(logFile);
            httpPostFileModel.setHttpUrl(httpUrl);
            httpPostFileModel.setRetry(retry);
            return httpPostFileModel;
        }
    }
}
