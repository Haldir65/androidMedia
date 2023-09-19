package com.me.harris.libjpeg.ui

import android.graphics.Bitmap

sealed class CompressEvents{
    data object CompressStart:CompressEvents()

    class CompressCompleted(val path:String):CompressEvents()

    class TurboCompressCompleted(val path:String):CompressEvents()

    class LibJpegCompressInMemoryCompleted(val path:String):CompressEvents()

}


sealed class DecompressEvents {
   data class JpegDecompressFinished(val bitmap:Bitmap,val path:String):DecompressEvents()
   data class JpegDecompressFinishedTurbo(val bitmap:Bitmap,val path:String):DecompressEvents()
}
