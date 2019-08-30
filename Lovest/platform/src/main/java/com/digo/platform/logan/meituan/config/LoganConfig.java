/*
 * Copyright (c) 2018-present, 美团点评
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.digo.platform.logan.meituan.config;

import android.text.TextUtils;

public class LoganConfig {
    private static final long DAYS = 24 * 60 * 60 * 1000; //天
    private static final long M = 1024 * 1024; //M(以B为起始单位)
    private static final long DEFAULT_DAY = 7 * DAYS; //默认删除天数
    private static final long DEFAULT_FILE_SIZE = 10 * M;
    private static final long DEFAULT_MIN_SDCARD_SIZE = 50 * M; //最小的SD卡小于这个大小不写入
    private static final int DEFAULT_QUEUE = 500;
    private static final int DEFAULT_DEPOT_FILE_NUM = 10;//单一天仓库可容纳的最大切片数量
    private static final long DEFAULT_SIMPLE_SIZE = 2 * M;//最大切片大小

    boolean isMainProcess;//是否是主进程

    String mCachePath; //mmap缓存路径
    String mPathPath; //file文件路径

    long mMaxFile = DEFAULT_FILE_SIZE; //写文件最大值
    long mDay = DEFAULT_DAY; //删除天数
    long mMaxQueue = DEFAULT_QUEUE;
    long mMinSDCard = DEFAULT_MIN_SDCARD_SIZE; //最小sdk卡大小

    long mSimpleSize = DEFAULT_SIMPLE_SIZE;//单个分片的最大限制
    int mDepotFileNum = DEFAULT_DEPOT_FILE_NUM;//单天仓库的日志文件数量

    byte[] mEncryptKey16; //16位ase加密Key
    byte[] mEncryptIv16; //16位aes加密IV

    boolean isValid() {
        boolean valid = false;
        if (!TextUtils.isEmpty(mCachePath) && !TextUtils.isEmpty(mPathPath) && mEncryptKey16 != null
                && mEncryptIv16 != null) {
            valid = true;
        }
        return valid;
    }

    LoganConfig() {

    }

    public void setCachePath(String cachePath) {
        mCachePath = cachePath;
    }

    public void setPathPath(String pathPath) {
        mPathPath = pathPath;
    }

    public void setMaxFile(long maxFile) {
        mMaxFile = maxFile;
    }

    public void setDay(long day) {
        mDay = day;
    }

    public void setMinSDCard(long minSDCard) {
        mMinSDCard = minSDCard;
    }

    public void setEncryptKey16(byte[] encryptKey16) {
        mEncryptKey16 = encryptKey16;
    }

    public void setEncryptIV16(byte[] encryptIv16) {
        mEncryptIv16 = encryptIv16;
    }

    public void setmSimpleSize(long mSimpleSize) {
        this.mSimpleSize = mSimpleSize;
    }

    public void setmDepotFileNum(int mDepotFileNum) {
        this.mDepotFileNum = mDepotFileNum;
    }

    public void setIsMainProcess(boolean isMain){
        this.isMainProcess = isMain;
    }

    public static final class Builder {
        boolean isMainProcess;//是否是主进程
        String mCachePath; //mmap缓存路径
        String mPath; //file文件路径
        long mMaxFile = DEFAULT_FILE_SIZE; //写文件最大值
        long mDay = DEFAULT_DAY; //删除天数
        byte[] mEncryptKey16; //16位ase加密Key
        byte[] mEncryptIv16; //16位aes加密IV
        long mMinSDCard = DEFAULT_MIN_SDCARD_SIZE;
        long mSimpleSize = DEFAULT_SIMPLE_SIZE;
        int mDepotFileNum = DEFAULT_DEPOT_FILE_NUM;

        public Builder setIsMainProcess(boolean isMain) {
            isMainProcess = isMain;
            return this;
        }

        public Builder setCachePath(String cachePath) {
            mCachePath = cachePath;
            return this;
        }

        public Builder setPath(String path) {
            mPath = path;
            return this;
        }

        public Builder setMaxFile(long maxFile) {
            mMaxFile = maxFile * M;
            return this;
        }

        public Builder setDay(long day) {
            mDay = day * DAYS;
            return this;
        }

        public Builder setEncryptKey16(byte[] encryptKey16) {
            mEncryptKey16 = encryptKey16;
            return this;
        }

        public Builder setEncryptIV16(byte[] encryptIv16) {
            mEncryptIv16 = encryptIv16;
            return this;
        }

        public Builder setMinSDCard(long minSDCard) {
            mMinSDCard = minSDCard * M;
            return this;
        }

        public Builder setmSimpleSize(long simpleSize) {
            mSimpleSize = simpleSize;
            return this;
        }

        public Builder setmDepotFileNum(int depotFileNum) {
            mDepotFileNum = depotFileNum;
            return this;
        }

        public LoganConfig build() {
            LoganConfig config = new LoganConfig();
            config.setCachePath(mCachePath);
            config.setPathPath(mPath);
            config.setMaxFile(mMaxFile);
            config.setMinSDCard(mMinSDCard);
            config.setDay(mDay);
            config.setEncryptKey16(mEncryptKey16);
            config.setEncryptIV16(mEncryptIv16);
            config.setmDepotFileNum(mDepotFileNum);
            config.setmSimpleSize(mSimpleSize);
            config.setIsMainProcess(isMainProcess);
            return config;
        }
    }
}
