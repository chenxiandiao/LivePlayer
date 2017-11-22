package com.cxd.android.liveplayer.widgets.recyclerview.provider;

import com.cxd.android.liveplayer.widgets.recyclerview.listener.PullToRefreshListener;

/**
 * Created by Rancune@126.com on 16/9/6.
 */
public interface PullToRefreshProvider {

    /**
     * set pull-to-refresh enabled/disabled
     */
    void setPullToRefreshEnabled(boolean enabled);

    /**
     * whether pull-to-refresh is enabled
     */
    boolean isPullToRefreshEnabled();

    /**
     * start to pull to refresh
     */
    void startToRefresh();

    /**
     * pull-to-refresh complete
     */
    void completeRefresh();

    /**
     * pull to refresh listener
     */
    void setPullToRefreshListener(PullToRefreshListener listener);
}
