package com.me.harris.awesomelib.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

class Permission {
     static final String[] EXTERNALSTORAGE_PERMISSIONS = new String[]{
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static void checkMaybeRequestExternalStorage(
            @NonNull Activity activity, int requestCode
    ){
        if (ActivityCompat.
                checkSelfPermission(activity,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, EXTERNALSTORAGE_PERMISSIONS, requestCode);
        }
    }
}
