package com.cxd.android.liveplayer.widgets.abslistview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class KasListView extends ListView {

	public KasListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setScrollingCacheEnabled(false);
	}

	public KasListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setScrollingCacheEnabled(false);
	}

	public KasListView(Context context) {
		super(context);
		this.setScrollingCacheEnabled(false);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	

}
