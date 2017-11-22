package com.cxd.android.liveplayer.toolkit.rxjava.bus;

/**
 * Created by linhonghong on 2015/6/15.
 */
public class BusProvider {

    public static void post(Object event) {
        RxBus.get().post(event);
    }

    public static void register(Object obj) {
        RxBus.get().register(obj);
    }

    public static void unRegister(Object obj) {
        RxBus.get().unregister(obj);
    }

    @Deprecated
    public static void postMainBus(Object event) {
        post(event);
    }

    @Deprecated
    public static void postAnyBus(Object event) {
        post(event);
    }

    @Deprecated
    public static void registMainbus(Object context) {
        register(context);
    }

    @Deprecated
    public static void unRegistMainbus(Object context) {
        unRegister(context);
    }

    @Deprecated
    public static void registAnybus(Object context) {
        register(context);
    }

    @Deprecated
    public static void unRegistAnybus(Object context) {
        unRegister(context);
    }

}
