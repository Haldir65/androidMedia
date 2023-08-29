package com.me.harris.libjpeg.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.me.harris.libjpeg.JpegSpoon
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

internal class LibJpegEntryViewModel :ViewModel(){


    val events = MutableSharedFlow<CompressEvents>()

    private val spoon = JpegSpoon

    fun callJpegEntryMethod(){
        Log.w("=A=","call Jpeg Entry method1")
        spoon.basic("""
            we home
            phone call
            bee
        """.trimIndent())
    }

    @WorkerThread
    fun callJpegCompressBitmap(bitmap:Bitmap,quality:Int,outFilePath:String,optimize:Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val width = bitmap.width
            val height = bitmap.height
            spoon.compressbitmap(bitmap, quality, outFilePath, optimize)
            Log.w("=A=","compress bitmap w = ${width} h = ${height} to $outFilePath cost me ${System.currentTimeMillis() - now} milliseconds ")
            events.emit(CompressEvents.CompressCompleted(path = outFilePath))
        }
    }
}
