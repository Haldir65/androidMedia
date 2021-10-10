package com.me.harris.droidmedia.textureview

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import com.me.harris.droidmedia.video.VideoPlayView

class VideoDecoder(val mSurface: Surface) : TypicalDecoder {


    @Volatile
    var mStop: Boolean = false

    override fun isStopped(): Boolean = mStop


    override fun start(url: String) {
        startExtract(url)
    }

    override fun stop() {
        mStop = true
    }


    private val DEFAULT_TIME_OUT = 10_000L * 2

    fun startExtract(url: String) {
        val str = url
        val extractor = MediaExtractor()
        var decoder: MediaCodec? = null
        extractor.setDataSource(str)
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime?.startsWith("video/") == true) {
                extractor.selectTrack(i)
                decoder = MediaCodec.createDecoderByType(mime)
                decoder.configure(format, mSurface, null, 0)
                break
            }
        }
        if (decoder == null) throw IllegalStateException("unable to initiate codec")
        decoder.start()
        var sawEOS = false
        val info = MediaCodec.BufferInfo()
        val startMs = System.currentTimeMillis()
        while (!isStopped()) {
            if (!sawEOS) {
                val inputBufferId = decoder.dequeueInputBuffer(DEFAULT_TIME_OUT)
                if (inputBufferId >= 0) {
                    val inputBuffer = decoder.getInputBuffer(inputBufferId)
                    val sampleSize = inputBuffer?.let { extractor.readSampleData(it, 0) } ?: -1
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
                            extractor.sampleTime,
                            0
                        )
                        extractor.advance()
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
                    sleepRender(info, startMs)
                }
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
        }catch (e:Exception){

        }

    }


}