package com.me.harris.serviceapi.jpeg

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


interface JpegLibService {

    fun compressbitmap(bitmap: Bitmap, quality:Int, storage_dir:String, outFilePath:String, optimize:Boolean, turbo:Boolean):Int

}
