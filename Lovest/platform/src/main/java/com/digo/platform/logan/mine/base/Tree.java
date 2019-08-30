package com.digo.platform.logan.mine.base;

import android.text.TextUtils;
import android.util.Log;

import com.digo.platform.logan.mine.Logz;
import com.digo.platform.logan.mine.common.LogzConstant;
import com.digo.platform.logan.mine.common.LogzConvert;
import com.digo.platform.logan.mine.config.ILogzConfig;
import com.digo.platform.logan.mine.tree.ITree;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Author : Create by Linxinyuan on 2018/08/02
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */
public abstract class Tree implements ITree {
    private final ThreadLocal<String> localTags = new ThreadLocal<>();
    private ILogzConfig mTLogConfig;//Golbal log output level

    public Tree() {
        //判断使用特性树配置中心或者是全局的日志配置
        mTLogConfig = (configer() == null ? Logz.getLogConfiger() : configer());
    }

    public ILogzConfig getConfiger() {
        return mTLogConfig;
    }

    public ITree setTag(String tag) {
        if (!TextUtils.isEmpty(tag) && mTLogConfig.isEnable() && TextUtils.isEmpty(localTags.get())) {
            localTags.set(tag);
        }
        return this;
    }

    @Override
    public void v(String message, Object... args) {
        prepareLog(Log.VERBOSE, null, message, args);
    }

    @Override
    public void v(Throwable t, String message, Object... args) {
        prepareLog(Log.VERBOSE, t, message, args);
    }

    @Override
    public void v(Throwable t) {
        prepareLog(Log.VERBOSE, t, null);
    }

    @Override
    public void v(Object o) {
        prepareLogObject(Log.VERBOSE, o);
    }

    @Override
    public void d(String message, Object... args) {
        prepareLog(Log.DEBUG, null, message, args);
    }

    @Override
    public void d(Throwable t, String message, Object... args) {
        prepareLog(Log.DEBUG, t, message, args);
    }

    @Override
    public void d(Throwable t) {
        prepareLog(Log.DEBUG, t, null);
    }

    @Override
    public void d(Object o) {
        prepareLogObject(Log.DEBUG, o);
    }

    @Override
    public void i(String message, Object... args) {
        prepareLog(Log.INFO, null, message, args);
    }

    @Override
    public void i(Throwable t, String message, Object... args) {
        prepareLog(Log.INFO, t, message, args);
    }

    @Override
    public void i(Throwable t) {
        prepareLog(Log.INFO, t, null);
    }

    @Override
    public void i(Object o) {
        prepareLogObject(Log.INFO, o);
    }

    @Override
    public void w(String message, Object... args) {
        prepareLog(Log.WARN, null, message, args);
    }

    @Override
    public void w(Throwable t, String message, Object... args) {
        prepareLog(Log.WARN, t, message, args);
    }

    @Override
    public void w(Throwable t) {
        prepareLog(Log.WARN, t, null);
    }

    @Override
    public void w(Object o) {
        prepareLogObject(Log.WARN, o);
    }

    @Override
    public void e(String message, Object... args) {
        prepareLog(Log.ERROR, null, message, args);
    }

    @Override
    public void e(Throwable t, String message, Object... args) {
        prepareLog(Log.ERROR, t, message, args);
    }

    @Override
    public void e(Throwable t) {
        prepareLog(Log.ERROR, t, null);
    }

    @Override
    public void e(Object o) {
        prepareLogObject(Log.ERROR, o);
    }

    @Override
    public void wtf(String message, Object... args) {
        prepareLog(Log.ASSERT, null, message, args);
    }

    @Override
    public void wtf(Throwable t, String message, Object... args) {
        prepareLog(Log.ASSERT, t, message, args);
    }

    @Override
    public void wtf(Throwable t) {
        prepareLog(Log.ASSERT, t, null);
    }

    @Override
    public void wtf(Object o) {
        prepareLogObject(Log.ASSERT, o);
    }

    @Override
    public void log(int priority, String message, Object... args) {
        prepareLog(priority, null, message, args);
    }

    @Override
    public void log(int priority, Throwable t, String message, Object... args) {
        prepareLog(priority, t, message, args);
    }

    @Override
    public void log(int priority, Throwable t) {
        prepareLog(priority, t, null);
    }

    @Override
    public void log(int priority, Object o) {
        prepareLogObject(priority, o);
    }

