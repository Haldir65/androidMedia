package com.harris.androidMedia.util;

import android.content.Context;
import android.widget.Toast;

import com.harris.androidMedia.App;

/**
 * Created by Harris on 2016/4/8.
 */
public class ToastUtil {

    private static Toast toast;

    public static void showTextLong(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(App.getContext(), text, Toast.LENGTH_LONG);
        } else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_LONG);
        }
        toast.show();
    }

    public static void showTextShort(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(App.getContext(), text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
