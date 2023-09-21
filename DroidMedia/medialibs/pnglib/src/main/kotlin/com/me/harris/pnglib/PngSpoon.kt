package com.me.harris.pnglib

import java.nio.ByteBuffer

object PngSpoon {

    init {
        System.loadLibrary("mypng")
    }

    external fun probeFileInfo(pngFilePath:String):Int

    external fun getPngWidth(pngFilepath:String):Int
    external fun getPngHeight(pngFilepath:String):Int

    external fun pngHasAlpha(pngFilepath:String):Boolean

    external fun decodePngToDirectBuffer(pngFilePath:String,buffer:ByteBuffer):Int

    external fun compressBitmapToPngFile(destfile:String,buffer: ByteBuffer,width:Int,height:Int):Int
}
