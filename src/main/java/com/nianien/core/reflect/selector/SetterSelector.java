package com.nianien.core.reflect.selector;

import com.nianien.core.function.Predicate;
import com.nianien.core.reflect.Reflections;

import java.lang.reflect.Method;

/**
 * Setter方法选择器
 * @author skyfalling
 */
public  class SetterSelector implements Predicate<Method> {

    private final String propertyName;
    private final Class<?> propertyType;
    private final Object propertyValue;


    public SetterSelector() {
        this(null, null, null);
    }

    public SetterSelector(String propertyName, Class<?> propertyType, Object propertyValue) {
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.propertyValue = propertyValue;
    }

    @Override
    public boolean apply(Method method) {
        String methodName = method.getName();
        boolean isSetter = method.getReturnType() == Void.TYPE && method.getParameterTypes().length == 1 && methodName.startsWith("set") && methodName.length() > 3;
        return isSetter &&
                (propertyName == null || Reflections.propertyName(method).equals(propertyName)) &&
                (propertyType == null || method.getParameterTypes()[0] == propertyType) &&
                (propertyValue == null || method.getParameterTypes()[0].isInstance(propertyValue));
    }
}