package com.nianien.idea.database.table;

import com.nianien.core.annotation.Ignore;
import com.nianien.core.collection.map.CaseInsensitiveMap;
import com.nianien.core.exception.ExceptionHandler;
import com.nianien.core.function.Predicate;
import com.nianien.core.log.LoggerFactory;
import com.nianien.core.reflect.Reflections;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * 接口{@link DataTable}的默认实现
 *
 * @param <T>
 */
class DataTableImpl<T> implements DataTable<T> {
    private static Logger logger = LoggerFactory.getLogger(DataTableImpl.class);
    /**
     * 表名
     */
    private String name;
    /**
     * 实体类型
     */
    private Class<T> type;
    /**
     * 字段属性列表
     */
    private CaseInsensitiveMap<String, FiledProperty> fieldProperties = new CaseInsensitiveMap<String, FiledProperty>();

    /**
     * 键值字段
     */
    private String idField;


    /**
     * 构造方法
     *
     * @param entityClass
     */
    public DataTableImpl(Class<T> entityClass) {
        this.type = entityClass;
        this.name = TableHelper.getTableName(entityClass);
        Reflections.getters(entityClass, new Predicate<Method>() {

            public boolean apply(Method method) {
                if (!method.isAnnotationPresent(Ignore.class)) {
                    addFieldProperty(method);
                }
                return false;
            }
        });
        if (idField == null && hasField("id")) {
            idField = fieldProperty("id").name;
        } else {
            logger.warning("no id field defined in table[" + type + "]");
        }
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public Set<String> getFieldNames() {
        return fieldProperties.keySet();
    }

    @Override
    public List<DataField> getFields(T entity) {
        List<DataField> fields = new ArrayList<DataField>();
        for (FiledProperty property : this.fieldProperties.values()) {
            fields.add(property.getField(entity));
        }
        return fields;
    }

    @Override
    public List<DataField> getFields(T entity, String... fieldNames) {
        List<DataField> fields = new ArrayList<DataField>();
        for (String fieldName : fieldNames) {
            fields.add(fieldProperty(fieldName).getField(entity));
        }
        return fields;
    }

    @Override
    public DataField getField(T entity, String fieldName) {
        return fieldProperty(fieldName).getField(entity);
    }


    @Override
    public void setField(T entity, String fieldName, Object value) {
        fieldProperty(fieldName).setField(entity, value);
    }


    @Override
    public String getFieldName(String fieldName) {
        return fieldProperty(fieldName).name;
    }

    @Override
    public boolean hasField(String fieldName) {
        return fieldProperties.containsKey(fieldName);
    }

    @Override
    public Class getFieldType(String fieldName) {
        return fieldProperties.get(fieldName).getter.getReturnType();
    }

    @Override
    public DataField idField(T entity) {
        ExceptionHandler.throwIfNull(idField, new NoSuchFieldException("no id field declared in table[" + type + "]"));
        return getField(entity, idField);
    }


    /**
     * 获取字段属性
     *
     * @param fieldName
     * @return
     */
    private FiledProperty fieldProperty(String fieldName) {
        FiledProperty filedProperty = fieldProperties.get(fieldName);
        ExceptionHandler.throwIfNull(filedProperty, new NoSuchFieldException("no such field declared in table[" + type + "]:" + fieldName));
        return filedProperty;
    }

    /**
     * 添加字段属性方法
     *
     * @param getter
     */
    private void addFieldProperty(Method getter) {
        String columnName = TableHelper.getColumnName(getter);
        ExceptionHandler.throwIf(fieldProperties.containsKey(columnName), "duplicate field declared in table[" + type + "]: " + columnName);
        String getterName = getter.getName();
        String setterName = "set" + getterName.substring(getterName.startsWith("is") ? 2 : 3);
        //获取getter对应的setter方法
        Method setter = Reflections.getMethod(type, setterName, getter.getReturnType());
        fieldProperties.put(columnName, new FiledProperty(columnName, getter.isAnnotationPresent(Column.class) ? getter.getAnnotation(Column.class).sqlType() : DataField.GenericType, getter, setter));
        if (getter.isAnnotationPresent(Id.class)) {
            ExceptionHandler.throwIf(idField != null, "duplicate id field declared in table[" + type + "]: " + columnName + "," + idField);
            this.idField = columnName;
        }
    }


    /**
     * 字段相关属性信息,包括名称,sqlType类型,getter和setter方法<br/>
     * 字段的getter和setter方法名必须保持一致,且getter方法的返回类型为setter的参数类型
     */
    public static class FiledProperty {
        final Method getter;
        final Method setter;
        final String name;
        final int type;

        FiledProperty(String name, int type, Method getter, Method setter) {
            this.name = name;
            this.type = type;
            this.getter = getter;
            this.setter = setter;
        }

        DataField getField(Object obj) {
            return new DataField(name, Reflections.invoke(getter, obj), type);
        }

        void setField(Object obj, Object value) {
            Reflections.invoke(setter, obj, value);
        }
    }

}