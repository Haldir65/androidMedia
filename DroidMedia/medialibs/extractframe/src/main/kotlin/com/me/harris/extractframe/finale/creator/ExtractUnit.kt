package com.me.harris.extractframe.finale.creator

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import com.me.harris.extractframe.contract.ExtractConfiguration
import com.me.harris.extractframe.parallel.Range
import kotlinx.coroutines.*

const val TAG = ExtractConfiguration.LOG_TAG

internal class ExtractUnit(val config: ExtractConfig,val range: Range) {

    private val DEFAULT_TIMEOUT_US = 100000L


    suspend fun doingExtract(){
        val filePath:String = config.filepath
        val durationMicroSeconds = config.videoDurationMicroSecond
        val gapMicroSeconds = config.gapInBetweenSeconds*1000_000
        kotlin.runCatching {
            val extractor  = MediaExtractor()
            extractor.setDataSource(filePath)
            val index = selectVideoTrack(extractor = extractor)
            if (index < 0) {
                error("failed to selectVideoTrack for material ${filePath}")
            }
            val format = extractor.getTrackFormat(index)
            val mime = format.getString(MediaFormat.KEY_MIME)!!
            val decoder = requireNotNull(MediaCodec.createDecoderByType(mime))
            val width = format.getInteger(MediaFormat.KEY_WIDTH)
            val height = format.getInteger(MediaFormat.KEY_HEIGHT)
            decoder.configure(format,null,null,0)
            decoder.start()
            val info = MediaCodec.BufferInfo()
            var sawInputEOS = false
            var sawOutputEOS = false
            val outputFrameCount = 0
            var rangeIndex = 0
            var startTime = range.points[rangeIndex]
            extractor.seekTo(startTime,MediaExtractor.SEEK_TO_CLOSEST_SYNC)
            while (currentCoroutineContext().isActive && !sawOutputEOS){
                if (!sawInputEOS){
                    val inputBufferId = decoder.dequeueInputBuffer(DEFAULT_TIMEOUT_US)
                    if (inputBufferId>=0){
                        val inputBuffer = requireNotNull(decoder.getInputBuffer(inputBufferId))
                        val sampleSize = extractor.readSampleData(inputBuffer,0)
                        if (sampleSize < 0 ){
                            decoder.queueInputBuffer(inputBufferId,0,0,0L,MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                            sawInputEOS = true
                            Log.i(TAG,"sawInputEos set to true")
                        }else
                        {
                            val time =  extractor.sampleTime
                            if (Math.abs(time-durationMicroSeconds) < 5_000_00){
                                decoder.queueInputBuffer(inputBufferId,0,0,0L,MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                                sawInputEOS = true
                                Log.i(TAG,"current sampleTime is ${time} manually sawInputEos set to true")
                            }else {
                                decoder.queueInputBuffer(inputBufferId,0,sampleSize,time,0)

                            }
                        }
                    }
                }
            }
        }
    }

    private fun selectVideoTrack(extractor: MediaExtractor): Int {
        val numTracks = extractor.trackCount
        for (i in 0 until numTracks) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime!!.startsWith("video/")) {
                extractor.selectTrack(i)
                return i
            }
        }
        return -1
    }
}
