package com.digo.platform.logan.mine.tree;

/**
 * Author : Create by Linxinyuan on 2018/08/02
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */
public interface ITree {
    //Log.VERBOSE
    void v(Throwable t, String message, Object... args);
    void v(String message, Object... args);
    void v(Throwable t);
    void v(Object o);

    //Log.DEBUG
    void d(Throwable t, String message, Object... args);
    void d(String message, Object... args);
    void d(Throwable t);
    void d(Object o);

    //Log.INFO
    void i(Throwable t, String message, Object... args);
    void i(String message, Object... args);
    void i(Throwable t);
    void i(Object o);

    //Log.WRAN
    void w(Throwable t, String message, Object... args);
    void w(String message, Object... args);
    void w(Throwable t);
    void w(Object o);

    //Log.ERROR
    void e(Throwable t, String message, Object... args);
    void e(String message, Object... args);
    void e(Throwable t);
    void e(Object o);

    //Log.ASSERT
    void wtf(Throwable t, String message, Object... args);
    void wtf(String message, Object... args);
    void wtf(Throwable t);
    void wtf(Object o);

    //Log.CUSTOM
    void log(int priority, Throwable t, String message, Object... args);
    void log(int priority, String message, Object... args);
    void log(int priority, Throwable t);
    void log(int priority, Object o);

    //Log.SPECIAL
    void json(String j);
    void xml(String x);
}
