package com.harris.androidMedia.album;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.harris.androidMedia.util.LogUtil;

/**
 * Created by Harris on 2017/4/22.
 */

public class AlbumAsyncTask extends AsyncTask<String, Void, Bitmap> {

    CallBack mCallback;
    int position;

    public AlbumAsyncTask(CallBack mCallback, int position) {
        this.mCallback = mCallback;
        this.position = position;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        if (isCancelled()) {
            return null;
        }
        return ImageHelper.loadBitmapFromNet(params[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (mCallback != null&&bitmap!=null) {
            LogUtil.d("successfully loaded!  " + position);
            mCallback.onReady(bitmap, position);
        }
    }
}
