package com.app.mvpdemo.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用的ViewHolder
 * <p/>
 * Created by weiwen on 16/12/30.
 */
@SuppressWarnings("unused")
public abstract class BaseListAdapter<T> extends BaseAdapter {
    protected LayoutInflater mInflater;
    private List<T> mDatas;
    private List<T> mPreData;
    protected Context mContext;

    public BaseListAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext=context;
        this.mDatas = new ArrayList<T>();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        if (position >= 0 && position < mDatas.size())
            return mDatas.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        T time = getItem(position);
        Log.d("HorizontalPurchaseItem", "position-"+ position + "--" + time);
        int layoutId = getLayoutId(position, time);
        final ViewHolder vh = ViewHolder.getViewHolder(mInflater, convertView, parent, layoutId, position);
        convert(vh, time, position);
        return vh.getConvertView();
    }

    public List<T> getDatas() {
        return this.mDatas;
    }

    protected abstract void convert(ViewHolder vh, T item, int position);

    protected abstract int getLayoutId(int position, T item);

    public void updateItem(int location, T item) {
        if (mDatas.isEmpty()) return;
        mDatas.set(location, item);
        notifyDataSetChanged();
    }

    public void addItem(T item) {
        checkListNull();
        mDatas.add(item);
        notifyDataSetChanged();
    }

    public void addItem(int location, T item) {
        checkListNull();
        mDatas.add(location, item);
        notifyDataSetChanged();
    }

    public void addItems(List<T> items) {
        checkListNull();
        if (items != null) {
            List<T> date = new ArrayList<>();
            if (mPreData != null) {
                for (T d : items) {
                    if (!mPreData.contains(d)) {
                        date.add(d);
                    }
                }
            } else {
                date = items;
            }
            mPreData = items;
            mDatas.addAll(date);
        }
        notifyDataSetChanged();
    }

    public void addItems(int position, List<T> items) {
        checkListNull();
        mDatas.addAll(position, items);
        notifyDataSetChanged();
    }

    public void removeItem(int location) {
        if (mDatas == null || mDatas.isEmpty()) {
            return;
        }
        mDatas.remove(location);
        notifyDataSetChanged();
    }

    public void clear() {
        if (mDatas == null || mDatas.isEmpty()) {
            return;
        }
        mPreData = null;
        mDatas.clear();
        notifyDataSetChanged();
    }

    public void checkListNull() {
        if (mDatas == null) {
            mDatas = new ArrayList<T>();
        }
    }

    public int getCurrentPage() {
        return getCount() % 20;
    }

}
