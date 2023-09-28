package com.me.harris.playerLibrary.video.player.subordinates

import android.media.MediaExtractor
import android.media.MediaFormat
import android.view.Surface
import java.io.File

class MediaCodecInMutableState(val filepath:String) {
    //    // 2. 不大会变化的东西
    //    // data source
    //    // video duration
    //    // video width
    //    // video height
    //    // video rotation

    var width:Int = 0
    var height:Int = 0
    val videoDurationMicroSeconds:Long
    var surface:Surface? = null
    var surfaceChanged = false // pause -> resume 重新setSurface

    init {
        videoDurationMicroSeconds = getVideoDurationInMicroSeconds(filepath)
    }

    private fun getVideoDurationInMicroSeconds (filepath:String):Long {
        require(File(filepath).exists())
        val extractor = MediaExtractor()
        extractor.setDataSource(filepath)
        for (i in 0 until extractor.trackCount){
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME).orEmpty()
            if (mime.startsWith("video/")){
                val keyFrameRate = runCatching { format.getInteger(MediaFormat.KEY_FRAME_RATE) }.getOrElse { -1 } // may throw
                val duration = format.getLong(MediaFormat.KEY_DURATION) // microseconds
                 width = format.getInteger(MediaFormat.KEY_WIDTH)
                 height = format.getInteger(MediaFormat.KEY_HEIGHT)
                return  duration
            }
        }
        error("unpxpected!")
    }

}
