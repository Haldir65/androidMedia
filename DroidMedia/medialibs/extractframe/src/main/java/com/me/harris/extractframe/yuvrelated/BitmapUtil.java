package com.me.harris.extractframe.yuvrelated;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class BitmapUtil {


    //
//    byte[] i420RorateBytes = BitmapUtil.rotateYUV420Degree90(i420bytes, width, height);
//    byte[] nv21bytes = BitmapUtil.I420Tonv21(i420RorateBytes, height, width);
//    //TODO check YUV数据是否正常
////                BitmapUtil.dumpFile("mnt/sdcard/1.yuv", i420bytes);
//
//    Bitmap bitmap = BitmapUtil.getBitmapImageFromYUV(nv21bytes, height, width);

    public static byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        // Rotate the Y luma
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }
        // Rotate the U and V color components
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i--;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                i--;
            }
        }
        return yuv;
    }


    public static Bitmap getBitmapImageFromYUV(byte[] data, int width, int height) {
        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, width, height), 80, baos);
        byte[] jdata = baos.toByteArray();
        BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
        bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length, bitmapFatoryOptions);
        return bmp;
    }

    /**
     * I420转nv21
     */
    public static byte[] I420Tonv21(byte[] data, int width, int height) {
        byte[] ret = new byte[data.length];
        int total = width * height;

        ByteBuffer bufferY = ByteBuffer.wrap(ret, 0, total);
        ByteBuffer bufferVU = ByteBuffer.wrap(ret, total, total / 2);

        bufferY.put(data, 0, total);
        for (int i = 0; i < total / 4; i += 1) {
            bufferVU.put(data[i + total + total / 4]);
            bufferVU.put(data[total + i]);
        }

        return ret;
    }

    public static byte[] toNv21(Image yuv420Image) {
        int width = yuv420Image.getWidth();
        int height = yuv420Image.getHeight();

        // Order of U/V channel guaranteed, read more:
        // https://developer.android.com/reference/android/graphics/ImageFormat#YUV_420_888
        Image.Plane yPlane = yuv420Image.getPlanes()[0];
        Image.Plane uPlane = yuv420Image.getPlanes()[1];
        Image.Plane vPlane = yuv420Image.getPlanes()[2];

        ByteBuffer yBuffer = yPlane.getBuffer();
        ByteBuffer uBuffer = uPlane.getBuffer();
        ByteBuffer vBuffer = vPlane.getBuffer();

        // Full size Y channel and quarter size U+V channels.
        int numPixels = (int) (width * height * 1.5f);
        byte[] nv21 = new byte[numPixels];
        int idY = 0;
        int idUV = width * height;
        int uvWidth = width / 2;
        int uvHeight = height / 2;

        // Copy Y & UV channel.
        // NV21 format is expected to have YYYYVU packaging.
        // The U/V planes are guaranteed to have the same row stride and pixel stride.
        int uvRowStride = uPlane.getRowStride();
        int uvPixelStride = uPlane.getPixelStride();
        int yRowStride = yPlane.getRowStride();
        int yPixelStride = yPlane.getPixelStride();
        for(int y = 0; y < height; ++y) {
            int yOffset = y * yRowStride;
            int uvOffset = y * uvRowStride;

            for (int x = 0; x < width; ++x) {
                nv21[idY++] = yBuffer.get(yOffset + x * yPixelStride);

                if (y < uvHeight && x < uvWidth) {
                    int bufferIndex = uvOffset + (x * uvPixelStride);
                    // V channel.
                    nv21[idUV++] = vBuffer.get(bufferIndex);
                    // U channel.
                    nv21[idUV++] = uBuffer.get(bufferIndex);
                }
            }
        }

        return nv21;
    }

}