    @Override
    public void json(String json) {
        if (TextUtils.isEmpty(json)) {
            d("JSON{json is empty}");
            return;
        }
        try {
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                String msg = jsonObject.toString(LogzConstant.JSON_PRINT_INDENT);
                d(msg);
            } else if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                String msg = jsonArray.toString(LogzConstant.JSON_PRINT_INDENT);
                d(msg);
            }
        } catch (JSONException e) {
            e(e.toString());
        }
    }

    @Override
    public void xml(String xml) {
        if (TextUtils.isEmpty(xml)) {
            d("XML{xml is empty}");
            return;
        }
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            d(xmlOutput.getWriter().toString().replaceFirst(">", ">\n"));
        } catch (TransformerException e) {
            e(e.toString());
        }
    }

    private void prepareLogObject(int priority, Object o) {
        prepareLog(priority, null, LogzConvert.objectToString(mTLogConfig, o));
    }

    private void prepareLog(int priority, Throwable t, String message, Object... args) {
        //not allow log output
        if (!mTLogConfig.isEnable()) {
            return;
        }
        //target log level mim than minLogOutputLevel
        if (priority < mTLogConfig.getMimLogLevel()) {
            return;
        }
        //get tag (custom/global/class_name)
        String tagPrefix = generateTag();
        if (TextUtils.isEmpty(tagPrefix)) {
            return;
        }
        //get combine log msg
        message = getCombineLogMsg(t, message, args);
        if (TextUtils.isEmpty(message)) {
            return;
        }
        //do not need cut message
        if (message.length() >= LogzConstant.LINE_MAX) {
            try {
                if (mTLogConfig.isShowBorder()) {
                    printLog(priority, tagPrefix, LogzConvert.printDividingLine(LogzConstant.DIVIDER_TOP));
                    printLog(priority, tagPrefix, LogzConvert.printDividingLine(LogzConstant.DIVIDER_NORMAL) + tagPrefix);
                    printLog(priority, tagPrefix, LogzConvert.printDividingLine(LogzConstant.DIVIDER_CENTER));
                    String[] subArray = message.split(LogzConstant.BR);
                    if (subArray != null && subArray.length > 0) {
                        for (String sub : subArray) {
                            printLog(priority, tagPrefix, LogzConvert.printDividingLine(LogzConstant.DIVIDER_NORMAL) + sub);
                        }
                    }
                    printLog(priority, tagPrefix, LogzConvert.printDividingLine(LogzConstant.DIVIDER_BOTTOM));
                } else {
                    if (message != null) {
                        printLog(priority, tagPrefix, message);
                    }
                }
            } catch (Exception e) {
                e(e.toString());
            } finally {
                return;
            }
        }
        // split by line, then ensure each line can fit into Log's maximum length.
        if (message.length() < LogzConstant.LINE_MAX) {
            try {
                List<String> subList = getSplitMessageList(message);
                if (mTLogConfig.isShowBorder()) {
                    printLog(priority, tagPrefix, LogzConvert.printDividingLine(LogzConstant.DIVIDER_TOP));
                    printLog(priority, tagPrefix, LogzConvert.printDividingLine(LogzConstant.DIVIDER_NORMAL) + tagPrefix);
                    printLog(priority, tagPrefix, LogzConvert.printDividingLine(LogzConstant.DIVIDER_CENTER));
                    if (subList != null && subList.size() > 0) {
                        for (String sub : subList) {
                            printLog(priority, tagPrefix, LogzConvert.printDividingLine(LogzConstant.DIVIDER_NORMAL) + sub);
                        }
                    }
                    printLog(priority, tagPrefix, LogzConvert.printDividingLine(LogzConstant.DIVIDER_BOTTOM));
                } else {
                    if (subList != null && subList.size() > 0) {
                        for (String sub : subList) {
                            printLog(priority, tagPrefix, sub);
                        }
                    }
                }
            } catch (Exception e) {
                e(e.toString());
            } finally {
                return;
            }
        }
    }

    private List<String> getSplitMessageList(String message) {
        List<String> stringList = new ArrayList<>();
        try {
            for (int i = 0, length = message.length(); i < length; i++) {
                int newline = message.indexOf('\n', i);
                newline = newline != -1 ? newline : length;
                do {
                    int end = Math.min(newline, i + LogzConstant.LINE_MAX);
                    String part = message.substring(i, end);
                    stringList.add(part);
                    i = end;
                } while (i < newline);
            }
        } catch (Exception e) {
            e(e.toString());
        }
        return stringList;
    }

    // custom > global > class
    private String generateTag() {
        String tempTag = "Lovest_Logz";
        try {
            //custom tag(用户Log输出自定义tag)
            //可能是用户自定义也有可能是上层处理的默认类名
            if (!TextUtils.isEmpty(localTags.get())){
                tempTag = localTags.get();
                localTags.remove();
            }
        } catch (Exception e) {
            return "Lovest_Logz";
        }
        return tempTag;
    }

    private String getCombineLogMsg(Throwable t, String message, Object... args) {
        try {
            if (TextUtils.isEmpty(message)) {
                if (null == t)
                    return null;
                return getThrowable2String(t);
            } else {
                if (args != null && args.length > 0) {
                    message = String.format(message, args);
                }
                if (t != null) {
                    message += "\n" + getThrowable2String(t);
                }
            }
        } catch (Exception e) {
            return message;
        }
        return message;
    }

    private String getThrowable2String(Throwable t) {
        StringWriter sw = new StringWriter(256);
        PrintWriter pw = new PrintWriter(sw, false);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    private void printLog(int priority, String tag, String message) {
        log(priority, tag, message);
    }

    protected abstract ILogzConfig configer();//return null if you want to use global config

    protected abstract void log(int type, String tag, String message);
}
