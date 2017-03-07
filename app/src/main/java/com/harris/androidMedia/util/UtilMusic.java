package com.harris.androidMedia.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

/**
 * Created by majingwei on 2017/3/7.
 */

public class UtilMusic {
    public static class MusicInfo{
        public String name;
        public String info;
        public String path; // TODO: 2017/3/7 add ThumbNail for album
        public String disPlayName;
        public String album;
    }

    @RequiresPermission(READ_EXTERNAL_STORAGE)
    public static List<MusicInfo> getAllAudioOnDevice(Context context, @NonNull List<MusicInfo> list) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // 标题
                String name = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media.TITLE));
                // 专辑名称
                String albumInfo = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media.ALBUM));
//                String album = cursor.getString(cursor.getColumnIndex(android.provider.MediaStore.Audio.Albums.ALBUM_ART));
                String displayName = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));

                // 位置的数据
                byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                // 将data转换成String类型的图片路径
                String path = new String(data, 0, data.length - 1);
                MusicInfo music = new MusicInfo();
                music.info = albumInfo == null ? "" : albumInfo;
                music.name = name == null ? "" : name;
                music.path = path;
                music.disPlayName = displayName;
                list.add(music);
            }
            cursor.close();
        }
        return list;
    }
}
