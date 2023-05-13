package com.me.harris.playerLibrary.textureview

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import java.lang.NullPointerException

class VideoDecoder(val mSurface: Surface) : TypicalDecoder,Seekable {

    private var extractor:MediaExtractor? = null


    @Volatile
    var mStop: Boolean = false


    override fun isStopped(): Boolean = mStop


    override fun start(url: String) {
        startExtract(url)
    }

    override fun stop() {
        mStop = true
    }

    var timBase:Long = 0
    var duration:Long = 0

    override fun extractor() = extractor

    override val closeFunction = ::stop

    override fun close() {

    }


    private val DEFAULT_TIME_OUT = 10_000L * 2

    fun startExtract(url: String) {
        val str = url
         extractor = MediaExtractor()
        var decoder: MediaCodec? = null
        extractor?.setDataSource(str)
        for (i in 0 until extractor!!.trackCount) {
            val format = extractor!!.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime?.startsWith("video/") == true) {
                try {
                    val keyFrameRate = format.getInteger(MediaFormat.KEY_FRAME_RATE) // 1s 30帧左右
                    duration = format.getLong(MediaFormat.KEY_DURATION)
                    Log.e("VideoDecoder", " keyFrameRate =  ${keyFrameRate} ") // 5s 一个关键帧
                }catch (e:NullPointerException){
                    e.printStackTrace()
                }
                extractor?.selectTrack(i)
                decoder = MediaCodec.createDecoderByType(mime)
                decoder.configure(format, mSurface, null, 0)
                break
            }
        }
        if (decoder == null) throw IllegalStateException("unable to initiate codec")
        decoder.start()
        var sawEOS = false
        var keyFrameCount = 0
        val info = MediaCodec.BufferInfo()
        val startMs = System.currentTimeMillis()
        while (!isStopped()) {
            if (!sawEOS) {
                val inputBufferId = try {
                    decoder.dequeueInputBuffer(DEFAULT_TIME_OUT)
                }catch (e:IllegalStateException){
                    -1
                }
                if (inputBufferId >= 0) {
                    val inputBuffer = decoder.getInputBuffer(inputBufferId)
                    val sampleSize = inputBuffer?.let { extractor!!.readSampleData(it, 0) } ?: -1
                    if (sampleSize < 0) {
                        decoder.queueInputBuffer(
                            inputBufferId,
                            0,
                            0,
                            0,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM
                        )
                        sawEOS = true
                    } else {
                        val nowTime = extractor!!.sampleTime
                        decoder.queueInputBuffer(inputBufferId, 0, sampleSize, nowTime, 0)
                        synchronized(extractor!!){
//                            timBase = nowTime
                            extractor!!.advance()
                        }
                    }
                }
            }
            val outIndex = try {
                decoder.dequeueOutputBuffer(info, DEFAULT_TIME_OUT)
            }catch (e:IllegalStateException){
                Int.MIN_VALUE
            }

            when (outIndex) {
                MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                    Log.d("VideoDecoder", "INFO_OUTPUT_FORMAT_CHANGED")
                }
                MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> {
                    Log.d("VideoDecoder", "INFO_OUTPUT_BUFFERS_CHANGED")
                }
                MediaCodec.INFO_TRY_AGAIN_LATER -> {
                    Log.v("VideoDecoder", "dequeueOutputBuffer timed out!")
                }
                Int.MIN_VALUE -> {
                    stop()
                    break
                }
                else -> {
                    decoder.releaseOutputBuffer(outIndex, true)
                    sleepRender(info, startMs-timBase)
                }
            }
            if ((info.flags and MediaCodec.BUFFER_FLAG_KEY_FRAME) == MediaCodec.BUFFER_FLAG_KEY_FRAME ){
                Log.v("VideoDecoder", "${++keyFrameCount} key frame 关键帧 found ")
            }
            if ((info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                sawEOS = true
                stop()
                break
            }
        }
        try {
            decoder?.stop()
            decoder?.release()
            extractor?.release()
            extractor = null
        }catch (e:Exception){

        }

    }
    override fun decoderName(): String {
        return "VideoDecoder"
    }

    override fun seekTo(absTimeMillisecond: Long) {
    }

    override fun forward(forwardMillisecond: Long) {
        synchronized(extractor!!){
            timBase+= forwardMillisecond
            extractor?.seekTo(minOf(timBase,duration.toLong()),MediaExtractor.SEEK_TO_NEXT_SYNC)
        }
    }

    override fun backWard(backwardMillisecond: Long) {
        synchronized(extractor!!){
            timBase-= backwardMillisecond
            extractor?.seekTo(maxOf(0L,timBase),MediaExtractor.SEEK_TO_NEXT_SYNC)
        }
    }


}