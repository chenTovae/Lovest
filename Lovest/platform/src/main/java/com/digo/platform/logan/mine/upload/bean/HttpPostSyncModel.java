package com.digo.platform.logan.mine.upload.bean;

/**
 * Author : Create by Linxinyuan on 2019/01/18
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */
public class HttpPostSyncModel {
    private String httpUrl;

    public HttpPostSyncModel() {
        //Class Default Constructor
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public static final class Builder {
        private String httpUrl;

        public Builder setHttpUrl(String httpUrl) {
            this.httpUrl = httpUrl;
            return this;
        }

        public HttpPostSyncModel build() {
            HttpPostSyncModel httpPostSyncModel = new HttpPostSyncModel();
            httpPostSyncModel.setHttpUrl(httpUrl);
            return httpPostSyncModel;
        }
    }
}
