package com.example.achypur.notepadapp;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BitmapDecoder extends AsyncTask<InputStream, Void, Bitmap> {

    InputStream mStream;

    @Override
    protected Bitmap doInBackground(InputStream... params) {
        mStream = params[0];

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            copy(mStream, out);
            InputStream newStream = new ByteArrayInputStream(out.toByteArray());
            return decodeSampledBitmapFromInputStream(mStream, newStream, 1, 1);

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public int copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int count = 0;
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromInputStream(InputStream in,
                                                            InputStream copyOfIn, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(copyOfIn, null, options);
    }
}
