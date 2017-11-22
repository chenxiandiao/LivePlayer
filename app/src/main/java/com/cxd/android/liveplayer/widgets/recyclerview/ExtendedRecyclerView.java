package com.cxd.android.liveplayer.widgets.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.cxd.android.liveplayer.widgets.recyclerview.listener.DateSetWatcher;
import com.cxd.android.liveplayer.widgets.recyclerview.provider.HeaderAndFooterProvider;
import com.cxd.android.liveplayer.widgets.recyclerview.provider.LoadMoreProvider;


/**
 * Created by Rancune@126.com on 2016/9/29.
 * <p>
 * Recyclerview which supports header views and footer views
 */
public class ExtendedRecyclerView extends RecyclerView implements HeaderAndFooterProvider {

    /**
     * a decration of normal RecyclerView.Adapter
     * which supports header view, footer view ,empty view
     * {@link ExtendedRecyclerAdapter}
     */
    protected ExtendedRecyclerAdapter extendedAdapter;

    /**
     * the empty view
     */
    protected View emptyView;

    public ExtendedRecyclerView(Context context) {
        super(context);
    }

    public ExtendedRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * @param adapter a normal RecyclerView.Adapter is enough
     *                no need to wraps it as a ExtendedRecyclerAdapter
     */
    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter instanceof ExtendedRecyclerAdapter) {
            extendedAdapter = (ExtendedRecyclerAdapter) adapter;
            super.setAdapter(extendedAdapter);
        } else {
            if (extendedAdapter == null) {
                extendedAdapter = new ExtendedRecyclerAdapter(adapter);
                super.setAdapter(extendedAdapter);
            } else {
                extendedAdapter.setAdapter(adapter);
            }
        }
    }

    @Override
    public ExtendedRecyclerAdapter getAdapter() {
        return extendedAdapter;
    }

    /**
     * add a header view for the RecyclerView
     */
    @Override
    public void addHeaderView(@NonNull View header) {
        if (extendedAdapter == null) {
            throw new IllegalStateException("should call setAdapter() first");
        }
        extendedAdapter.addHeaderView(header);
    }

    @Override
    public void addHeaderView(@NonNull View view,int position) {
        if (extendedAdapter == null) {
            throw new IllegalStateException("should call setAdapter() first");
        }
        extendedAdapter.addHeaderView(view, position);
    }

    @Override
    public boolean isHeaderView(View header) {
        return extendedAdapter != null && extendedAdapter.isHeader(header);
    }

    @Override
    public boolean isHeaderView(int postiion) {
        return extendedAdapter != null && extendedAdapter.isHeader(postiion);
    }

    /**
     * remove the header
     */
    @Override
    public void removeHeaderView(View header) {
        if (header == null) {
            return;
        }

        // if the extendedAdapter is null,
        // then the header can not  be a header of the RecyclerView
        if (extendedAdapter == null) {
            return;
        }

        extendedAdapter.removeHeaderView(header);
    }

    /**
     * get the count of header views
     */
    @Override
    public int getHeaderViewCount() {
        return extendedAdapter == null ? 0 : extendedAdapter.getHeaderViewsCount();
    }

    /**
     * add a footer view fot the RecyclerView
     */
    @Override
    public void addFooterView(@NonNull View footer) {
        if (extendedAdapter == null) {
            throw new IllegalStateException("should call setAdapter() first");
        }

        extendedAdapter.addFooterView(footer);
    }

    @Override
    public boolean isFooterView(View footer) {
        return extendedAdapter != null && extendedAdapter.isFooter(footer);
    }

    @Override
    public boolean isFooterView(int postiion) {
        return extendedAdapter != null && extendedAdapter.isFooter(postiion);
    }

    @Override
    public void addFooterView(@NonNull View view, int position) {
        if (extendedAdapter == null) {
            throw new IllegalStateException("should call setAdapter() first");
        }
        extendedAdapter.addFooterView(view, position);
    }

    @Override
    public void removeFooterView(View footer) {
        if (footer == null) {
            return;
        }

        // if the extendedAdapter is null,
        // then the footer can not  be a footer of the RecyclerView
        if (extendedAdapter == null) {
            return;
        }

        extendedAdapter.removeFooterView(footer);
    }

    @Override
    public int getFooterViewCount() {
        return extendedAdapter == null ? 0 : extendedAdapter.getFooterViewsCount();
    }

    @Override
    public void setEmptyView(@NonNull final View empty) {
        this.emptyView = empty;
        if (extendedAdapter == null) {
            throw new IllegalArgumentException("should call setAdapter() first");
        }
        extendedAdapter.setEmptyListener(new DateSetWatcher() {
            @Override
            public void isDataSetEmpty(boolean isEmpty) {
                if (emptyView != null) {
                    if (isEmpty) {
                        ExtendedRecyclerView.this.setVisibility(GONE);
                        ExtendedRecyclerView.this.emptyView.setVisibility(VISIBLE);
                    } else {
                        ExtendedRecyclerView.this.setVisibility(VISIBLE);
                        ExtendedRecyclerView.this.emptyView.setVisibility(GONE);
                    }
                }
            }
        });
    }

    @Nullable
    @Override
    public View getEmptyView() {
        return emptyView;
    }

    public void setLoadMoreProvider(LoadMoreProvider loadMoreProvider) {
        if (extendedAdapter == null) {
            throw new IllegalArgumentException("should call setAdapter() first");
        }
        extendedAdapter.setLoadMoreProvider(loadMoreProvider);
    }

}
