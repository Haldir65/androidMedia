package com.harris.androidMedia.album;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.bumptech.glide.disklrucache.DiskLruCache;
import com.harris.androidMedia.App;
import com.harris.androidMedia.util.Constants;
import com.harris.androidMedia.util.LogUtil;
import com.harris.androidMedia.util.Utils;

import java.io.File;
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

    public static volatile LruCache<String, Bitmap> mMemoryResultCache; //scaledDown cache , first Layer of Cache
    public static volatile LruCache<String, Bitmap> mMemoryCache; //original cache, second Layer of Cache
    public static volatile DiskLruCache mDiskLruCache; //DiskLruCache, third layer of cache

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

    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    public static void addBitmapToResultCache(String key, @NonNull Bitmap scaledBitmap) {
        if (getBitmapFromResultCache(key) == null) {
            mMemoryResultCache.put(key, scaledBitmap);
        }
    }

    public static int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
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

    @Nullable
    private static Bitmap getUnScaledBitmapFromDiskCache(String key) {
        if (mDiskLruCache == null) {
            try {
                mDiskLruCache = DiskLruCache.open(getDiskCacheDir(App.getContext(),"bitmap"),getAppVersion(App.getContext()),1, 10 * 1024 * 1024);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } // TODO: 2017/5/4 grab a copy of bitmap soon
        return null;
    }

    /**
     * 获取scaled Down version的Bitmap
     *
     * @param key
     * @return
     */
    @Nullable
    private static Bitmap getBitmapFromResultCache(String key) {
        if (mMemoryResultCache == null) {
            mMemoryResultCache = new LruCache<>(getMemoryCacheSize(App.getContext()));
        }
        return mMemoryResultCache.get(key);
    }

    /**
     * 获得网络图片Bitmap,理应在这里处理缓存
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
                options.outWidth = Utils.getScreenWidth(App.getContext())/4;
                options.outHeight = Utils.dip2px(App.getContext(), 200);
                bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length, options);
                addBitmapToMemoryCache(imageUrlStr, bitmap);
                LogUtil.e("bitmap loaded! and added to cache! Thread Id is " + Thread.currentThread().getId() + "Bitmap size is " + imgData.length);
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }
        return bitmap;
    }

    @WorkerThread
    public static Bitmap getResultBitmapWithExactSize(String url, int width, int height) {
        Bitmap bitmap = getBitmapFromResultCache(url);
        if (bitmap != null) {
            return bitmap; //return immediately
        } else {
            bitmap = getBitmapFromMemCache(url);
            if (bitmap != null) {
                bitmap = resizeBitmapAndAddToCache(url, bitmap, width, height);
                return bitmap;
            } else {
                bitmap = loadBitmapFromNet(url);
                return resizeBitmapAndAddToCache(url, bitmap, width, height);
            }
        }
    }

    @WorkerThread
    public static Bitmap resizeBitmapAndAddToCache(String url, @NonNull Bitmap bitmap, int width, int height) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        addBitmapToResultCache(url, newBitmap);
        return newBitmap;
    }
}
