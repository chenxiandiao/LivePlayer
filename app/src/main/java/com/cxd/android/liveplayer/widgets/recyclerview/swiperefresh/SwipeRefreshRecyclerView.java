package com.cxd.android.liveplayer.widgets.recyclerview.swiperefresh;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.cxd.android.liveplayer.R;
import com.cxd.android.liveplayer.widgets.recyclerview.DefaultLoadMoreView;
import com.cxd.android.liveplayer.widgets.recyclerview.ExtendedRecyclerAdapter;
import com.cxd.android.liveplayer.widgets.recyclerview.ExtendedRecyclerView;
import com.cxd.android.liveplayer.widgets.recyclerview.RecyclerviewUtils;
import com.cxd.android.liveplayer.widgets.recyclerview.listener.LoadMoreListener;
import com.cxd.android.liveplayer.widgets.recyclerview.listener.PullToRefreshListener;
import com.cxd.android.liveplayer.widgets.recyclerview.provider.HeaderAndFooterProvider;
import com.cxd.android.liveplayer.widgets.recyclerview.provider.LoadMoreProvider;
import com.cxd.android.liveplayer.widgets.recyclerview.provider.PullToRefreshProvider;

/**
 * Created by Rancune@126.com on 2016/7/6.
 * <p>
 * a combine of swipeRefreshLayout and ExtendedRecyclerView
 */

