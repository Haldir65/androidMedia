package com.me.harris.libjpeg.ui

sealed class CompressEvents{
    data object CompressStart:CompressEvents()

    class CompressCompleted(val path:String):CompressEvents()

    class TurboCompressCompleted(val path:String):CompressEvents()

}
