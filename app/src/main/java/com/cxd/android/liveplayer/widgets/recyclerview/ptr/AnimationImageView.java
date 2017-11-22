package com.cxd.android.liveplayer.widgets.recyclerview.ptr;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Rancune@126.com on 2016/7/4.
 */
@SuppressWarnings("unused")
public class AnimationImageView extends ImageView {
    private FrameAnimationListener mListener;
    private AnimationDrawable mAnimation;

    public AnimationImageView(Context context) {
        this(context, null, 0);
    }

    public AnimationImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimationImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        startPlay();
    }

    private void startPlay() {
        Drawable drawable = getBackground();
        if (!(drawable instanceof AnimationDrawable)) {
            return;
        }
        mAnimation = (AnimationDrawable) getBackground();
        post(new Runnable() {
            @Override
            public void run() {
                if (mAnimation == null) {
                    return;
                }
                mAnimation.start();
                if (mListener != null) {
                    mListener.onStart();
                }
                if (mAnimation.isOneShot()) {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mListener != null) {
                                mListener.onEnd();
                            }
                        }
                    }, getTotalDuration());
                }
            }
        });

    }

    public void setAnimationDrawable(@DrawableRes int drawableRes) {
        if (drawableRes == 0) {
            return;
        }
        setBackgroundResource(drawableRes);
        startPlay();
    }

    public int getTotalDuration() {
        int totalTime = 0;
        if (mAnimation != null) {
            for (int i = 0; i < mAnimation.getNumberOfFrames(); ++i) {
                totalTime += mAnimation.getDuration(i);
            }
        }
        return totalTime;
    }

    public void setAnimationListener(FrameAnimationListener listener) {
        this.mListener = listener;
    }

}
