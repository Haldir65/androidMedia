package com.harris.androidMedia.album;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.harris.androidMedia.util.Constants;
import com.harris.androidMedia.util.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 工具类，用于获得要加载的图片资源
 *
 * @author carrey
 */
public class ImageHelper {

    private static final String TAG = "ImageHelper";
    private static Random random;
    private static String[] array = new String[]{Constants.IMG_URL_1, Constants.IMG_URL_2, Constants.IMG_URL_3, Constants.IMG_URL_4, Constants.IMG_URL_5};

    public static String getImageUrl(int position) {
        if (random == null) {
            random = new Random();
        }
        return array[random.nextInt(5)];
    }

    public static List<String> getDummyStringList(int size) {
        if (random == null) {
            random = new Random();
        }
        List<String> res = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            res.add(array[random.nextInt(5)]);
        }
        return res;
    }

    /**
     * 获得网络图片Bitmap
     *
     * @param
     * @return
     */
    public static Bitmap loadBitmapFromNet(String imageUrlStr) {
        Bitmap bitmap = null;
        URL imageUrl = null;
        LogUtil.e("load url " + imageUrlStr+" currentThreadId " +Thread.currentThread().getId());
        if (imageUrlStr == null || imageUrlStr.length() == 0) {
            return null;
        }
        try {
            imageUrl = new URL(imageUrlStr);
            URLConnection conn = imageUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            int length = conn.getContentLength();
            if (length != -1) {
                byte[] imgData = new byte[length];
                byte[] temp = new byte[512];
                int readLen = 0;
                int destPos = 0;
                while ((readLen = is.read(temp)) != -1) {
                    System.arraycopy(temp, 0, imgData, destPos, readLen);
                    destPos += readLen;
                }
                bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
                LogUtil.e("bitmap loaded! ");
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }
        return bitmap;
    }
}
