package com.cxd.android.liveplayer.widgets.recyclerview.ptr;

/**
 * Created by Rancune@126.com on 2016/11/2.
 */

public interface FrameAnimationListener {
    void onStart();

    void onFrame(int frame);

    void onEnd();

}
