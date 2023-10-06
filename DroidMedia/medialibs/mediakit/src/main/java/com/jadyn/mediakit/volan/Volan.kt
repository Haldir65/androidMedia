package com.jadyn.mediakit.volan

import android.graphics.Bitmap

object Volan {

    init {
        System.loadLibrary("gljni")
    }

    external fun rotateBitmapCcw90(bitmap: Bitmap):Bitmap
}
