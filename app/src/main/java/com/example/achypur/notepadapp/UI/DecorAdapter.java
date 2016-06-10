package com.example.achypur.notepadapp.ui;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.achypur.notepadapp.R;

class DecorAdapter extends BaseAdapter {

    interface Listener {
        void onRemoveClicked(int position);
    }

    LayoutInflater mLayoutInflater;
    BaseAdapter mAdapter;
    Listener mListener;

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void setAdapter(BaseAdapter gridViewAdapter) {
        mAdapter = gridViewAdapter;
        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                notifyDataSetChanged();
            }

            @Override
            public void onInvalidated() {
                notifyDataSetInvalidated();
            }
        });
    }


    public DecorAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return mAdapter.getCount();
    }

    @Override
    public Object getItem(int position) {
        return mAdapter.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ImageView cancelView;
        ViewGroup decor = (ViewGroup) mLayoutInflater.inflate(R.layout.picture_cancel_layout, parent, false);
        final View child = mAdapter.getView(position, null, decor);
        decor.addView(child);
        convertView = decor;
        cancelView = (ImageView) convertView.findViewById(R.id.note_cancel_picture);
        cancelView.bringToFront();
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.onRemoveClicked(position);
                }
            }
        });

        return convertView;
    }
}