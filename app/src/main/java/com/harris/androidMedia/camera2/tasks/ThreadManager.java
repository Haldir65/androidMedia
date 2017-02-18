package com.harris.androidMedia.camera2.tasks;

import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by Harris on 2016/4/9.
 * 后台一直运行着handlerThread的管理类，负责控制调用该线程
 */
public class ThreadManager implements ImageReader.OnImageAvailableListener {

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private HandlerThread mBackgroundThread;

    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler mBackgroundHandler;



    /**
     * Starts a background thread and its {@link Handler}.
     */
    public void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    public void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onImageAvailable(ImageReader reader) {
        mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/Happy/camera2"));

    }

    public HandlerThread getmBackgroundThread() {
        return mBackgroundThread;
    }

    public Handler getmBackgroundHandler() {
        return mBackgroundHandler;
    }
}
