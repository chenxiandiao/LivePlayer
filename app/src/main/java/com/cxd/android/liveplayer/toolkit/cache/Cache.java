package com.cxd.android.liveplayer.toolkit.cache;

import android.support.annotation.Nullable;

/**
 * Created by Rancune@126.com on 2016/2/23.
 */
public interface Cache<T> {

    void put(String key, T object);

    @Nullable
    T get(String key);

    @Nullable
    T remove(String key);

    void clear();
}
