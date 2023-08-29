package com.me.harris.libjpeg

import android.graphics.Bitmap

object JpegSpoon {

    init {
        System.loadLibrary("myjpeg")
    }

    external fun compressbitmap(bitmap: Bitmap,quality:Int,outFilePath:String,optimize:Boolean):Int
    external fun basic(string :String)
}
