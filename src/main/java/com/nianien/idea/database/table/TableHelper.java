package com.nianien.idea.database.table;

import com.nianien.core.reflect.Reflections;
import com.nianien.core.util.StringUtils;

import java.lang.reflect.Method;

/**
 * 获取表名和字段名的辅助类
 *
 * @author skyfalling
 */
public class TableHelper {
    private TableHelper() {
    }

    /**
     * 根据实体类型获取数据库表名称<br>
     * 其中,注解名称优先于类名称
     *
     * @param clazz
     * @return 表名称
     */
    public static String getTableName(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        String name = table != null ? table.value() : null;
        return StringUtils.notEmpty(name) ? name : clazz.getSimpleName();
    }

    /**
     * 根据getter/setter(或isXXX)方法获取数据库表中字段名称<br>
     * 优先级: @Column>@Property>getter
     *
     * @param method
     * @return 字段名称
     */
    public static String getColumnName(Method method) {
        Column column = method.getAnnotation(Column.class);
        String name = column != null ? column.value() : null;
        return StringUtils.notEmpty(name) ? name : Reflections.propertyName(method);
    }
}
