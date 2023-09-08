package com.me.harris.extractframe.finale.creator

import android.graphics.Bitmap
import androidx.core.os.bundleOf
import com.me.harris.awesomelib.ServiceHelper
import com.me.harris.serviceapi.KEY_VIDEO_URL
import com.me.harris.serviceapi.jpeg.JpegLibService

internal fun saveJpeg(bitmap: Bitmap, quality:Int, storage_dir:String, outFilePath:String, optimize:Boolean, turbo:Boolean){

    ServiceHelper.getService(JpegLibService::class.java)?.compressbitmap(bitmap, quality, storage_dir, outFilePath, optimize, turbo)


}