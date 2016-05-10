package com.example.achypur.notepadapp.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.example.achypur.notepadapp.R;

public class ProfilePicture extends ImageView {

    private int mBorderSize;

    public int getBorderSize() {
        return mBorderSize;
    }

    public void setBorderSize(int mBorderSize) {
        this.mBorderSize = mBorderSize;
    }

    public ProfilePicture(Context context) {
        super(context);
    }

    public ProfilePicture(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProfilePicture, 0, 0);
        try {
            setBorderSize((int) typedArray.getDimension(R.styleable.ProfilePicture_borderSize, 100.0f));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            typedArray.recycle();
        }
    }

    public ProfilePicture(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();


        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(getBorderSize());
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - getBorderSize() / 2, paint);

    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        Bitmap roundBorderedBitmap = bm.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap result = rectangleToSquare(roundBorderedBitmap);
        result = drawCroppedImage(result);
        super.setImageBitmap(result);
    }

    public Bitmap rectangleToSquare(Bitmap bitmap) {
        Bitmap currentBitmap;
        if (bitmap.getWidth() < bitmap.getHeight()) {
            currentBitmap = Bitmap.createBitmap(bitmap, 0, bitmap.getWidth() / 4, bitmap.getWidth(), bitmap.getWidth());
        } else if (bitmap.getWidth() > bitmap.getHeight()) {
            currentBitmap = Bitmap.createBitmap(bitmap, bitmap.getHeight() / 4, 0, bitmap.getHeight(), bitmap.getHeight());
        } else {
            currentBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        }
        return currentBitmap;
    }


    public Bitmap drawBorder(Bitmap bitmap, int borderSize) {
        Bitmap currentBitMap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(currentBitMap);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(getBorderSize());
        canvas.drawCircle(currentBitMap.getWidth() / 2, currentBitMap.getHeight() / 2, currentBitMap.getWidth() / 2 - getBorderSize() / 2, paint);

        return currentBitMap;
    }

    public Bitmap drawCroppedImage(Bitmap bitmap) {
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);
        Bitmap roundedBorderedBitMap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundedBorderedBitMap);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getHeight() / 2, paint);

        return roundedBorderedBitMap;
    }
}
