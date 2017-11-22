package com.cxd.android.liveplayer.toolkit.rxjava.executor;

/**
 * Created by Rancune@126.com on 2016/1/20.
 */
public interface Function<T> {
    T call(Object... params);
}
