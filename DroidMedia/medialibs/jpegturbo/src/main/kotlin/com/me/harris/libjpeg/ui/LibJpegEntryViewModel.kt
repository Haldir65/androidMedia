package com.me.harris.libjpeg.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.me.harris.libjpeg.JpegSpoon
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

internal class LibJpegEntryViewModel :ViewModel(){


    val compressEvents = MutableSharedFlow<CompressEvents>()

    val decompressEvents = MutableSharedFlow<DecompressEvents>()

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
    fun callJpegCompressBitmap(bitmap:Bitmap,quality:Int,storage_dir:String,outFilePath:String,optimize:Boolean,mode:Int){
        viewModelScope.launch(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val width = bitmap.width
            val height = bitmap.height
            spoon.compressbitmap(bitmap, quality, storage_dir,outFilePath, optimize,mode)

            Log.w("=A=","compress bitmap w = ${width} h = ${height} to $outFilePath cost me ${System.currentTimeMillis() - now} milliseconds ")

            val event = when(mode) {
                JpegSpoon.COMPRESS_MODE_TURBO_JPEG -> CompressEvents.TurboCompressCompleted(path = outFilePath)
                JpegSpoon.COMPRESS_MODE_LIBJPEG_FILE -> CompressEvents.CompressCompleted(path = outFilePath)
                else -> CompressEvents.LibJpegCompressInMemoryCompleted(path = outFilePath)
            }
            compressEvents.emit(event)
        }
    }

    @WorkerThread
    fun decomressJpegToBitmap(jpegFilePath:String){
        require(File(jpegFilePath).exists())
        viewModelScope.launch {
            val now = System.currentTimeMillis()

            val (width, height) = spoon.probeJpegInfo(jpegFilePath);
            require(width>0 && height >0 ) { " width or height must > 0" }
            Log.w("=A=","jpeg file ${jpegFilePath} has width = ${width} height = ${height}")
//            val width = 1760
//            val height = 990
            val outBuffer = ByteBuffer.allocateDirect(width*height*4).apply {
                order(ByteOrder.nativeOrder())
            }
            outBuffer.flip()
            spoon.decompressBitmapFromJpegFilePath(jpegFilePath, outBuffer) // return image width + image height
            outBuffer.limit(width*height*4)

            val bmp = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888)
            bmp.copyPixelsFromBuffer(outBuffer) // 1024 * 576 : 1760 990  1920 1080
            val count = bmp.allocationByteCount
            Log.w("=A=","count is ${count}")
            Log.w("=A=","decompress jpeg to bitmap cost me ${System.currentTimeMillis()-now} microseconds")

            val start2 = System.currentTimeMillis()
            val bmp2 = BitmapFactory.decodeFile(jpegFilePath)
            Log.w("=A=","decompress jpeg to bitmap using bitmapFactory  cost me ${System.currentTimeMillis()-start2} microseconds")

            decompressEvents.emit(DecompressEvents.JpegDecompressFinished(bmp,jpegFilePath))
        }
    }


    @WorkerThread
    fun decomressJpegToBitmapTurbo(jpegFilePath:String){
        require(File(jpegFilePath).exists())
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val (width, height) = spoon.probeJpegInfo(jpegFilePath);
            require(width>0 && height >0 ) { " width or height must > 0" }
            Log.w("=A=","jpeg file ${jpegFilePath} has width = ${width} height = ${height} cost is ${System.currentTimeMillis()-now}")
            val outBuffer = ByteBuffer.allocateDirect(width*height*4).apply {
                order(ByteOrder.nativeOrder())
            }
            Log.w("=A=","turbo allocateDirect cost me ${System.currentTimeMillis()-now}")
            outBuffer.flip()
            val t = System.currentTimeMillis()
            spoon.decompressBitmapFromJpegFilePathTurbo(jpegFilePath, outBuffer,width, height) // return image width + image height
            Log.w("=A=","turbo decompressBitmapFromJpegFilePathTurbo cost me ${System.currentTimeMillis()-t}")
            outBuffer.limit(width*height*4)


            val start = System.currentTimeMillis()
            val bmp = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888)
            bmp.copyPixelsFromBuffer(outBuffer)
            Log.w("=A=","turbo copy from buffer cost me ${System.currentTimeMillis()-start}")
//            val count = bmp.allocationByteCount
//            Log.w("=A=","count is ${count}")
            Log.w("=A=","decompress jpeg to bitmap turbo cost me ${System.currentTimeMillis()-now} microseconds")

            val start2 = System.currentTimeMillis()
            val bmp2 = BitmapFactory.decodeFile(jpegFilePath)
            Log.w("=A=","decompress jpeg to bitmap using bitmapFactory  cost me ${System.currentTimeMillis()-start2} microseconds")

            decompressEvents.emit(DecompressEvents.JpegDecompressFinishedTurbo(bmp,jpegFilePath))
        }
    }
}
