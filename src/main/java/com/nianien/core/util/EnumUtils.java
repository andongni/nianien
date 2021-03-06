package com.nianien.core.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 枚举工具类
 *
 * @author skyfalling
 */
public class EnumUtils {


    /**
     * 返回"value"字段值为value的枚举值<br/>
     * 如果不存在则返回默认值defaultEnum
     *
     * @param enumClass
     * @param value
     * @param defaultEnum
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> T valueOf(Class<T> enumClass, Object value, T defaultEnum) {
        return withField(enumClass, "value", value, defaultEnum);
    }

    /**
     * 返回"value"字段值为value的枚举值<br/>
     * 如果不存在则返回null
     *
     * @param enumClass
     * @param value
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> T valueOf(Class<T> enumClass, Object value) {
        return withField(enumClass, "value", value);
    }

    /**
     * 根据字段值返回相应的枚举对象<br/>
     * 如果不存在则返回默认值defaultEnum
     *
     * @param enumClass  枚举类型
     * @param fieldName  字段名
     * @param fieldValue 字段值
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> T withField(Class<T> enumClass, String fieldName, Object fieldValue, T defaultEnum) {
        T t = withField(enumClass, fieldName, fieldValue);
        return t != null ? t : defaultEnum;
    }

    /**
     * 根据字段值返回相应的枚举对象<br/>
     * 如果没有对应的字段或字段值返回null
     *
     * @param enumClass  枚举类型
     * @param fieldName  字段名
     * @param fieldValue 字段值
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> T withField(Class<T> enumClass, String fieldName, Object fieldValue) {
        T[] types = enumClass.getEnumConstants();
        Field field;
        try {
            field = enumClass.getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            System.err.println("field not found in enum[" + enumClass.getName() + "]: " + fieldName);
            return null;
        }
        for (T t : types) {
            try {
                if (field.get(t).equals(fieldValue))
                    return t;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.err.println("enum not found: " + enumClass.getName() + "[" + fieldName + "=" + fieldValue + "]");
        return null;
    }


    /**
     * 取枚举对象除excludes之外的实例
     *
     * @param enumClass
     * @param excludes
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> List<T> list(Class<T> enumClass, T... excludes) {
        List<T> list = new ArrayList<T>(Arrays.asList(enumClass.getEnumConstants()));
        for (T exclude : excludes) {
            list.remove(exclude);
        }
        return list;
    }

    /**
     * 取枚举对象除exclude和others之外的实例<br/>
     * 该方法等价于
     * <pre>
     * <code>List list=Arrays.asList(others);
     * list.add(exclude)
     * EnumUtils.list(exclude.getDeclaringClass(),list.toArray())
     * </code>
     * </pre>
     *
     * @param exclude
     * @param others
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> List<T> except(T exclude, T... others) {
        List<T> list = new ArrayList<T>(Arrays.asList(exclude.getDeclaringClass().getEnumConstants()));
        list.remove(exclude);
        for (T other : others) {
            list.remove(other);
        }
        return list;
    }
}
