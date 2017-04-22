package com.harris.androidMedia.album;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.harris.androidMedia.App;
import com.harris.androidMedia.util.Constants;
import com.harris.androidMedia.util.LogUtil;
import com.harris.androidMedia.util.Utils;

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

    public static int getMemoryCacheSize(Context context) {
        // Get memory class of this device, exceeding this amount will throw an
        // OutOfMemory exception.
        final int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        // Use 1/8th of the available memory for this memory cache.
        return 1024 * 1024 * memClass / 8;
    }

    public static volatile LruCache<String, Bitmap> mMemoryCache;

    /**
     * @param key
     * @param bitmap
     * @description 将bitmap添加到内存中去
     */
    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * @param key
     * @return
     * @description 通过key来从内存缓存中获得bitmap对象
     */
    private static Bitmap getBitmapFromMemCache(String key) {
        if (mMemoryCache == null) {
            mMemoryCache = new LruCache<>(getMemoryCacheSize(App.getContext()));
        }
        return mMemoryCache.get(key);
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
        if (imageUrlStr == null || imageUrlStr.length() == 0) {
            return null;
        }
        bitmap = getBitmapFromMemCache(imageUrlStr);
        if (bitmap != null) {
            LogUtil.d("cache hit !");
            return bitmap;
        }
        try {
            LogUtil.e("load url " + imageUrlStr + " currentThreadId " + Thread.currentThread().getId());
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
//                bitmap = decodeByteArray(imgData, 0, imgData.length);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.outWidth = Utils.getScreenWidth(App.getContext());
                options.outHeight = Utils.dip2px(App.getContext(), 100);
                bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length, options);
                addBitmapToMemoryCache(imageUrlStr, bitmap);
                LogUtil.e("bitmap loaded! and added to cache! Thread Id is "+ Thread.currentThread().getId() + "Bitmap size is " + imgData.length);
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }
        return bitmap;
    }
}
