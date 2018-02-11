package org.gwhere.utils;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public class TokenCache {

    private static Map<String, MemoryReference<Object>> cache = new HashMap<>();

    private static ReferenceQueue<MemoryReference> gcQueue = new ReferenceQueue<>();

    private final static Object lock = new Object();

    public static void put(String key, Object value) {
        synchronized (lock) {
            flushCache();
            MemoryReference<Object> memoryReference = new MemoryReference<>(key, value, gcQueue);
            cache.put(key, memoryReference);
        }
    }

    public static Object get(String key) {
        synchronized (lock) {
            MemoryReference<Object> reference = cache.get(key);
            if (reference != null) {
                return reference.get();
            }
            return null;
        }
    }

    public static void clean(String key) {
        synchronized (lock) {
            cache.remove(key);
        }
    }

    /**
     * 清除已被回收的项
     */
    public static void flushCache() {
        MemoryReference<Object> reference;
        while ((reference = (MemoryReference) gcQueue.poll()) != null) {
            cache.remove(reference.key);
        }
    }

    private static class MemoryReference<T> extends SoftReference<T> {
        String key;

        public MemoryReference(String key, T referent, ReferenceQueue q) {
            super(referent, q);
            this.key = key;
        }
    }
    
    public static boolean isNotInitialized(){
    	return cache.isEmpty();
    }
}
