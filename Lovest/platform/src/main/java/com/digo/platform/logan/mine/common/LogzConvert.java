package com.digo.platform.logan.mine.common;

import com.digo.platform.logan.mine.config.ILogzConfig;
import com.digo.platform.logan.mine.parser.ArrayParser;
import com.digo.platform.logan.mine.parser.IParser;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Author : Create by Linxinyuan on 2018/08/02
 * Email : linxinyuan@lizhi.fm
 * Desc : Logz转化类
 */
public class LogzConvert {
    /**
     * 日志框分割线打印
     *
     * @param dividerTop
     * @return
     */
    public static String printDividingLine(int dividerTop) {
        switch (dividerTop) {
            case LogzConstant.DIVIDER_TOP:
                return "╔═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════";
            case LogzConstant.DIVIDER_BOTTOM:
                return "╚═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════";
            case LogzConstant.DIVIDER_NORMAL:
                return "║ ";
            case LogzConstant.DIVIDER_CENTER:
                return "╟───────────────────────────────────────────────────────────────────────────────────────────────────────────────────";
            default:
                break;
        }
        return "";
    }

    /**
     * 将对象转化为String
     *
     * @param object
     * @return
     */
    public static String objectToString(ILogzConfig configer, Object object) {
        return objectToString(configer, object, 0);
    }

    /**
     * 将对象转化为String
     *
     * @param object
     * @param childLevel -> 解析对象的最大层级
     * @return
     */
    public static String objectToString(ILogzConfig configer, Object object, int childLevel) {
        if (object == null) {
            return LogzConstant.TIP_OBJECT_NULL;
        }
        if (object instanceof String) {
            return (String) object;
        }
        if (childLevel > configer.getParserLevel()) {
            return object.toString();
        }
        // 对数组类型的进行转化(独立为一个分支判断是因为Object无法跟Array进行判断)
        if (object.getClass().isArray()) {
            return ArrayParser.getInstance().parseString(configer, object);
        }
        // 通过自定义解析器解析(Map/Collection),支持自定义装配解析器
        if (LogzConstant.getParserList(configer) != null && LogzConstant.getParserList(configer).size() > 0) {
            for (IParser parser : LogzConstant.getParserList(configer)) {
                if (parser.parseClassType().isAssignableFrom(object.getClass())) {
                    return parser.parseString(configer, object);
                }
            }
        }
        // 对Object进行解析(反射会耗费一定性能,不建议调用)
        if (object.toString().startsWith(object.getClass().getName() + "@")) {
            StringBuilder builder = new StringBuilder();
            //往下解析类成员属性，最大子类层次默认为1
            getClassFields(configer, object.getClass(), builder, object, false, childLevel);
            //往上解析父类属性，最大父类层次默认为1
            for (int i = 0; i < configer.getParserLevel(); i++) {
                Class superClass = object.getClass().getSuperclass();
                while (!superClass.equals(Object.class)) {
                    getClassFields(configer, superClass, builder, object, true, childLevel);
                    superClass = superClass.getSuperclass();
                }
            }
            return builder.toString();
        } else {
            // 若对象重写toString()方法默认走toString()
            return object.toString();
        }
    }

    /**
     * 拼接class的字段和值
     *
     * @param cla
     * @param builder
     * @param object
     * @param isSubClass
     * @param childOffset
     */
    private static void getClassFields(ILogzConfig configer, Class cla, StringBuilder builder,
                                       Object object, boolean isSubClass, int childOffset) {
        if (cla.equals(Object.class)) {
            return;
        }
        //是否是解析类的超类,添加换行符 + "=>"
        if (isSubClass) {
            builder.append(LogzConstant.BR).append(LogzConstant.BR).append("=> ");
        }
        String breakLine = "";
        builder.append(cla.getSimpleName()).append(" {");
        //反射获取对象内属性,不局限于public修饰符
        Field[] fields = cla.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            Field field = fields[i];
            field.setAccessible(true);
            if (cla.isMemberClass() && !isStaticInnerClass(cla) && i == 0) {
                continue;
            }
            Object subObject = null;
            try {
                subObject = field.get(object);
            } catch (IllegalAccessException e) {
                subObject = e;
            } finally {
                //解析自己与类成员属性/解析父类对象
                if (subObject != null) {
                    if (!isStaticInnerClass(cla) && (field.getName().equals("$change")
                            || field.getName().equalsIgnoreCase("this$0"))) {
                        continue;
                    }
                    if (subObject instanceof String) {
                        subObject = "\"" + subObject + "\"";
                    } else if (subObject instanceof Character) {
                        subObject = "\'" + subObject + "\'";
                    }
                    if (childOffset < configer.getParserLevel()) {
                        subObject = objectToString(configer, subObject, childOffset + 1);
                    }
                }
                String formatString = breakLine + "%s = %s, ";
                builder.append(String.format(formatString, field.getName(),
                        subObject == null ? "null" : subObject.toString()));
            }
        }
        if (builder.toString().endsWith("{")) {
            builder.append("}");
        } else {
            builder.replace(builder.length() - 2, builder.length() - 1, breakLine + "}");
        }
    }

    /**
     * 是否为静态内部类
     *
     * @param cla
     * @return
     */
    private static boolean isStaticInnerClass(Class cla) {
        if (cla != null && cla.isMemberClass()) {
            int modifiers = cla.getModifiers();
            if ((modifiers & Modifier.STATIC) == Modifier.STATIC) {
                return true;
            }
        }
        return false;
    }
}
