package com.cxd.android.liveplayer.widgets.recyclerview;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Created by Rancune@126.com on 2016/4/28.
 */
public class RecyclerviewUtils {

    public static int findLastVisibleItemPosition(RecyclerView.LayoutManager manager) {
        if (manager != null) {
            if (manager instanceof LinearLayoutManager) {
                return ((LinearLayoutManager) manager).findLastVisibleItemPosition();
            } else if (manager instanceof StaggeredGridLayoutManager) {
                int lastVisiblePosition = RecyclerView.NO_POSITION;
                StaggeredGridLayoutManager stgManager = (StaggeredGridLayoutManager) manager;
                int[] lastPositions = new int[stgManager.getSpanCount()];
                stgManager.findLastVisibleItemPositions(lastPositions);
                if (lastPositions.length > 0) {
                    lastVisiblePosition = lastPositions[0];
                    for (int i = 1; i < lastPositions.length; ++i) {
                        if (lastPositions[i] > lastVisiblePosition) {
                            lastVisiblePosition = lastPositions[i];
                        }
                    }
                }
                return lastVisiblePosition;
            }

        }
        return RecyclerView.NO_POSITION;
    }

    public static int findFirstVisibleItemPosition(RecyclerView.LayoutManager manager) {
        if (manager != null) {
            if (manager instanceof LinearLayoutManager) {
                return ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
            } else if (manager instanceof StaggeredGridLayoutManager) {
                int firstVisiblePostion = RecyclerView.NO_POSITION;
                StaggeredGridLayoutManager stgManager = (StaggeredGridLayoutManager) manager;
                int[] firstPositions = new int[stgManager.getSpanCount()];
                stgManager.findFirstVisibleItemPositions(firstPositions);
                if (firstPositions.length > 0) {
                    firstVisiblePostion = firstPositions[0];
                    for (int i = 1; i < firstPositions.length; ++i) {
                        if (firstPositions[i] < firstVisiblePostion) {
                            firstVisiblePostion = firstPositions[i];
                        }
                    }
                }
                return firstVisiblePostion;
            }

        }
        return RecyclerView.NO_POSITION;
    }



    public static void setGridHeaderFullSpan(final GridLayoutManager manager, final ExtendedRecyclerAdapter adapter) {
        if (manager == null || adapter == null) {
            return;
        }
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.isHeader(position) || adapter.isFooter(position) ? manager.getSpanCount() : 1;
            }
        });
    }
}
