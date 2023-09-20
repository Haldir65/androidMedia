package com.me.harris.pnglib

object PngSpoon {

    init {
        System.loadLibrary("mypng")
    }

    external fun probeFileInfo(pngFilePath:String):Int
}
