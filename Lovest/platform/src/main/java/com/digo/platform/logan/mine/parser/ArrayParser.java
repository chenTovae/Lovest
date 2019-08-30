package com.digo.platform.logan.mine.parser;

import com.digo.platform.logan.mine.common.LogzConvert;
import com.digo.platform.logan.mine.config.ILogzConfig;

import java.util.Arrays;

/**
 * Author : Create by Linxinyuan on 2018/08/03
 * Email : linxinyuan@lizhi.fm
 * Desc : Array对象解析器
 */
public class ArrayParser implements IParser<Object> {
    private ArrayParser() {
    }

    public static IParser getInstance() {
        return ArrayParserInstance.INSTANCE;
    }

    public static class ArrayParserInstance {
        private static final ArrayParser INSTANCE = new ArrayParser();
    }

    @Override
    public Class parseClassType() {
        return Arrays.class;//can not check object type, what the fuck
    }

    @Override
    public String parseString(ILogzConfig configer, Object array) {
        StringBuilder result = new StringBuilder();
        traverseArray(configer, result, array);
        return result.toString();
    }

    /**
     * 遍历数组(一维数组或多维数组)
     *
     * @param result
     * @param array
     */
    private void traverseArray(ILogzConfig configer, StringBuilder result, Object array) {
        if (getArrayDimension(array) == 1) {
            switch (getArrayType(array)) {
                case 'I'://int
                    result.append(Arrays.toString((int[]) array));
                    break;
                case 'D'://double
                    result.append(Arrays.toString((double[]) array));
                    break;
                case 'Z'://boolean
                    result.append(Arrays.toString((boolean[]) array));
                    break;
                case 'B'://byte
                    result.append(Arrays.toString((byte[]) array));
                    break;
                case 'S'://short
                    result.append(Arrays.toString((short[]) array));
                    break;
                case 'J'://long
                    result.append(Arrays.toString((long[]) array));
                    break;
                case 'F'://float
                    result.append(Arrays.toString((float[]) array));
                    break;
                case 'L'://array of object
                    Object[] objects = (Object[]) array;
                    result.append("[");
                    for (int i = 0; i < objects.length; ++i) {
                        //Object数组需要分单个重新解析
                        result.append(LogzConvert.objectToString(configer, objects[i]));
                        if (i != objects.length - 1) {
                            result.append(",");
                        }
                    }
                    result.append("]");
                    break;
                default:
                    result.append(Arrays.toString((Object[]) array));
                    break;
            }
        } else {
            result.append("[");
            for (int i = 0; i < ((Object[]) array).length; i++) {
                traverseArray(configer, result, ((Object[]) array)[i]);
                if (i != ((Object[]) array).length - 1) {
                    result.append(",");
                }
            }
            result.append("]");
        }
    }

    /**
     * 获取数组的纬度
     * [[Ljava.lang.String;@2497a91e->[个数标识维度
     *
     * @param object
     * @return
     */
    private int getArrayDimension(Object object) {
        int dim = 0;
        for (int i = 0; i < object.toString().length(); ++i) {
            if (object.toString().charAt(i) == '[') {
                ++dim;
            } else {
                break;
            }
        }
        return dim;
    }

    /**
     * 获取数组类型
     *
     * @param object 如L为string型
     * @return
     */
    private char getArrayType(Object object) {
        if (object.getClass().isArray()) {
            String str = object.toString();
            return str.substring(str.lastIndexOf("[") + 1, str.lastIndexOf("[") + 2).charAt(0);
        }
        return 0;
    }
}
