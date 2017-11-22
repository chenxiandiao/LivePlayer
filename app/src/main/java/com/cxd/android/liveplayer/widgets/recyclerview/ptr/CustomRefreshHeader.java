package com.cxd.android.liveplayer.widgets.recyclerview.ptr;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.cxd.android.liveplayer.R;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Created by Rancune@126.com on 16/9/22.
 */
public class CustomRefreshHeader extends FrameLayout implements PtrUIHandler {
    private ImageView mIvPullToRefresh;
    private AnimationImageView mIvRefreshing;
    private TextView mTvRefreshTip;

    public CustomRefreshHeader(Context context) {
        this(context, null, 0);
    }

    public CustomRefreshHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View header = LayoutInflater.from(getContext()).inflate(R.layout.zues_recyclerview_ptr_fresh_header, this);
        mIvPullToRefresh = (ImageView) header.findViewById(R.id.iv_pull_to_fresh);
        mIvRefreshing = (AnimationImageView) header.findViewById(R.id.iv_freshing);
        mTvRefreshTip = (TextView) header.findViewById(R.id.tv_refresh_tip);
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        mIvPullToRefresh.setVisibility(VISIBLE);
        mIvRefreshing.setVisibility(GONE);
        mTvRefreshTip.setText(getContext().getString(R.string.zues_record_pull_refresh));
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        mIvPullToRefresh.setVisibility(GONE);
        mIvRefreshing.setVisibility(VISIBLE);
        mTvRefreshTip.setText(getContext().getString(R.string.zues_record_refreshing));
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        mIvPullToRefresh.setVisibility(GONE);
        mIvRefreshing.setVisibility(VISIBLE);
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        final int mOffsetToRefresh = frame.getOffsetToRefresh();
        final int currentPos = ptrIndicator.getCurrentPosY();
        final int lastPos = ptrIndicator.getLastPosY();

        if (currentPos < mOffsetToRefresh && lastPos >= mOffsetToRefresh) {
            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                mTvRefreshTip.setVisibility(VISIBLE);
                if (frame.isPullToRefresh()) {
                    mTvRefreshTip.setText(getResources().getString(R.string.zues_record_pull_refresh));
                } else {
                    mTvRefreshTip.setText(getResources().getString(R.string.zues_record_pull_refresh));
                }
            }
        } else if (currentPos > mOffsetToRefresh && lastPos <= mOffsetToRefresh) {
            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                if (!frame.isPullToRefresh()) {
                    mTvRefreshTip.setVisibility(VISIBLE);
                    mTvRefreshTip.setText(R.string.zues_record_release_refresh);
                }
            }
        }
    }
}
