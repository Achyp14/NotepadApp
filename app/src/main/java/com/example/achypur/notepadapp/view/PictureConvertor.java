package com.example.achypur.notepadapp.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PictureConvertor {

    public static PictureConvertor instance;

    private PictureConvertor(){};

    public static synchronized PictureConvertor getInstance() {
        if(instance == null) {
            instance = new PictureConvertor();
        }
        return instance;
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public Bitmap byteToBitMap(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
