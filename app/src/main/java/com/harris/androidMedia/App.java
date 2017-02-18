package com.harris.androidMedia;

import android.app.Application;
import android.content.Context;

/**
 * Created by Harris on 2016/4/8.
 */
public class App extends Application {
    public static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}
