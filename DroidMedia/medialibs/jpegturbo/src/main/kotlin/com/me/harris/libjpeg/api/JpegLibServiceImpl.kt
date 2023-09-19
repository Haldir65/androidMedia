package com.me.harris.libjpeg.api

import android.graphics.Bitmap
import com.me.harris.libjpeg.JpegSpoon
import com.me.harris.serviceapi.jpeg.JpegLibService

class JpegLibServiceImpl: JpegLibService {
    override fun compressbitmap(
        bitmap: Bitmap,
        quality: Int,
        storage_dir: String,
        outFilePath: String,
        optimize: Boolean,
        turbo: Boolean
    ): Int {
        return JpegSpoon.compressbitmap(bitmap, quality, storage_dir, outFilePath, optimize, mode = JpegSpoon.COMPRESS_MODE_TURBO_JPEG)
    }
}
