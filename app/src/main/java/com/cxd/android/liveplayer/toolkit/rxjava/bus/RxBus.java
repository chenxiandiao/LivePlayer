package com.cxd.android.liveplayer.toolkit.rxjava.bus;


import com.cxd.android.liveplayer.toolkit.rxjava.bus.thread.ThreadEnforcer;

/**
 * Instance of {@link Bus}.
 * Simply use {@link #get()} to get the instance of {@link Bus}
 */
public class RxBus {

    /**
     * Instance of {@link Bus}
     */
    private volatile static Bus sBus;

    /**
     * Get the instance of {@link Bus}
     *
     * @return
     */
    public static Bus get() {
        if(sBus == null){
            synchronized (RxBus.class){
                if (sBus == null) {
                    sBus = new Bus(ThreadEnforcer.ANY);
                }
            }
        }
        return sBus;
    }
}