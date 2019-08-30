package com.digo.platform.logan.mine.parser.intent;

import android.os.Bundle;

import com.digo.platform.logan.mine.common.LogzConstant;
import com.digo.platform.logan.mine.common.LogzConvert;
import com.digo.platform.logan.mine.config.ILogzConfig;
import com.digo.platform.logan.mine.parser.IParser;

/**
 * Author : Create by Linxinyuan on 2018/08/07
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */
public class BundleParse implements IParser<Bundle> {
    @Override
    public Class<Bundle> parseClassType() {
        return Bundle.class;
    }

    @Override
    public String parseString(ILogzConfig configer, Bundle bundle) {
        if (bundle != null) {
            StringBuilder builder = new StringBuilder(bundle.getClass().getName() + " {" + LogzConstant.BR);
            for (String key : bundle.keySet()) {
                builder.append(String.format("'%s' => %s " + LogzConstant.BR, key, LogzConvert.objectToString(configer, bundle.get(key))));
            }
            builder.append("}");
            return builder.toString();
        }
        return null;
    }
}
