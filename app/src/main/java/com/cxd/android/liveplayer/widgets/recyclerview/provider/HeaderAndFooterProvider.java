package com.cxd.android.liveplayer.widgets.recyclerview.provider;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by Rancune@126.com on 16/9/6.
 */
public interface HeaderAndFooterProvider {

    /**
     * add header view
     */
    void addHeaderView(@NonNull View view);

    /**
     * we can add more than one header view,
     * than we can add a header view at the given postion
     *
     * @param position the postion of the header view list
     * @param view     header view
     */
    void addHeaderView(@NonNull View view, int position);

    /**
     * @param header header
     * @return whether the view is header
     */
    boolean isHeaderView(View header);

    /**
     * @param postiion position
     * @return whether the view is header
     */
    boolean isHeaderView(int postiion);

    /**
     * remove header view
     */
    void removeHeaderView(View view);

    /**
     * return the count of header views
     */
    int getHeaderViewCount();

    /**
     * add footer view
     */
    void addFooterView(@NonNull View view);

    /**
     * @param footer footer
     * @return whether the view is footer
     */
    boolean isFooterView(View footer);

    /**
     * @param postiion position
     * @return whether the view is footer
     */
    boolean isFooterView(int postiion);

    /**
     * we can add more than one footer view,
     * than we can add a footer view at the given postion
     *
     * @param position the position of the footer view list
     * @param view     footer view
     */
    void addFooterView(@NonNull View view, int position);

    /**
     * remove footer view
     */
    void removeFooterView(View view);

    /**
     * return the count of footer views
     */
    int getFooterViewCount();

    /**
     * set the view to be shown when the data is empty
     */
    void setEmptyView(@NonNull View emptyView);

    /**
     * get the empty view of the recyclerview
     */
    @Nullable
    View getEmptyView();
}
