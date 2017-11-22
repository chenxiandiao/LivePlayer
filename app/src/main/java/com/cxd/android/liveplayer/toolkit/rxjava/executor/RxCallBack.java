package com.cxd.android.liveplayer.toolkit.rxjava.executor;

/**
 * Created by chenxiandiao on 16/10/31.
 */

public interface RxCallBack<T> {
    void start();

    void success(T data);

    void failture();
}