public class SwipeRefreshRecyclerView extends CustomSwipeRefreshLayout implements LoadMoreProvider,
        HeaderAndFooterProvider, PullToRefreshProvider {
    /**
     * we use extendedRecyclerView instead of normal RecyclerView
     */
    private ExtendedRecyclerView recyclerView;

    /**
     * view to be shown when the recycleview scrolls to the bottom and load more
     */
    private View loadMoreFooter;

    /**
     * load more listener
     */
    private LoadMoreListener loadMoreListener;

    /**
     * whether is loading more this moment
     */
    private boolean isLoadingMore = false;

    /**
     * whether the recyclerview has more items
     */
    private boolean hasMoreItems = true;

    /**
     * whether pull-to-refresh is supported
     */
    private boolean isPullToRefreshEnabled = true;

    private int smallPageSize = 10;

    private int onePageSize = 20;
    /**
     * pull to refresh listener
     */
    private PullToRefreshListener refreshListener;

    /**
     * scroll listener
     */
    private RecyclerView.OnScrollListener onScrollDelegate;

    public SwipeRefreshRecyclerView(Context context) {
        this(context, null);
    }

    public SwipeRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        recyclerView = new ExtendedRecyclerView(getContext(), attrs);
        recyclerView.setId(R.id.zues_swiperefresh_recyclerview);
//        recyclerView.getItemAnimator().setSupportsChangeAnimations(false);
        RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();
        if(itemAnimator instanceof SimpleItemAnimator){
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }
        recyclerView.setHasFixedSize(true);
        addView(recyclerView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        setColorSchemeResources(android.R.color.holo_green_light, android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_blue_bright);
        recyclerView.addOnScrollListener(mOnScrollListener);
        super.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (refreshListener != null) {
                    hasMoreItems = true;
                    ExtendedRecyclerAdapter adapter = getAdapter();
                    if (adapter != null) {
                        adapter.resetCount();
                    }
                    refreshListener.onRefresh();
                }
            }
        });
        this.setEnabled(isPullToRefreshEnabled);

        if (Build.VERSION.SDK_INT < 21) {
            this.setCanChildScrollUpCallback(new CustomSwipeRefreshLayout.CanChildScrollUpCallback() {
                @Override
                public boolean canSwipeRefreshChildScrollUp() {
                    if (null != recyclerView) {
                        return recyclerView.getScrollY() > 0;
                    }

                    return false;
                }
            });
        }

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void setOnePageSize(int onePageSize) {
        this.onePageSize = onePageSize;
    }

    public ExtendedRecyclerView getInnerRecyclerView() {
        return recyclerView;
    }

    public void setHasFixedSize(boolean hasFixedSize) {
        recyclerView.setHasFixedSize(hasFixedSize);
    }

    public void setRecyclePaddingTop(int top) {
        recyclerView.setPadding(0, top, 0, 0);
    }

    public int getRecyclePaddingTop() {
        return recyclerView.getPaddingTop();
    }


    public void setUpDefault() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadMoreFooter = new DefaultLoadMoreView(getContext());
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
        if (loadMoreListener != null) {
            recyclerView.setLoadMoreProvider(this);
        }
    }

    public ExtendedRecyclerAdapter getAdapter() {
        return recyclerView.getAdapter();
    }

    @Override
    public void addHeaderView(@NonNull View view) {
        recyclerView.addHeaderView(view);
    }

    @Override
    public void addHeaderView(@NonNull View view,int position) {
        recyclerView.addHeaderView(view, position);
    }

    @Override
    public boolean isHeaderView(View header) {
        return recyclerView.isHeaderView(header);
    }

    @Override
    public boolean isHeaderView(int postiion) {
        return recyclerView.isHeaderView(postiion);
    }

    @Override
    public void removeHeaderView(View view) {
        recyclerView.removeHeaderView(view);
    }

    @Override
    public int getHeaderViewCount() {
        return recyclerView.getHeaderViewCount();
    }

    @Override
    public void addFooterView(@NonNull View view) {
        recyclerView.addFooterView(view);
    }

    @Override
    public boolean isFooterView(View footer) {
        return recyclerView.isFooterView(footer);
    }

    @Override
    public boolean isFooterView(int postiion) {
        return recyclerView.isFooterView(postiion);
    }

    @Override
    public void addFooterView(@NonNull View view, int position) {
        recyclerView.addFooterView(view, position);
    }

    @Override
    public void removeFooterView(View view) {
        recyclerView.removeFooterView(view);
    }

    @Override
    public int getFooterViewCount() {
        return recyclerView.getFooterViewCount();
    }

    @Override
    public void setEmptyView(@NonNull View empty) {
        recyclerView.setEmptyView(empty);
    }

    @Nullable
    @Override
    public View getEmptyView() {
        return recyclerView.getEmptyView();
    }

    @Override
    public void setLoadMoreListener(LoadMoreListener listener) {
        this.loadMoreListener = listener;
        if (getAdapter() != null) {
            recyclerView.setLoadMoreProvider(this);
        }
    }


    @Override
    public void setLoadMoreFooter(@NonNull View v) {
        this.loadMoreFooter = v;
    }


    @Override
    public void onFinishLoadMore() {
        isLoadingMore = false;
    }

    @Override
    public void showLoadMore(int count) {
        if (!hasMoreItems) {
            return;
        }
        ExtendedRecyclerAdapter adapter = recyclerView.getAdapter();
        if (adapter == null) {
            return;
        }
        if (loadMoreFooter == null) {
            loadMoreFooter = new DefaultLoadMoreView(getContext());
        }
        if (count >= onePageSize) {
            if (!recyclerView.isFooterView(loadMoreFooter)) {
                recyclerView.addFooterView(loadMoreFooter, 0);
            }
        } else {
            if (recyclerView.isFooterView(loadMoreFooter)) {
                recyclerView.removeFooterView(loadMoreFooter);
            }
        }
        isLoadingMore = false;
    }

    @Override
    public boolean hasLoadMoreView() {
        if (loadMoreFooter == null) {
            loadMoreFooter = new DefaultLoadMoreView(getContext());
        }
        if (recyclerView.isFooterView(loadMoreFooter)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void hideLoadMore() {
        if (recyclerView.isFooterView(loadMoreFooter)) {
            recyclerView.removeFooterView(loadMoreFooter);
        }
    }


    @Override
    public void setHasMoreItems(boolean hasMoreItems) {
        this.hasMoreItems = hasMoreItems;
        //最后一次请求刚好为pageSize,下次请求返回0的情况
        if (!hasMoreItems) {
            if (recyclerView.isFooterView(loadMoreFooter)) {
                recyclerView.removeFooterView(loadMoreFooter);
            }
        }
    }



    @Override
    public void setPullToRefreshEnabled(boolean enable) {
        isPullToRefreshEnabled = enable;
        this.setEnabled(isPullToRefreshEnabled);
    }

    @Override
    public boolean isPullToRefreshEnabled() {
        return isPullToRefreshEnabled;
    }

    @Override
    public void startToRefresh() {
        this.setRefreshing(true);
        hasMoreItems = true;
        ExtendedRecyclerAdapter adapter = getAdapter();
        if (adapter != null) {
            adapter.resetCount();
        }
    }

    @Override
    public void completeRefresh() {
        this.setRefreshing(false);
    }

    @Override
    public void setPullToRefreshListener(PullToRefreshListener listener) {
        this.refreshListener = listener;
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        recyclerView.setLayoutManager(layoutManager);
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return recyclerView.getLayoutManager();
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        recyclerView.addItemDecoration(itemDecoration);
    }

    public void scrollToPosition(int position) {
        recyclerView.scrollToPosition(position);
    }

    public void smoothScrollToPosition(int position) {
        recyclerView.smoothScrollToPosition(position);
    }


    public void addOnScrollListener(RecyclerView.OnScrollListener listener) {
        this.onScrollDelegate = listener;
    }

    public void setItemDecration(RecyclerView.ItemDecoration itemDecration) {
        recyclerView.addItemDecoration(itemDecration);
    }


    /**
     * 防止顶部下拉刷新可能触发上拉加载更多
     */
    private float startY;
    private float startX;
    private boolean mIsUp;
    private float mTouchSlop;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIsUp = false;
                // 记录手指按下的位置
                startY = ev.getY();
                startX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:

                // 获取当前手指位置
                float endY = ev.getY();
                float distanceY = Math.abs(endY - startY);
                float distanceY2 = endY - startY;
                if (distanceY > mTouchSlop && distanceY2 < 0) {
                    mIsUp = true;
                } else {
                    mIsUp = false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {

        private int lastVisiblePosition = RecyclerView.NO_POSITION;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (onScrollDelegate != null) {
                onScrollDelegate.onScrolled(recyclerView, dx, dy);
            }

            if (SwipeRefreshRecyclerView.this.recyclerView.getLayoutManager() == null) {
                return;
            }
            lastVisiblePosition = RecyclerviewUtils.findLastVisibleItemPosition(SwipeRefreshRecyclerView.this.recyclerView.getLayoutManager());
            if (Build.VERSION.SDK_INT < 21) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                SwipeRefreshRecyclerView.this.setEnabled(topRowVerticalPosition >= recyclerView.getPaddingTop() && isPullToRefreshEnabled);
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            //区分滚动状态设置图片加载，提升滚动性能
            if (newState == RecyclerView.SCROLL_STATE_IDLE ||
                    newState == RecyclerView.SCROLL_STATE_DRAGGING) {
//                ImageLoader.resume();
            } else if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
//                ImageLoader.pause();
            }
            if (onScrollDelegate != null) {
                onScrollDelegate.onScrollStateChanged(recyclerView, newState);
            }

            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager == null) {
                return;
            }
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (adapter == null) {
                return;
            }
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();

            //如果不是正处于滑动阶段，并且已经滑到了最后一项,减去footer
            if ((visibleItemCount > 0 && newState == RecyclerView.SCROLL_STATE_IDLE && (lastVisiblePosition) >= totalItemCount - 1)
                    && mIsUp) {
                if (!isLoadingMore && hasMoreItems && loadMoreListener != null && adapter.getItemCount() >= smallPageSize) {
                    loadMoreListener.loadMore();
                    isLoadingMore = true;
                }
            }
        }

    };

}
