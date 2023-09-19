package com.me.harris.libjpeg

import android.graphics.Bitmap
import java.nio.ByteBuffer

object JpegSpoon {

    const val COMPRESS_MODE_TURBO_JPEG  = 1
    const val COMPRESS_MODE_LIBJPEG_FILE  = 2
    const val COMPRESS_MODE_LIBJPEG_IN_MEMORY =  3

    init {
        System.loadLibrary("myjpeg")



    }


    external fun compressbitmap(bitmap: Bitmap,quality:Int,storage_dir:String,outFilePath:String,optimize:Boolean,mode:Int):Int
    external fun compressbitmapInMemory(bitmap: Bitmap,quality:Int,storage_dir:String,outFilePath:String,optimize:Boolean,turbo:Boolean):Int


    external fun decompressBitmapFromJpegFilePath(jpegPath:String,buffer:ByteBuffer):Int
    external fun decompressBitmapFromJpegFilePathTurbo(jpegPath:String,buffer:ByteBuffer,width:Int,height:Int):Int

    external fun probeJpegInfo(filepath:String):IntArray
    external fun basic(string :String)
}
