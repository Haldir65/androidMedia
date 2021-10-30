package com.me.harris.droidmedia.textureview

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import com.me.harris.droidmedia.video.VideoPlayView

class VideoDecoder(val mSurface: Surface) : TypicalDecoder {

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

    override fun extractor() = extractor


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
                val keyFrameRate = format.getInteger(MediaFormat.KEY_FRAME_RATE) // 1s 30帧左右
                Log.e("VideoDecoder", " keyFrameRate =  ${keyFrameRate} ") // 5s 一个关键帧
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
                val inputBufferId = decoder.dequeueInputBuffer(DEFAULT_TIME_OUT)
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
                        decoder.queueInputBuffer(
                            inputBufferId,
                            0,
                            sampleSize,
                            extractor!!.sampleTime,
                            0
                        )
                        extractor!!.advance()
                    }
                }
            }
            val outIndex = decoder.dequeueOutputBuffer(info, DEFAULT_TIME_OUT)

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



}