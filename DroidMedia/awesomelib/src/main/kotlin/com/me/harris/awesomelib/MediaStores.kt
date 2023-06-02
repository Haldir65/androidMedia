package com.me.harris.awesomelib

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import java.io.File

fun exportMp4ToGallery(context: Context, filePath:String){
    val values = ContentValues(2)
    values.put(MediaStore.Video.Media.MIME_TYPE,"video/mp4")
    values.put(MediaStore.Video.Media.DATA,filePath)
    context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,values)
    context.sendBroadcast(
        Intent(
            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
            Uri.parse("file://"+filePath))
    )
}

 fun exportPngToGallery(context: Context, filePath: String) {
    val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    val f = File(filePath)
    val contentUri = Uri.fromFile(f)
    mediaScanIntent.data = contentUri
    context.sendBroadcast(mediaScanIntent)
}