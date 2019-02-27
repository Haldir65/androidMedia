package com.harris.androidMedia.util

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.provider.MediaStore
import android.support.annotation.RequiresPermission
import java.io.File

data class VideoInfo(val name:String = "",val info:String = "",  val path:String)

@RequiresPermission(READ_EXTERNAL_STORAGE)
fun getAllVideoOnDevice(context: Context, list: MutableList<VideoInfo>): List<VideoInfo> {
    val cursor = context.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null)
    if (cursor != null) {
        while (cursor.moveToNext()) {
            // 视频的名称
            val name = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME))
            // 视频的描述
            val info = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Video.Media.DESCRIPTION))
            // 视频位置的数据
            val data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
            // 将data转换成String类型的图片路径
            val path = String(data, 0, data.size - 1)
            val video = VideoInfo(info,name,path)
            list.add(video)
        }
        cursor.close()
    }
    return list
}

@RequiresPermission(READ_EXTERNAL_STORAGE)
fun getAllVideoUnderCertainDirNonRecrusive(absPath:String,list:MutableList<VideoInfo>):MutableList<VideoInfo>{
    val folder = File(absPath)
    if (!folder.exists()||!folder.isDirectory){
        return list
    }
    val children = folder.listFiles { _, name ->
        name.endsWith("mp4") || name.endsWith("mkv") || name.endsWith("avi")
    }
    children.forEach {f ->
        list.add(VideoInfo(f.name,"",f.absolutePath))
    }
    return list
}