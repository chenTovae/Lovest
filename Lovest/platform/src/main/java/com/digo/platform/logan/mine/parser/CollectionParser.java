package com.digo.platform.logan.mine.parser;

import com.digo.platform.logan.mine.common.LogzConstant;
import com.digo.platform.logan.mine.common.LogzConvert;
import com.digo.platform.logan.mine.config.ILogzConfig;

import java.util.Collection;
import java.util.Iterator;

/**
 * Author : Create by Linxinyuan on 2018/08/03
 * Email : linxinyuan@lizhi.fm
 * Desc : Collection接口对象解析器
 */
public class CollectionParser implements IParser<Collection> {
    @Override
    public Class parseClassType() {
        return Collection.class;
    }

    @Override
    public String parseString(ILogzConfig configer, Collection collection) {
        String simpleName = collection.getClass().getName();
        String msg = "%s size = %d" + LogzConstant.BR;
        msg = String.format(msg, simpleName, collection.size());
        if (!collection.isEmpty()) {
            Iterator iterator = collection.iterator();
            int flag = 0;
            while (iterator.hasNext()) {
                String itemString = "[%d]:%s%s";
                Object item = iterator.next();
                msg += String.format(itemString, flag, LogzConvert.objectToString(configer, item),
                        flag++ < collection.size() - 1 ? "," + LogzConstant.BR : LogzConstant.BR);
            }
        }
        return msg;
    }
}
