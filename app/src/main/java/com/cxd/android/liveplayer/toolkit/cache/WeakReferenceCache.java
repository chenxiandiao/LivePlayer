package com.cxd.android.liveplayer.toolkit.cache;

import android.support.annotation.Nullable;

import com.cxd.android.liveplayer.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.HashMap;


/**
 * Created by Rancune@126.com on 2017/3/27.
 * cache,内部是weakreference
 * 线程安全
 */

public class WeakReferenceCache<T> implements Cache<T> {
    private static final int DEFAULT_CAPACITY = 4;
    
    private final HashMap<String, WeakReference<T>> mCache;
    
    public WeakReferenceCache() {
        mCache = new HashMap<>(DEFAULT_CAPACITY);
    }
    
    public WeakReferenceCache(int capacity) {
        if (capacity <= 0) {
            capacity = DEFAULT_CAPACITY;
        }
        mCache = new HashMap<>(capacity);
    }
    
    
    @Override
    public void put(String key, T object) {
        if (Utils.isEmpty(key) || object == null) {
            return;
        }
        synchronized (this) {
            mCache.put(key, new WeakReference<>(object));
        }
    }
    
    @Nullable
    @Override
    public T get(String key) {
        if (Utils.isEmpty(key)) {
            return null;
        }
        synchronized (this) {
            WeakReference<T> reference = mCache.get(key);
            return reference != null ? reference.get() : null;
        }
    }
    
    @Nullable
    @Override
    public T remove(String key) {
        if (Utils.isEmpty(key)) {
            return null;
        }
        synchronized (this) {
            WeakReference<T> previous = mCache.remove(key);
            return previous != null ? previous.get() : null;
        }
    }
    
    @Override
    public void clear() {
        synchronized (this) {
            mCache.clear();
        }
    }
}
