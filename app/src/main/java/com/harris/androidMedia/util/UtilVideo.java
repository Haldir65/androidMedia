package com.harris.androidMedia.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;


import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

/**
 * Created by Haldir65 on 2017/3/7.
 */

public class UtilVideo {

   public static class VideoInfo {
        public String name;
        public String info;
        public String path;
    }

    @RequiresPermission(READ_EXTERNAL_STORAGE)
    public static List<VideoInfo> getAllVideoOnDevice(Context context, @NonNull List<VideoInfo> list) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // 图片的名称
                String name = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                // 图片的描述
                String info = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Video.Media.DESCRIPTION));
                // 图片位置的数据
                byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                // 将data转换成String类型的图片路径
                String path = new String(data, 0, data.length - 1);
                VideoInfo video = new VideoInfo();
                video.info = info == null ? "" : info;
                video.name = name == null ? "" : name;
                video.path = path;
                list.add(video);
            }
            cursor.close();
        }
        return list;
    }
}
