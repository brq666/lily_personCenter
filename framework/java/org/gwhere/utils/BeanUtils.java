package org.gwhere.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiangtao on 15-7-17.
 */
public class BeanUtils {

    private static void putMapValue2Bean(Class clazz, Map map, Object obj) throws InvocationTargetException, IllegalAccessException {
        if (clazz == null) return;
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (name.startsWith("set") && name.length() != 3) {
                String fieldName = name.substring(3);
                String first = fieldName.substring(0, 1);
                fieldName = fieldName.replaceFirst(first, first.toUpperCase());
                if (map.containsKey(fieldName)) {
                    method.setAccessible(true);
                    if (method.getParameterTypes()[0] == Integer.class) {
                        method.invoke(obj, Integer.parseInt(map.get(fieldName).toString()));
                    } else {
                        method.invoke(obj, map.get(fieldName));
                    }
                }
            }
        }
    }

    public static void map2Bean(Object obj, Map map) {
        try {
            Class clazz = obj.getClass();
            do {
                putMapValue2Bean(clazz, map, obj);
                clazz = clazz.getSuperclass();
            } while (clazz != null);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> bean2Map(Object object) {
        Map<String, String> map = new HashMap<>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            Object obj;
            try {
                field.setAccessible(true);
                obj = field.get(object);
                if (obj != null) {
                    map.put(field.getName(), obj.toString());
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
