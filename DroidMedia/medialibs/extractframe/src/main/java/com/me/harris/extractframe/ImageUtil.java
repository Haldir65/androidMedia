package com.me.harris.extractframe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;

public class
ImageUtil {

    @NonNull
    @NotNull
    public static Bitmap yuvDataToBitMap(byte[] data,int pWidth,int pHeight,int bWidth,int bHeight ) {
        // pWidth and pHeight define the size of the preview Frame
        ByteArrayOutputStream out = new ByteArrayOutputStream(data.length);

// Alter the second parameter of this to the actual format you are receiving
        YuvImage yuv = new YuvImage(data, ImageFormat.NV21, pWidth, pHeight, null);

// bWidth and bHeight define the size of the bitmap you wish the fill with the preview image
        yuv.compressToJpeg(new Rect(0, 0, bWidth, bHeight), 100, out);
        byte[] bytes = out.toByteArray();
        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }


}
