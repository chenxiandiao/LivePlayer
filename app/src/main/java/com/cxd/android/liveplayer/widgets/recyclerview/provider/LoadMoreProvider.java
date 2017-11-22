package com.cxd.android.liveplayer.widgets.recyclerview.provider;

import android.support.annotation.NonNull;
import android.view.View;

import com.cxd.android.liveplayer.widgets.recyclerview.listener.LoadMoreListener;

/**
 * Created by Rancune@126.com on 2016/7/6.
 */
@SuppressWarnings("unused")
public interface LoadMoreProvider {

    void setLoadMoreListener(LoadMoreListener listener);

    void setLoadMoreFooter(@NonNull View v);

    void onFinishLoadMore();

    void setHasMoreItems(boolean hasMoreItems);

    void showLoadMore(int count);

    boolean hasLoadMoreView();

    void hideLoadMore();
}
