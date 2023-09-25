package com.me.harris.playerLibrary.video.player.decode

import android.media.AudioFormat
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.SystemClock
import android.util.Log
import com.me.harris.playerLibrary.video.player.MediaCodecPlayerContext
import com.me.harris.playerLibrary.video.player.audio.AudioPlayer
import com.me.harris.playerLibrary.video.player.internal.PlayerState
import com.me.harris.playerLibrary.video.player.misc.CodecExceptions
import java.io.IOException
import java.util.concurrent.locks.ReentrantLock
import kotlin.jvm.internal.Ref.ObjectRef

class AudioDecodeThread(val path:String, val context: MediaCodecPlayerContext): Thread("【Decoder-Audio】Thread") {

    private val TAG = "AudioDecodeThread"

    private var mediaCodec: MediaCodec? = null

    @Volatile
    var stop = false

    private var mPlayer: AudioPlayer? = null

    @Volatile
    var mute:Boolean = false

    var mStartTimeForSync: Long = 0

    var mSeekPts: Long = -1

    private var isSeeking = false


    override fun run() {
        val mediaExtractor = MediaExtractor()
        mediaExtractor.setDataSource(path) // 设置数据源
       selectTrack(mediaExtractor)
        val codec = requireNotNull(mediaCodec)
        codec.start() // 启动MediaCodec ，等待传入数据
        val inputBuffers = codec.inputBuffers // 用来存放目标文件的数据  todo catch crash ?
        var outputBuffers = codec.outputBuffers // 解码后的数据
        val info = MediaCodec.BufferInfo() // 用于描述解码得到的byte[]数据的相关信息
        var bIsEos = false
        mStartTimeForSync = SystemClock.uptimeMillis()
        while (!stop&&!interrupted()) {

            waitIfPaused() // pause

            val inIndex = codec.dequeueInputBuffer(0)
            if (!bIsEos ) {
                if (inIndex >= 0) {
                    val buffer = inputBuffers[inIndex]
                    val nSampleSize = mediaExtractor.readSampleData(buffer, 0) // 读取一帧数据至buffer中
                    if (nSampleSize < 0) {
                        loge { "【Audio】 InputBuffer BUFFER_FLAG_END_OF_STREAM"  }
                        codec.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                        bIsEos = true
                    } else {
                        // 填数据
                        loge { "【Audio】queue inputBufer at ${mediaExtractor.sampleTime/1_000_000} s"  }
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
//                    bIsEos = true
                    Log.e("=A=", "【Audio】dequeueInputBuffer return -1!!!!" + mSeekPts + "  current pos = " + mediaExtractor.sampleTime)
                }
            }
            val outIndex = codec.dequeueOutputBuffer(info, 0)

            // All decoded frames have been rendered, we can stop playing
            // now
            if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                break
            }

            when (outIndex) {
                MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> {
                    Log.d(TAG, "INFO_OUTPUT_BUFFERS_CHANGED")
                    outputBuffers = codec.outputBuffers
                }

                MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> Log.d(TAG, "New format " + codec.outputFormat)

                MediaCodec.INFO_TRY_AGAIN_LATER -> Log.d(TAG, "dequeueOutputBuffer timed out!")

                else -> {
                    val buffer = outputBuffers[outIndex]
//                    Log.v(TAG, "We can't use this buffer but render it due to the API limit, $buffer")
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
                        context.avSynchronizer?._audioPtsMicroSeconds = info.presentationTimeUs
                        //用来保存解码后的数据
                        val outData = ByteArray(info.size)
                        buffer[outData]
                        //清空缓存
                        buffer.clear()
                        //播放解码后的数据
                        if (!mute && !stop){
                            mPlayer!!.play(outData, 0, info.size)
                        }
                        codec.releaseOutputBuffer(outIndex, false)
                    }
                }
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
                logw { "【Audio】sleep  $diff ms" }
                sleep(33)
                info.presentationTimeUs / 1000 - (SystemClock.uptimeMillis() - mStartTimeForSync)
            } catch (e: InterruptedException) {
                stop = true
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

    private inline fun logw( crossinline e:  (() -> String)) {
        Log.w("=A=", e())
    }

    private inline fun loge( crossinline e:  (() -> String)) {
        Log.e("=A=", e())
    }


    private fun selectTrack(mediaExtractor: MediaExtractor) {
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
            throw CodecExceptions.PrepareExtractorException()
        }
    }


    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    private fun waitIfPaused(){
        if (context.state == PlayerState.PAUSED){
            lock.lock()
        }
    }


}
