package com.harris.androidMedia.util;

import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

/**
 * Created by Harris on 2017/2/25.
 */

public class Utils {

    @RequiresPermission(READ_EXTERNAL_STORAGE)
    @Nullable
    public static List<String> getFileAbsolutePathList() {
        List<String> result = null;
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+Environment.DIRECTORY_MOVIES);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                result = new ArrayList<>(files.length);
                for (int i = 0; i < files.length; i++) {
                    result.add(files[i].getAbsolutePath());
                }
            }
        }
        return result;
    }
}
