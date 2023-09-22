package com.me.harris.playerLibrary.video

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.SystemClock
import android.util.Log
import android.view.Surface
import com.me.harris.playerLibrary.VideoPlayView
import java.io.IOException
import java.nio.ByteBuffer

class VideoDecodeThread2(val surface:Surface, val path:String,val view:VideoPlayView,):Thread("【Decoder-Video】Thread") {

    private val TAG = "VideoDecodeThread"

    @Volatile
    var stop = false

    /** 用来读取音視频文件 提取器  */
    private var mediaCodec: MediaCodec? = null

    private var presentationTimeMs: Long = 0

    var videoDuration: Long = 0

    var mStartTimeForSync: Long = 0
    var mSeekPts: Long = -1

    private var isSeeking = false



    override fun run() {
        val mediaExtractor = MediaExtractor()
        try {
            mediaExtractor.setDataSource(path) // 设置数据源
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        var mimeType: String? = null
        for (i in 0 until mediaExtractor.trackCount) { // 信道总数
            val format = mediaExtractor.getTrackFormat(i) // 音频文件信息
            mimeType = format.getString(MediaFormat.KEY_MIME)
            if (mimeType!!.startsWith("video/")) { // 视频信道
                videoDuration = format.getLong(MediaFormat.KEY_DURATION) / 1000
                Log.w("=A=", "video duration is $videoDuration")
                mediaExtractor.selectTrack(i) // 切换到视频信道
                try {
                    mediaCodec = MediaCodec.createDecoderByType(mimeType!!) // 创建解码器,提供数据输出
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                //用于临时处理 surfaceView还没有create，却调用configure导致崩溃的问题
                while (!view.isCreate) {
                    try {
                        sleep(100)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                mediaCodec?.configure(format, surface, null, 0)
                break
            }
        }
        if (mediaCodec == null) {
            Log.e(TAG, "Can't find video info!")
            return
        }
        val codec = requireNotNull(mediaCodec)

        codec.start() // 启动MediaCodec ，等待传入数据

        // 输入
        // 输入
        val inputBuffers: Array<ByteBuffer> = codec.getInputBuffers() // 用来存放目标文件的数据

        // 输出
        // 输出
        var outputBuffers: Array<ByteBuffer> = codec.getOutputBuffers() // 解码后的数据

        val info = MediaCodec.BufferInfo() // 用于描述解码得到的byte[]数据的相关信息

        var bIsEos = false
        mStartTimeForSync = SystemClock.uptimeMillis()

        // ==========开始解码=============

        // ==========开始解码=============
        while (!stop && !interrupted()) {
            try {
                if (!bIsEos) {
                    val inIndex: Int = codec.dequeueInputBuffer(0)
                    if (inIndex >= 0) {
                        val buffer = inputBuffers[inIndex]
                        val nSampleSize = mediaExtractor.readSampleData(buffer, 0) // 读取一帧数据至buffer中
                        if (nSampleSize < 0) {
                            Log.d(TAG, "InputBuffer BUFFER_FLAG_END_OF_STREAM")
                            codec.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                            bIsEos = true
                        } else {
                            codec.queueInputBuffer(inIndex, 0, nSampleSize, mediaExtractor.sampleTime, 0) // 通知MediaDecode解码刚刚传入的数据
                            if (mSeekPts > 0 && !isSeeking) {
                                mediaExtractor.seekTo(mSeekPts, MediaExtractor.SEEK_TO_NEXT_SYNC)
                                isSeeking = true
                                Log.e("=A=", "${identity()} after seek to " + mSeekPts + "  current pos = " + mediaExtractor.sampleTime)
                            } else {
                                mediaExtractor.advance() // 继续下一取样
                            }
                        }
                    }
                }
                val outIndex: Int = codec.dequeueOutputBuffer(info, 0)
                when (outIndex) {
                    MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> {
                        Log.d(TAG, "INFO_OUTPUT_BUFFERS_CHANGED")
                        outputBuffers = codec.outputBuffers
                    }
                    MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> Log.d(TAG, "New format " + codec.outputFormat)

                    MediaCodec.INFO_TRY_AGAIN_LATER -> Log.d(TAG, "dequeueOutputBuffer timed out!")

                    else -> {
                        if (mSeekPts > 0) {
                            if (info.presentationTimeUs - mSeekPts > 0 ){
                                mSeekPts = -1L
                                isSeeking = false
                                mStartTimeForSync = SystemClock.uptimeMillis() - info.presentationTimeUs/1_000
                                codec.releaseOutputBuffer(outIndex, true)
                                Log.e("=A=", """
                                ${identity()} after so many times , finally found valid buffer , buffer pts is ${info.presentationTimeUs/1_000_000} and we already seek to ${mSeekPts/1_000_000}, so we will continue
                                next sleep time is ${info.presentationTimeUs / 1000 - (SystemClock.uptimeMillis() - mStartTimeForSync)} ms
                            """.trimIndent())
                            } else {
                                Log.e("=A=", "【Video】 invalid buffer , buffer pts is ${info.presentationTimeUs/1_000_000} but we already seek to ${mSeekPts/1_000_000}, so we will discard")
                                codec.releaseOutputBuffer(outIndex, true)
                            }
                        }else {
                            val buffer = outputBuffers[outIndex]
                            Log.v(TAG, "We can't use this buffer but render it due to the API limit, $buffer")
                            sleepRender(info)
                            presentationTimeMs = info.presentationTimeUs / 1000
                            codec.releaseOutputBuffer(outIndex, info.size > 0)
                        }
                    }
                }
                // All decoded frames have been rendered, we can stop playing
                // now
                if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    break
                }
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
        codec.stop()
        codec.release()
        mediaExtractor.release()


    }

    private fun sleepRender(info: MediaCodec.BufferInfo) {
        //防止视频播放过快
        var diff: Long = info.presentationTimeUs / 1000 - (SystemClock.uptimeMillis() - mStartTimeForSync)
        while (diff > 0) {
            try {
                Log.w("=A=", " ${identity()} sleep $diff ms")
                //                sleep(diff);
                sleep(10)
                diff = info.presentationTimeUs / 1000 - (SystemClock.uptimeMillis() - mStartTimeForSync)
                //							sleep(10);
            } catch (e: InterruptedException) {
                e.printStackTrace()
                break
            }
        }
    }

    /**
     *
     * @param position 微秒
     */
    fun seek(position: Long) {
        mSeekPts = position
    }

    fun identity():String {
        return "【Video】 ${Thread.currentThread().name}"
    }

    /**
     * ms
     */
    fun currentPosition():Long{
        return if (isSeeking && mSeekPts >0 ) mSeekPts/1_000
        else presentationTimeMs
    }






}
