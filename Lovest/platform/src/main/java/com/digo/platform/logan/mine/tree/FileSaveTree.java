package com.digo.platform.logan.mine.tree;

import android.text.TextUtils;

import com.digo.platform.logan.meituan.Logan;
import com.digo.platform.logan.mine.Logz;
import com.digo.platform.logan.mine.base.Tree;
import com.digo.platform.logan.mine.combine.SimpleLogout;
import com.digo.platform.logan.mine.common.LogzConstant;
import com.digo.platform.logan.mine.config.ILogzConfig;
import com.digo.platform.logan.mine.config.LogzConfiger;
import com.digo.platform.logan.mine.utils.ConverLevelUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;


/**
 * Author : Create by Linxinyuan on 2018/08/02
 * Email : linxinyuan@lizhi.fm
 * Desc : 输出到File的日志树节点(默认日志输出级别为INFO)
 */
public class FileSaveTree extends Tree {
    private Gson mGson;

    public FileSaveTree() {
        //FileSaveTree Constructor
        mGson = new GsonBuilder().disableHtmlEscaping().create();
    }

    @Override
    protected ILogzConfig configer() {
        return new LogzConfiger()
                //app-config配置是否允许输出,默认是false
                .configAllowLog(Logz.getLogConfiger().getAppConfigSave())
                //app-config配置输出最小级别,默认是Info
                .configMimLogLevel(Logz.getLogConfiger().getAppConfigLevel());
    }

    @Override
    protected void log(int type, String tag, String msg) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        writeLog2FileLogan(getMsgJsonForma(type, tag, msg));
    }

    private String getMsgJsonForma(int type, String tag, String msg) {
        String msgJson = msg;
        try {
            SimpleLogout sl = new SimpleLogout();
            sl.setTag(tag);
            sl.setMsg(msg);
            sl.setLv(ConverLevelUtils.IntLevel2String(type));
            msgJson = mGson.toJson(sl, SimpleLogout.class);
        } catch (NullPointerException e) {
            Logz.e(e.toString());
        } catch (JsonParseException e) {
            Logz.e(e.toString());
        }
        return msgJson;
    }

    private void writeLog2FileLogan(String msgJson) {
        if (!TextUtils.isEmpty(msgJson) && Logz.getLogConfiger().getMeituLoganInit()) {
            try {
                //use logan to out put msg-json
                Logan.w(msgJson, LogzConstant.LOGAN_TYPE);
            } catch (Exception e) {
                Logz.e(e.toString());
            }
        }
    }
}