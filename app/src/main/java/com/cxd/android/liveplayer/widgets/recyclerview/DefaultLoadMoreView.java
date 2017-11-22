package com.cxd.android.liveplayer.widgets.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.cxd.android.liveplayer.R;


/**
 * Created by Rancune@126.com on 2016/1/20.
 */
public class DefaultLoadMoreView extends RelativeLayout {

    public DefaultLoadMoreView(Context context) {
        this(context, null, 0);
    }

    public DefaultLoadMoreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultLoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.zues_recyclerview_load_more_footer, this, true);
    }
}
