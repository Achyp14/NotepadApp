package com.example.achypur.notepadapp;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Checkable;
import android.widget.LinearLayout;

import java.util.jar.Attributes;

public class CheckableLinearLayout extends LinearLayout implements Checkable {
    private static final String TAG = "CheckableLinearLayout";
    private boolean mChecked;

    public CheckableLinearLayout(Context context) {
        super(context);
    }

    public CheckableLinearLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public void setChecked(boolean b) {
        Log.d(TAG, "setChecked: " + b);
        if (b != mChecked) {

            mChecked = b;
            refreshDrawableState();
        }
    }

    @Override
    public boolean isChecked() {
        Log.d(TAG, "isChecked: " + mChecked);
        return mChecked;
    }

    @Override
    public void toggle() {
        Log.d(TAG, "toggle: " + mChecked + " -> " + !mChecked);
        setChecked(!mChecked);
    }


}

