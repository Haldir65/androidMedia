package com.me.harris.playerLibrary.video

import android.media.AudioFormat
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.SystemClock
import android.util.Log
import java.io.IOException

class SoundDecodeThread2(val path:String): Thread("【Decoder-Audio】Thread") {

    private val TAG = "SoundDecodeThread"

    private var mediaCodec: MediaCodec? = null

    @Volatile
    var stop = false

    private var mPlayer: AudioPlayer? = null

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

        var mimeType: String
        for (i in 0 until mediaExtractor.trackCount) { // 信道总数
            val format = mediaExtractor.getTrackFormat(i) // 音频文件信息
            mimeType = format.getString(MediaFormat.KEY_MIME).orEmpty()
            if (mimeType.startsWith("audio/")) { // 音频信道
                mediaExtractor.selectTrack(i) // 切换到 音频信道
                try {
                    mediaCodec = MediaCodec.createDecoderByType(mimeType) // 创建解码器,提供数据输出
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                mediaCodec!!.configure(format, null, null, 0)
                mPlayer = AudioPlayer(
                    format.getInteger(MediaFormat.KEY_SAMPLE_RATE),
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                mPlayer!!.init()
                break
            }
        }
        if (mediaCodec == null) {
            Log.e(TAG, "Can't find video info!")
            return
        }
        val codec = requireNotNull(mediaCodec)

        codec.start() // 启动MediaCodec ，等待传入数据

        val inputBuffers = codec.getInputBuffers() // 用来存放目标文件的数据

        var outputBuffers = codec.getOutputBuffers() // 解码后的数据

        val info = MediaCodec.BufferInfo() // 用于描述解码得到的byte[]数据的相关信息

        var bIsEos = false
        mStartTimeForSync = SystemClock.uptimeMillis()


        while (!stop && !interrupted()) {
            if (!bIsEos) {
                val inIndex = codec.dequeueInputBuffer(0)
                if (inIndex >= 0) {
                    val buffer = inputBuffers[inIndex]
                    val nSampleSize = mediaExtractor.readSampleData(buffer, 0) // 读取一帧数据至buffer中
                    if (nSampleSize < 0) {
                        Log.d(TAG, "InputBuffer BUFFER_FLAG_END_OF_STREAM")
                        codec.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                        bIsEos = true
                    } else {
                        // 填数据
                        Log.e("=A=", "【Audio】queue inputBufer at " + mediaExtractor.sampleTime/1_000_000)
                        codec.queueInputBuffer(inIndex, 0, nSampleSize, mediaExtractor.sampleTime, 0) // 通知MediaDecode解码刚刚传入的数据
                        if (mSeekPts > 0 && !isSeeking) {
                            mediaExtractor.seekTo(mSeekPts, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
                            isSeeking = true
                            Log.e("=A=", "【Audio】after seek to " + mSeekPts + "  current pos = " + mediaExtractor.sampleTime)
                        } else {
                            mediaExtractor.advance() // 继续下一取样
                        }
                    }
                }else {
                    Log.e("=A=", "【Audio】dequeueInputBuffer return -1!!!!" + mSeekPts + "  current pos = " + mediaExtractor.sampleTime)
                }
            }
            val outIndex = codec.dequeueOutputBuffer(info, 0)
            when (outIndex) {
                MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> {
                    Log.d(TAG, "INFO_OUTPUT_BUFFERS_CHANGED")
                    outputBuffers = codec.getOutputBuffers()
                }

                MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> Log.d(
                    TAG,
                    "New format " + codec.getOutputFormat()
                )

                MediaCodec.INFO_TRY_AGAIN_LATER -> Log.d(TAG, "dequeueOutputBuffer timed out!")

                else -> {
                    val buffer = outputBuffers[outIndex]
                    Log.v(TAG, "We can't use this buffer but render it due to the API limit, $buffer")
                    if (mSeekPts>0) { // 说明是刚刚seek过出一次
                         // info.presentationTimeUs / 1000 - (SystemClock.uptimeMillis() - mStartTimeForSync)
//                        if (Math.abs(info.presentationTimeUs/1_000_000 - mSeekPts/1_000_000)<10){
                        if (info.presentationTimeUs - mSeekPts > 0  || Math.abs(info.presentationTimeUs-mSeekPts) < 1_000_00){
                            mSeekPts = -1L
                            isSeeking = false
                            mStartTimeForSync = SystemClock.uptimeMillis() - info.presentationTimeUs/1_000
                            codec.releaseOutputBuffer(outIndex, false)
                            Log.e("=A=", """
                                【Audio】 after so many times , finally found valid buffer , buffer pts is ${info.presentationTimeUs/1_000_000} and we already seek to ${mSeekPts/1_000_000}, so we will continue
                                next sleep time is ${info.presentationTimeUs / 1000 - (SystemClock.uptimeMillis() - mStartTimeForSync)} ms
                            """.trimIndent())
                        }else {
                            Log.e("=A=", "【Audio】 invalid buffer , buffer pts is ${info.presentationTimeUs/1_000_000} but we already seek to ${mSeekPts/1_000_000}, so we will discard")
                            codec.releaseOutputBuffer(outIndex, false)
                        }
                    } else {
                        val diff = info.presentationTimeUs / 1000 - (SystemClock.uptimeMillis() - mStartTimeForSync)
                        Log.w("=A=", "【Audio】 normal buffer , buffer pts is ${info.presentationTimeUs/1_000_000} , we will sleep for $diff ")
                        sleepRender(info)
                        //用来保存解码后的数据
                        val outData = ByteArray(info.size)
                        buffer[outData]
                        //清空缓存
                        buffer.clear()
                        //播放解码后的数据
                        mPlayer!!.play(outData, 0, info.size)
                        codec.releaseOutputBuffer(outIndex, false)
                    }
                }
            }

            // All decoded frames have been rendered, we can stop playing
            // now
            if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                break
            }
        }

        codec.stop()
        codec.release()
        mediaExtractor.release()
    }

    //modify
    //	pri
    //
    private fun sleepRender(info: MediaCodec.BufferInfo) {
        var diff = info.presentationTimeUs / 1000 - (SystemClock.uptimeMillis() - mStartTimeForSync)
        while (diff > 0) {
            diff = try {
                Log.w("=A=", "【Audio】sleep  $diff ms")
                sleep(10)
                info.presentationTimeUs / 1000 - (SystemClock.uptimeMillis() - mStartTimeForSync)
                //							sleep(10);
            } catch (e: InterruptedException) {
                e.printStackTrace()
                break
            }
        }
    }

    /**
     * @param position 微秒
     */
    fun seek(position: Long) {
        mSeekPts = position
    }

}
