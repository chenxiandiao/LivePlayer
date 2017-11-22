package com.cxd.android.liveplayer.toolkit.cache;


import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;

import com.cxd.android.liveplayer.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;



/**
 * Created by Rancune@126.com on 2016/2/24.
 * memory 二级缓存
 * 一级使用lruCache,二级使用WeakReference
 * 线程安全
 */
public class MemoryCache<T> implements Cache<T> {
    private static final int DEFAULT_SIZE = 8 * 1024 * 1024;

    private final LruCache<String, T> lruCache;
    private final Map<String, WeakReference<T>> weakReferenceMap;

    public MemoryCache(int size, final SizeOf<T> calculator) {
        weakReferenceMap = new HashMap<>();
        int lrusize = size <= 0 ? DEFAULT_SIZE : size;
        lruCache = new LruCache<String, T>(lrusize) {
            @Override
            protected int sizeOf(String key, T value) {
                if (calculator == null) {
                    // represents an entry
                    return 1;
                } else {
                    // the size of the entry for key and memory in user-defined units.
                    return calculator.sizeOf(key, value);
                }
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, T oldValue, T newValue) {
                if (evicted && oldValue != null) {
                    //the entry is being removed to make space
                    weakReferenceMap.put(key, new WeakReference<>(oldValue));
                }
                // if the removal was caused by a put or remove
                // we do not need to put that entry in the weakrefrence map
            }
        };
    }

    @Override
    public void put(String key, T object) {
        if (Utils.isEmpty(key) || object == null) {
            return;
        }
        synchronized (this) {
            lruCache.put(key, object);
        }
    }

    @Override
    @Nullable
    public T get(String key) {
        if (Utils.isEmpty(key)) {
            return null;
        }
        synchronized (this) {
            T value = lruCache.get(key);
            if (value == null) {
                WeakReference<T> reference = weakReferenceMap.get(key);
                if (reference != null) {
                    value = reference.get();
                    if (value != null) {
                        lruCache.put(key, value);
                    }
                }
            }

            return value;
        }
    }

    @Override
    @Nullable
    public T remove(String key) {
        if (Utils.isEmpty(key)) {
            return null;
        }
        synchronized (this) {
            T previous = lruCache.remove(key);
            WeakReference<T> reference = weakReferenceMap.remove(key);

            return previous != null ? previous : (reference != null ? reference.get() : null);
        }

    }

    @Override
    public void clear() {
        synchronized (this) {
            lruCache.evictAll();
            weakReferenceMap.clear();
        }
    }

}
