package com.me.harris.playerLibrary.video.player.misc

import android.media.MediaExtractor
import android.media.MediaFormat
import java.io.File

internal fun getVideoDurationInMicroSeconds (filepath:String):Long {
    require(File(filepath).exists())
    val extractor = MediaExtractor()
    extractor.setDataSource(filepath)
    for (i in 0 until extractor.trackCount){
        val format = extractor.getTrackFormat(i)
        val mime = format.getString(MediaFormat.KEY_MIME).orEmpty()
        if (mime.startsWith("video/")){
            val keyFrameRate = runCatching { format.getInteger(MediaFormat.KEY_FRAME_RATE) }.getOrElse { -1 } // may throw
            val duration = format.getLong(MediaFormat.KEY_DURATION) // microseconds
            val width = format.getInteger(MediaFormat.KEY_WIDTH)
            val height = format.getInteger(MediaFormat.KEY_HEIGHT)
            return duration
        }
    }
    error("unpxpected!")
}
