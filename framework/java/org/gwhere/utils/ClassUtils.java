package org.gwhere.utils;

/**
 * Created by jiangtao on 15-7-9.
 */
public class ClassUtils {

    private static final String CGLIB_CLASS_SEPARATOR = "$$";

    public static Class<?> getUserClass(Object instance) {
        return getUserClass(instance.getClass());
    }

    public static Class<?> getUserClass(Class<?> clazz) {
        if (clazz != null && clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && !Object.class.equals(superClass)) {
                return superClass;
            }
        }
        return clazz;
    }

    public static Class getClass(Object object, Class clazz) {
        Class source = object.getClass();
        return getSuperClass(source, clazz);
    }

    public static Class getSuperClass(Class source, Class clazz) {
        if (source == clazz) {
            return clazz;
        } else {
            return getSuperClass(source.getSuperclass(), clazz);
        }
    }
}
