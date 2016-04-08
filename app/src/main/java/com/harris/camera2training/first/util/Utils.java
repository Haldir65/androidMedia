package com.harris.camera2training.first.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Harris on 2016/4/8.
 */
public class Utils {
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        return w_screen;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int h_screen = dm.heightPixels;
        return h_screen;
    }
}
