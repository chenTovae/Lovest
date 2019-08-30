package com.digo.platform.logan.mine.parser.intent;

import android.content.Intent;
import android.text.TextUtils;

import com.digo.platform.logan.mine.Logz;
import com.digo.platform.logan.mine.common.LogzConstant;
import com.digo.platform.logan.mine.config.ILogzConfig;
import com.digo.platform.logan.mine.parser.IParser;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Author : Create by Linxinyuan on 2018/08/07
 * Email : linxinyuan@lizhi.fm
 * Desc : android dev
 */
public class IntentParser implements IParser<Intent> {
    private static Map<Integer, String> flagMap = new HashMap<>();

    static {
        Class cla = Intent.class;
        Field[] fields = cla.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getName().startsWith("FLAG_")) {
                int value = 0;
                try {
                    Object object = field.get(cla);
                    if (object instanceof Integer || object.getClass().getSimpleName().equals
                            ("int")) {
                        value = (int) object;
                    }
                } catch (Exception e) {
                    Logz.e(e.toString());
                }
                if (flagMap.get(value) == null) {
                    flagMap.put(value, field.getName());
                }
            }
        }
    }

    @Override
    public Class<Intent> parseClassType() {
        return Intent.class;
    }

    @Override
    public String parseString(ILogzConfig configer, Intent intent) {
        StringBuilder builder = new StringBuilder(parseClassType().getSimpleName() + " [" +
                LogzConstant.BR);
        builder.append(String.format("%s = %s" + LogzConstant.BR, "Scheme", intent.getScheme()));
        builder.append(String.format("%s = %s" + LogzConstant.BR, "Action", intent.getAction()));
        builder.append(String.format("%s = %s" + LogzConstant.BR, "Type", intent.getType()));
        builder.append(String.format("%s = %s" + LogzConstant.BR, "Package", intent.getPackage()));
        builder.append(String.format("%s = %s" + LogzConstant.BR, "ComponentInfo", intent.getComponent()));
        builder.append(String.format("%s = %s" + LogzConstant.BR, "Flags", getFlags(intent.getFlags())));
        builder.append(String.format("%s = %s" + LogzConstant.BR, "Categories", intent.getCategories()));
        builder.append(String.format("%s = %s" + LogzConstant.BR, "Extras", new BundleParse().parseString(configer, intent.getExtras())));
        return builder.toString() + "]";
    }

    /**
     * 获取flag的值
     *
     * @param flags
     * @return
     */
    private String getFlags(int flags) {
        StringBuilder builder = new StringBuilder();
        for (int flagKey : flagMap.keySet()) {
            if ((flagKey & flags) == flagKey) {
                builder.append(flagMap.get(flagKey));
                builder.append(" | ");
            }
        }
        if (TextUtils.isEmpty(builder.toString())) {
            builder.append(flags);
        } else if (builder.indexOf("|") != -1) {
            builder.delete(builder.length() - 2, builder.length());
        }
        return builder.toString();
    }
}
