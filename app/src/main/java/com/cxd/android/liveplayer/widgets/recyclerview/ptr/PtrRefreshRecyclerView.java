package com.cxd.android.liveplayer.widgets.recyclerview.ptr;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

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

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by Rancune@126.com on 2016/10/21.
 */

@SuppressWarnings("unused")
public class PtrRefreshRecyclerView extends PtrFrameLayout implements HeaderAndFooterProvider,
        LoadMoreProvider, PullToRefreshProvider {
    /**
     * we use extendedRecyclerView instead of normal RecyclerView
     */
    private ExtendedRecyclerView recyclerView;

    /**
     * view to be shown when the recycleview scrolls to the bottom
     */
    private View loadMoreFooter;

    /**
     * load more listener
     * if this is null, then loadmore is disabled
     */
    private LoadMoreListener loadMoreListener;

    /**
     * whether is auto loading more this moment
     */
    private boolean isLoadingMore = false;

    /**
     * whether the recyclerview has more items
     */
    private boolean hasMoreItems = true;

    /**
     * whether pull-to-refresh is enabled
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

    protected int mPaddingTop;
    protected int mPaddingBottom;
    protected int mPaddingLeft;
    protected int mPaddingRight;
    protected boolean mClipToPadding;


    public PtrRefreshRecyclerView(Context context) {
        this(context, null, 0);
    }

    public PtrRefreshRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PtrRefreshRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(attrs);
        recyclerView = new ExtendedRecyclerView(context, attrs, defStyle);
        recyclerView.setId(R.id.zues_ptrrefresh_recyclerview);
//        recyclerView.getItemAnimator().setSupportsChangeAnimations(false);
        RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();
        if(itemAnimator instanceof SimpleItemAnimator){
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(mClipToPadding);
        recyclerView.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
        addView(recyclerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        setPullToRefreshEnabled(isPullToRefreshEnabled);
        CustomRefreshHeader header = new CustomRefreshHeader(getContext());
        super.setHeaderView(header);
        super.addPtrUIHandler(header);
        super.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
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
        recyclerView.addOnScrollListener(mOnScrollListener);
    }


    protected void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PtrRefreshRecyclerView);
        try {
            mPaddingTop = (int) typedArray.getDimension(R.styleable.PtrRefreshRecyclerView_recyclerviewPaddingTop, 0.0f);
            mPaddingBottom = (int) typedArray.getDimension(R.styleable.PtrRefreshRecyclerView_recyclerviewPaddingBottom, 0.0f);
            mPaddingLeft = (int) typedArray.getDimension(R.styleable.PtrRefreshRecyclerView_recyclerviewPaddingLeft, 0.0f);
            mPaddingRight = (int) typedArray.getDimension(R.styleable.PtrRefreshRecyclerView_recyclerviewPaddingRight, 0.0f);
            mClipToPadding = typedArray.getBoolean(R.styleable.PtrRefreshRecyclerView_recyclerviewClipToPadding, true);
        } finally {
            typedArray.recycle();
        }
    }

    public int getRecyclerPaddingTop() {
        return recyclerView.getPaddingTop();
    }

    public ExtendedRecyclerView getInnerRecyclerView() {
        return recyclerView;
    }

    public void setHasFixedSize(boolean hasFixedSize) {
        recyclerView.setHasFixedSize(hasFixedSize);
    }

    public void setOnePageSize(int onePageSize) {
        this.onePageSize = onePageSize;
    }

    /**
     * set default settings
     * the receclerview act as a vertical listview
     * the PtrFramlayout uses the {@link in.srain.cube.views.ptr.PtrClassicDefaultHeader} as header view
     * this whole view supports pull-to-refresh, load more ,header ,footer, emptyview
     */
    public void setUpDefault() {
        //default vertical linearlayoutmanager
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

    /**
     * Be careful: the target of addHeaderView is the recyclerView
     * the target of setHeaderView is the PtrFrameLayout
     *
     * @param view view header
     */
    @Override
    public void addHeaderView(@NonNull View view) {
        recyclerView.addHeaderView(view);
    }

    @Override
    public void addHeaderView(@NonNull View view, int position) {
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
    public void setEmptyView(@NonNull View emptyView) {
        recyclerView.setEmptyView(emptyView);
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
        return recyclerView.isFooterView(loadMoreFooter);
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
    public void setPullToRefreshEnabled(boolean enabled) {
        isPullToRefreshEnabled = enabled;
        super.setEnabled(isPullToRefreshEnabled);
    }

    @Override
    public boolean isPullToRefreshEnabled() {
        return isPullToRefreshEnabled;
    }

    @Override
    public void startToRefresh() {
        super.autoRefresh();
    }

    @Override
    public void completeRefresh() {
        super.refreshComplete();
    }

    @Override
    public void setPullToRefreshListener(PullToRefreshListener listener) {
        View header = super.getHeaderView();
        if (header == null) {
            throw new IllegalStateException("should call setUpDefault() first, or you can add your custom");
        }
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

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {

        private int lastVisiblePosition = RecyclerView.NO_POSITION;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (onScrollDelegate != null) {
                onScrollDelegate.onScrolled(recyclerView, dx, dy);
            }

            if (PtrRefreshRecyclerView.this.recyclerView.getLayoutManager() == null) {
                return;
            }
            lastVisiblePosition = RecyclerviewUtils.findLastVisibleItemPosition(PtrRefreshRecyclerView.this.recyclerView.getLayoutManager());
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
            if ((visibleItemCount > 0 && newState == RecyclerView.SCROLL_STATE_IDLE && (lastVisiblePosition) >= totalItemCount - 1)) {
                if (!isLoadingMore && hasMoreItems && loadMoreListener != null && adapter.getItemCount() >= smallPageSize) {
                    loadMoreListener.loadMore();
                    isLoadingMore = true;
                }
            }
        }

    };
}
