package com.example.achypur.tagview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TagView extends ViewGroup {

    List<String> mList = new ArrayList<>();
    int mDeviceWidth;
    Listener mListener;

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void setList(List<String> list) {
        int i = 0;
        mList = list;
        removeAllViews();
        for (String tag : mList) {
            colorGenerator(R.drawable.circle, R.id.circle_shape, R.array.item_colors_dark, i);
            colorGenerator(R.drawable.item_effect, R.id.shape, R.array.item_colors, i);
            addView(createTagView(tag));
            i++;

            if (i > 5) {
                i = 0;
            }
        }
        addView(createAddTag());
    }

    public TagView(Context context) {
        super(context);
        init(context);
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        setList(mList);
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point deviceDisplay = new Point();
        display.getSize(deviceDisplay);
        mDeviceWidth = deviceDisplay.x;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();

        int curWidth, curHeight, curLeft, curTop, maxHeight;

        final int childLeft = this.getPaddingLeft();
        final int childTop = this.getPaddingTop();
        final int childRight = this.getMeasuredWidth() - this.getPaddingRight();
        final int childBottom = this.getMeasuredHeight() - this.getPaddingBottom();
        final int childWidth = childRight - childLeft;
        final int childHeight = childBottom - childTop;

        maxHeight = 0;
        curLeft = childLeft;
        curTop = childTop;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == GONE)
                return;

            child.measure(View.MeasureSpec.makeMeasureSpec(childWidth, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(childHeight, View.MeasureSpec.AT_MOST));
            curWidth = child.getMeasuredWidth();
            curHeight = child.getMeasuredHeight();

            if (i == count - 1) {
                curHeight = Math.max(maxHeight, curHeight);
            }

            if (curLeft + curWidth >= childRight) {
                curLeft = childLeft;
                curTop += maxHeight;
                maxHeight = 0;
            }

            child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight);
            if (maxHeight < curHeight)
                maxHeight = curHeight;
            curLeft += curWidth;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        int totalHeight = 0;
        int maxWidth = 0;
        int childState = 0;

        int curRowWidth = 0;
        int curRowMaxHeight = 0;
        int rowCount = 1;


        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE)
                continue;

            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            maxWidth += Math.max(maxWidth, child.getMeasuredWidth());

            if ((curRowWidth + child.getMeasuredWidth()) > getMeasuredWidth()) {
                rowCount++;
                totalHeight += Math.max(curRowMaxHeight, child.getMeasuredHeight());
                curRowMaxHeight = (i == count - 1) ? curRowMaxHeight : child.getMeasuredHeight();
                curRowWidth = 0;
            } else if (curRowMaxHeight < child.getMeasuredHeight()) {
                curRowMaxHeight = child.getMeasuredHeight();
            }

            curRowWidth += child.getMeasuredWidth();
            childState = combineMeasuredStates(childState, child.getMeasuredState());
        }

        totalHeight += curRowMaxHeight;
        totalHeight = Math.max(totalHeight, getSuggestedMinimumHeight());

        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(totalHeight, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));

    }

    public View createTagView(final String tag) {
        View root = inflate(getContext(), R.layout.tag_view_item, null);
        final TextView textView = (TextView) root.findViewById(R.id.text);
        final ImageView delete = (ImageView) root.findViewById(R.id.close);
        final LinearLayout linearLayout = (LinearLayout) root.findViewById(R.id.linear_layout);
        textView.setText(tag.toLowerCase());

        if(!isEnabled()) {
            delete.setVisibility(GONE);
        }

        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    linearLayout.setVisibility(GONE);
                    mListener.onRemovingTag(tag);
                }
            }
        });
        return root;
    }

    private View createAddTag() {
        View root = inflate(getContext(), R.layout.edit_layout, null);
        final ImageView imageView = (ImageView) root.findViewById(R.id.plus);
        final EditText editText = (EditText) root.findViewById(R.id.edit_text);

        if(isEnabled()) {
            imageView.setVisibility(VISIBLE);
        } else {
            imageView.setVisibility(GONE);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setVisibility(GONE);
                editText.setVisibility(VISIBLE);
                editText.requestFocus();
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    if (mListener != null) {
                        String item = editText.getText().toString();
                        editText.setText("");
                        editText.clearFocus();
                        mListener.onAddingTag(item);
                        setList(mList);
                        return true;
                    }
                }
                return false;
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    editText.setVisibility(GONE);
                    editText.setText("");
                    editText.clearFocus();
                    imageView.setVisibility(VISIBLE);
                }
            }
        });

        return root;
    }

    private void colorGenerator(int drawableId, int shapeId, int arrayId, int position) {
        int[] itemColor = getResources().getIntArray(arrayId);
        LayerDrawable layerDrawable = (LayerDrawable) ContextCompat.getDrawable(getContext(), drawableId);
        GradientDrawable gradientDrawable = (GradientDrawable) layerDrawable.findDrawableByLayerId(shapeId);
        gradientDrawable.setColor(itemColor[position]);
    }

    public interface Listener {
        void onAddingTag(String tag);

        void onRemovingTag(String tag);
    }

    public void addTag(String tag) {
        mList.add(tag);
    }

    public void removeTag(String tag) {
        mList.remove(tag);
    }
}
