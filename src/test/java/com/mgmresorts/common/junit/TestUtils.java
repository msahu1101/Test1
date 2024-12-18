package com.mgmresorts.common.junit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings("unchecked")
public abstract class TestUtils {

    public static <T> T invoke(String name, Object obj, Class<?>[] parameterTypes, Object[] parameters) throws Exception {
        final Method method = obj.getClass().getDeclaredMethod(name, parameterTypes);
        method.setAccessible(true);
        return (T) method.invoke(obj, parameters);
    }

    public static <T> T get(String field, Object obj) throws Exception {
        final Field declaredField = obj.getClass().getDeclaredField(field);
        declaredField.setAccessible(true);
        return (T) declaredField.get(obj);
    }
}
