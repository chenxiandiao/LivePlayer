package com.cxd.android.liveplayer.widgets.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.cxd.android.liveplayer.widgets.recyclerview.listener.DateSetWatcher;
import com.cxd.android.liveplayer.widgets.recyclerview.provider.LoadMoreProvider;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


/**
 * Created by Rancune@126.com on 2016/4/28.
 * <p>
 * an enhanced recyclerView adapter using decrator pattern
 * support adding header view, adding footer view, setting empty view
 */
@SuppressWarnings("unused")
public class ExtendedRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER_VIEW = Integer.MIN_VALUE;
    private static final int TYPE_FOOTER_VIEW = Integer.MIN_VALUE + 1;

    private LinearLayout mHeaderLayout;
    private LinearLayout mFooterLayout;
    private RecyclerView.Adapter innerAdapter;
    private DateSetWatcher emptyListener;
    private LoadMoreProvider loadMoreProvider;
    private int preCount = 0;
    private int nowCount = 0;

    public ExtendedRecyclerAdapter(RecyclerView.Adapter adapter) {
        setAdapter(adapter);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        preCount = 0;
        nowCount = 0;
        if (innerAdapter != null) {
            notifyItemRangeRemoved(getHeaderViewsCount(), innerAdapter.getItemCount());
            innerAdapter.unregisterAdapterDataObserver(observer);
        }
        this.innerAdapter = adapter;
        innerAdapter.registerAdapterDataObserver(observer);
        notifyItemRangeInserted(getHeaderViewsCount(), innerAdapter.getItemCount());
    }

    public void setEmptyListener(DateSetWatcher listener) {
        this.emptyListener = listener;
    }

    public void setLoadMoreProvider(LoadMoreProvider loadMoreProvider) {
        this.loadMoreProvider = loadMoreProvider;
    }

    public void resetCount() {
        preCount = 0;
        nowCount = 0;
    }

    @Override
    public int getItemViewType(int position) {
        int innerCount = innerAdapter.getItemCount();
        int headerCount = getHeaderViewsCount();
        if (position < headerCount) {
            return TYPE_HEADER_VIEW;
        } else if (position >= headerCount && position < headerCount + innerCount) {
            int innerPosition = position - getHeaderViewsCount();
            int innerType = innerAdapter.getItemViewType(innerPosition);
            if (innerType >= Integer.MAX_VALUE / 2) {
                throw new IllegalArgumentException("item view type in inner adapter should not be greater than Integer.MAX_VALUE / 2  ");
            }
            return innerType + Integer.MAX_VALUE / 2;
        } else {
            return TYPE_FOOTER_VIEW;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int headerViewsCountCount = getHeaderViewsCount();
        if (viewType == TYPE_HEADER_VIEW ){
            return new ViewHolder(mHeaderLayout);
        } else if (viewType == TYPE_FOOTER_VIEW) {
            return new ViewHolder(mFooterLayout);
        } else {
            return innerAdapter.createViewHolder(parent, viewType - Integer.MAX_VALUE / 2);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int headerViewsCountCount = getHeaderViewsCount();
        if (position >= headerViewsCountCount && position < headerViewsCountCount + innerAdapter.getItemCount()) {
            //noinspection unchecked
            innerAdapter.onBindViewHolder(holder, position - headerViewsCountCount);
        } else {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                ((StaggeredGridLayoutManager.LayoutParams) layoutParams).setFullSpan(true);
            }
        }
    }

    @Override
    public int getItemCount() {
        return getHeaderViewsCount() + innerAdapter.getItemCount() + getFooterViewsCount();
    }

    public int getHeaderViewsCount() {
        if (mHeaderLayout == null || mHeaderLayout.getChildCount() == 0) {
            return 0;
        }
        return 1;
    }

    public int getFooterViewsCount() {
        if (mFooterLayout == null || mFooterLayout.getChildCount() == 0) {
            return 0;
        }
        return 1;
    }

    /**
     * Append header to the rear of the mHeaderLayout.
     *
     * @param header
     */
    public void addHeaderView(View header) {
        addHeaderView(header, -1);
    }

    /**
     * Add header view to mHeaderLayout and set header view position in mHeaderLayout.
     * When index = -1 or index >= child count in mHeaderLayout,
     * the effect of this method is the same as that of {@link #addHeaderView(View)}.
     *
     * @param header
     * @param index  the position in mHeaderLayout of this header.
     *               When index = -1 or index >= child count in mHeaderLayout,
     *               the effect of this method is the same as that of {@link #addHeaderView(View)}.
     */
    public void addHeaderView(View header, int index) {
        addHeaderView(header, index, LinearLayout.VERTICAL);
    }

    /**
     * @param header
     * @param index
     * @param orientation
     */
    public void addHeaderView(View header, int index, int orientation) {
        if (mHeaderLayout == null) {
            mHeaderLayout = new LinearLayout(header.getContext());
            if (orientation == LinearLayout.VERTICAL) {
                mHeaderLayout.setOrientation(LinearLayout.VERTICAL);
                mHeaderLayout.setClipChildren(false);
                mHeaderLayout.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            } else {
                mHeaderLayout.setOrientation(LinearLayout.HORIZONTAL);
                mHeaderLayout.setLayoutParams(new RecyclerView.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
            }
        }
        index = index >= mHeaderLayout.getChildCount() ? -1 : index;
        mHeaderLayout.addView(header, index);
        if (mHeaderLayout.getChildCount() == 1) {
//            notifyItemInserted(0);
            notifyDataSetChanged();
        }
    }

    /**
     * remove header view from mHeaderLayout.
     * When the child count of mHeaderLayout is 0, mHeaderLayout will be set to null.
     *
     * @param header
     */
    public void removeHeaderView(View header) {
        if (getHeaderViewsCount() == 0) return;

        mHeaderLayout.removeView(header);
        if (mHeaderLayout.getChildCount() == 0) {
            notifyItemRemoved(0);
        }
    }

    /**
     * Append footer to the rear of the mFooterLayout.
     *
     * @param footer
     */
    public void addFooterView(View footer) {
        addFooterView(footer, -1, LinearLayout.VERTICAL);
    }

    public void addFooterView(View footer, int index) {
        addFooterView(footer, index, LinearLayout.VERTICAL);
    }

    /**
     * Add footer view to mFooterLayout and set footer view position in mFooterLayout.
     * When index = -1 or index >= child count in mFooterLayout,
     * the effect of this method is the same as that of {@link #addFooterView(View)}.
     *
     * @param footer
     * @param index  the position in mFooterLayout of this footer.
     *               When index = -1 or index >= child count in mFooterLayout,
     *               the effect of this method is the same as that of {@link #addFooterView(View)}.
     */
    public void addFooterView(View footer, int index, int orientation) {
        if (mFooterLayout == null) {
            mFooterLayout = new LinearLayout(footer.getContext());
            if (orientation == LinearLayout.VERTICAL) {
                mFooterLayout.setOrientation(LinearLayout.VERTICAL);
                mFooterLayout.setClipChildren(false);
                mFooterLayout.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            } else {
                mFooterLayout.setOrientation(LinearLayout.HORIZONTAL);
                mFooterLayout.setLayoutParams(new RecyclerView.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
            }
        }
        index = index >= mFooterLayout.getChildCount() ? -1 : index;
        mFooterLayout.addView(footer, index);
        if (mFooterLayout.getChildCount() == 1) {
            notifyItemInserted(getHeaderViewsCount() + innerAdapter.getItemCount());
        }
    }

    /**
     * remove footer view from mFooterLayout,
     * When the child count of mFooterLayout is 0, mFooterLayout will be set to null.
     *
     * @param footer
     */
    public void removeFooterView(final View footer) {
        if (getFooterViewsCount() == 0) return;

        mFooterLayout.post(new Runnable() {
            @Override
            public void run() {
                mFooterLayout.removeView(footer);
                if (mFooterLayout.getChildCount() == 0) {
                    notifyItemRemoved(getHeaderViewsCount() + innerAdapter.getItemCount());
                }
            }
        });
    }


    public boolean isHeader(View header) {
        if (mHeaderLayout == null || mHeaderLayout.getChildCount() == 0) {
            return false;
        }
        for (int i = 0; i < mHeaderLayout.getChildCount(); i++) {
            if (mHeaderLayout.getChildAt(i) == header){
                return true;
            }
        }
        return false;
    }

    public boolean isHeader(int position) {
        int headerCount = getHeaderViewsCount();
        return headerCount > 0 && position >= 0 && position < headerCount;
    }


    public boolean isFooter(View footer) {
        if (mFooterLayout == null || mFooterLayout.getChildCount() == 0) {
            return false;
        }
        for (int i = 0; i < mFooterLayout.getChildCount(); i++) {
            if (mFooterLayout.getChildAt(i) == footer){
                return true;
            }
        }
        return false;
    }

    public boolean isFooter(int position) {
        int headerCount = getHeaderViewsCount();
        int footerCount = getFooterViewsCount();
        return footerCount > 0 && (position >= headerCount + innerAdapter.getItemCount()) && position < getItemCount();
    }

    private String outOfBoundsMsg(int index, int size) {
        return "Index: " + index + ", Size: " + size;
    }

    public RecyclerView.Adapter getInnerAdapter() {
        return innerAdapter;
    }

    private RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataSetChanged();
            if (emptyListener != null) {
                if (innerAdapter.getItemCount() == 0) {
                    emptyListener.isDataSetEmpty(true);
                } else {
                    emptyListener.isDataSetEmpty(false);
                }
            }
            if (loadMoreProvider != null) {
                preCount = nowCount;
                nowCount = innerAdapter.getItemCount();
                //当item项项数不变时刷新列表footer不改变
                if (nowCount != preCount) {
                    loadMoreProvider.showLoadMore(nowCount - preCount);
                }
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            notifyItemRangeChanged(positionStart + getHeaderViewsCount(), itemCount);
        }

        //insert只在请求下一页手动去触发
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            //有加载更多时特殊处理
            if (loadMoreProvider != null) {
                if (loadMoreProvider.hasLoadMoreView()) {
                    notifyItemRangeChanged(positionStart, 1);
                    notifyItemRangeInserted(positionStart + 1 + getHeaderViewsCount(), itemCount);
                } else {
                    notifyItemRangeInserted(positionStart + getHeaderViewsCount(), itemCount);
                }
                preCount = nowCount;
                nowCount = innerAdapter.getItemCount();
                //当item项项数不变时刷新列表footer不改变
                if (nowCount != preCount) {
                    loadMoreProvider.showLoadMore(nowCount - preCount);
                }
            } else {
                notifyItemRangeInserted(positionStart + getHeaderViewsCount(), itemCount);
            }
//            notifyItemRangeInserted(positionStart + getHeaderViewsCount(), itemCount);
//            if (loadMoreProvider != null) {
//                preCount = nowCount;
//                nowCount = innerAdapter.getItemCount();
//                //当item项项数不变时刷新列表footer不改变
//                if (nowCount != preCount) {
//                    loadMoreProvider.showLoadMore(nowCount - preCount);
//                }
//            }
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            notifyItemRangeRemoved(positionStart + getHeaderViewsCount(), itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            int headerViewsCountCount = getHeaderViewsCount();
            notifyItemRangeChanged(fromPosition + headerViewsCountCount, toPosition + headerViewsCountCount + itemCount);
        }

    };

    private static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
