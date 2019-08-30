package com.digo.platform.logan.mine.tree;

import com.digo.platform.logan.mine.base.Tree;
import com.digo.platform.logan.mine.config.LogzConfiger;

/**
 * Author : Create by Linxinyuan on 2018/08/02
 * Email : linxinyuan@lizhi.fm
 * Desc : 灵魂之树(采用代理模式进行日志往各个渠道的分发)
 */
public class SoulsTree extends Tree {
    private volatile Tree[] forestAsArray = new Tree[0];

    public Tree tag(String tag) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].setTag(tag);
        }
        return this;
    }

    @Override
    public void v(String message, Object... args) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].v(message, args);
        }
    }

    @Override
    public void v(Throwable t, String message, Object... args) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].v(t, message, args);
        }
    }

    @Override
    public void v(Throwable t) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].v(t);
        }
    }

    @Override
    public void v(Object o) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].v(o);
        }
    }

    @Override
    public void d(String message, Object... args) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].d(message, args);
        }
    }

    @Override
    public void d(Throwable t, String message, Object... args) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].d(t, message, args);
        }
    }

    @Override
    public void d(Throwable t) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].d(t);
        }
    }

    @Override
    public void d(Object o) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].d(o);
        }
    }

    @Override
    public void i(String message, Object... args) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].i(message, args);
        }
    }

    @Override
    public void i(Throwable t, String message, Object... args) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].i(t, message, args);
        }
    }

    @Override
    public void i(Throwable t) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].i(t);
        }
    }

    @Override
    public void i(Object o) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].i(o);
        }
    }

    @Override
    public void w(String message, Object... args) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].w(message, args);
        }
    }

    @Override
    public void w(Throwable t, String message, Object... args) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].w(t, message, args);
        }
    }

    @Override
    public void w(Throwable t) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].w(t);
        }
    }

    @Override
    public void w(Object o) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].w(o);
        }
    }

    @Override
    public void e(String message, Object... args) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].e(message, args);
        }
    }

    @Override
    public void e(Throwable t, String message, Object... args) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].e(t);
        }
    }

    @Override
    public void e(Throwable t) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].e(t);
        }
    }

    @Override
    public void e(Object o) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].e(o);
        }
    }

    @Override
    public void wtf(String message, Object... args) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].wtf(message, args);
        }
    }

    @Override
    public void wtf(Throwable t, String message, Object... args) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].wtf(t, message, args);
        }
    }

    @Override
    public void wtf(Throwable t) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].wtf(t);
        }
    }

    @Override
    public void wtf(Object o) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].wtf(o);
        }
    }

    @Override
    public void json(String j) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].json(j);
        }
    }

    @Override
    public void xml(String x) {
        Tree[] forest = forestAsArray;
        for (int i = 0; i < forest.length; i++) {
            forest[i].xml(x);
        }
    }

    public ITree[] getForestAsArray() {
        return forestAsArray;
    }

    public void setForestAsArray(Tree[] forestAsArray) {
        this.forestAsArray = forestAsArray;
    }

    @Override
    protected void log(int type, String tag, String message) {
        throw new AssertionError("Missing override for log method.");
    }

    @Override
    protected LogzConfiger configer() {
        return null;//default configer
    }
}
