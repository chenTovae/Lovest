package com.digo.platform.logan.mine.parser;

import com.digo.platform.logan.mine.common.LogzConstant;
import com.digo.platform.logan.mine.common.LogzConvert;
import com.digo.platform.logan.mine.config.ILogzConfig;

import java.util.Map;
import java.util.Set;

/**
 * Author : Create by Linxinyuan on 2018/08/03
 * Email : linxinyuan@lizhi.fm
 * Desc : Map接口对象解析器
 */
public class MapParser implements IParser<Map> {
    @Override
    public Class parseClassType() {
        return Map.class;
    }

    @Override
    public String parseString(ILogzConfig configer, Map map) {
        String msg = map.getClass().getName() + LogzConstant.BR;
        Set keys = map.keySet();
        for (Object key : keys) {
            String itemString = "%s -> %s" + LogzConstant.BR;
            Object value = map.get(key);
            if (value != null) {
                if (value instanceof String) {
                    value = "\"" + value + "\"";//add "" when value is string
                } else if (value instanceof Character) {
                    value = "\'" + value + "\'";//add '' when value is char
                }
            }
            msg += String.format(itemString, LogzConvert.objectToString(configer, key),
                    LogzConvert.objectToString(configer, value));
        }
        return msg;
    }
}
