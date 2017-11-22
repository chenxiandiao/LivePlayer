package com.cxd.android.liveplayer.widgets.abslistview;

/**
 * Created by Rancune@126.com on 2015/9/10.
 */

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

@SuppressWarnings("unused")
public abstract class CommonAdapter<T> extends BaseAdapter {
    protected Context context;
    protected List<T> data;
    protected final int itemLayoutId;

    public CommonAdapter(Context context, List<T> data, int itemLayoutId) {
        this.context = context;
        this.data = data;
        this.itemLayoutId = itemLayoutId;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(itemLayoutId, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        bind(holder, data.get(position));
        return convertView;
    }

    public abstract void bind(ViewHolder holder, T item);

    public static class ViewHolder {
        private final SparseArray<View> views;
        private View convertView;

        public ViewHolder(View v) {
            views = new SparseArray<>();
            this.convertView = v;
        }

        public <T extends View> T getView(int viewId) {
            View view = views.get(viewId);
            if (view == null) {
                view = convertView.findViewById(viewId);
                views.put(viewId, view);
            }
            //noinspection unchecked
            return (T) view;
        }

        public ViewHolder setText(int viewId, CharSequence text) {
            TextView view = getView(viewId);
            view.setText(text);
            return this;
        }

        public ViewHolder setText(int viewId, @StringRes int textRes) {
            TextView view = getView(viewId);
            view.setText(textRes);
            return this;
        }

        public ViewHolder setImageResource(int viewId, @DrawableRes int drawableRes) {
            ImageView view = getView(viewId);
            view.setImageResource(drawableRes);
            return this;
        }

        /**
         * 给FrescoThumbnailView，设置图片链接， 设置占位符
         */
        public ViewHolder setImageUrl(int id, String url, int defaultRes, int width, int height) {
//            FrescoThumbnailView view = getView(id);
//            view.loadViewIfNecessary(url, defaultRes, width, height);
            return this;
        }


        public ViewHolder setVisible(boolean visible, int... ids) {
            for (int id : ids) {
                View v = getView(id);
                v.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
            return this;
        }
    }

}

