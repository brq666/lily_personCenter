package org.gwhere.utils;

import java.util.HashMap;
import java.util.Map;

import org.gwhere.vo.PermissionVO;

public class PermissionCache {

    private static Map<String, PermissionVO> permissions = new HashMap<String, PermissionVO>();

    public static void put(String key, PermissionVO value) {
        permissions.put(key, value);
    }

    public static PermissionVO get(String key) {
        return permissions.get(key);
    }

    public static void clear() {
        permissions.clear();
    }

    public static boolean isNotInitialized() {
        return permissions.isEmpty();
    }
}
