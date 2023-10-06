package com.me.harris.awesomelib.utils

import android.media.MediaExtractor
import android.media.MediaFormat
import java.io.File

object AudioUtils {

    const val SAMPLE_RATE_44100 = 44100
    const val SAMPLE_RATE_48000 = 48000


    fun getAudioSampleRate(filepath:String):Int{
        if (!filepath.startsWith("http")) require(File(filepath).exists())
        val extractor = MediaExtractor()
        extractor.setDataSource(filepath)
        val format = extractor.getTrackFormat(0)
        val bitRate = kotlin.runCatching {
            format.getInteger(MediaFormat.KEY_BIT_RATE);
        }.getOrDefault(1)
        val sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        require(sampleRate in arrayOf(SAMPLE_RATE_44100, SAMPLE_RATE_48000))
        extractor.release()
        return sampleRate
    }

}
