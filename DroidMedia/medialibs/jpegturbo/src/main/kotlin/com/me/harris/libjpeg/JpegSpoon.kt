package com.me.harris.libjpeg

import android.graphics.Bitmap

object JpegSpoon {

    init {
        System.loadLibrary("myjpeg")
    }

    external fun compressbitmap(bitmap: Bitmap,quality:Int,storage_dir:String,outFilePath:String,optimize:Boolean,turbo:Boolean):Int
    external fun basic(string :String)
}
