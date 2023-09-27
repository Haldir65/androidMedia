package com.me.harris.playerLibrary.video.player.video

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.SystemClock
import android.util.Log
import android.view.Surface
import com.me.harris.playerLibrary.video.player.MediaCodecPlayerContext
import com.me.harris.playerLibrary.video.player.datasource.LocalFileDataSource
import com.me.harris.playerLibrary.video.player.internal.PlayerState
import com.me.harris.playerLibrary.video.player.misc.CodecExceptions
import java.io.IOException
import java.nio.ByteBuffer
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class VideoDecodeThread(val surface: Surface, val path: String, val context: MediaCodecPlayerContext) :
    Thread("【Video-Decoder-Thread】") {

    private val TAG = "VideoDecodeThread"

    @Volatile
    var stop = false

    /** 用来读取音視频文件 提取器  */
    private var mediaCodec: MediaCodec? = null

    private var presentationTimeMs: Long = 0

    var videoDuration: Long = 0

    var mStartTimeForSync: Long = 0
//    var mSeekPts: Long = -1

    private var isSeeking = false
    private fun isSeeking():Boolean {
        return context.state == PlayerState.SEEKING
    }

    override fun run() {
        val mediaExtractor = MediaExtractor()
        mediaExtractor.setDataSource(LocalFileDataSource(path)) // 设置数据源
        selectTrack(mediaExtractor)
        val codec = requireNotNull(mediaCodec)
        codec.start() // 启动MediaCodec ，等待传入数据
        val inputBuffers: Array<ByteBuffer> = codec.inputBuffers // 用来存放目标文件的数据
        var outputBuffers: Array<ByteBuffer> = codec.outputBuffers // 解码后的数据
        val info = MediaCodec.BufferInfo() // 用于描述解码得到的byte[]数据的相关信息
        var bIsEos = false
        mStartTimeForSync = SystemClock.uptimeMillis()
        val avSynchronizer = requireNotNull(context.avSynchronizer)
        var seekStartTime = -1L
        while (!stop&& !interrupted()) {

            waitIfPaused()

            try {
                if (!bIsEos ) {
                    val inIndex: Int = codec.dequeueInputBuffer(0)
                    if (inIndex >= 0 ) {
                        val buffer = inputBuffers[inIndex]
                        val nSampleSize = mediaExtractor.readSampleData(buffer, 0) // 读取一帧数据至buffer中
                        if (nSampleSize < 0) {
                           loge { "InputBuffer BUFFER_FLAG_END_OF_STREAM" }
                            codec.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                            bIsEos = true
                        } else {
                            codec.queueInputBuffer(inIndex, 0, nSampleSize, mediaExtractor.sampleTime, 0) // 通知MediaDecode解码刚刚传入的数据
                            logd { "${identity()}【Video】queue inputBufer at ${mediaExtractor.sampleTime / 1_000_000} s" }
                            val mSeekPts = avSynchronizer.mVideoSeekPositionMs*1000
                            if (mSeekPts >=0 ) {
                                if ( !isSeeking){
                                    seekStartTime = SystemClock.uptimeMillis()
                                    mediaExtractor.seekTo(mSeekPts, MediaExtractor.SEEK_TO_NEXT_SYNC)
                                    isSeeking = true
                                    logw { "${identity()} 【Video】after an attempt to seek to " + mSeekPts + "  current pos for mediaExtractor.sampleTime = " + mediaExtractor.sampleTime }
                                }else {
                                    logw { "${identity()}【Video】skip seek to ${mSeekPts} since we are done seeking, but output buffer pt is still not satisfied? current mediaExtractor.sampleTime = ${ mediaExtractor.sampleTime} " }

                                }
                            } else {
                                mediaExtractor.advance() // 继续下一取样
                            }
                        }
                    }
                }
                val outIndex: Int = codec.dequeueOutputBuffer(info, 0)
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
                        val mSeekPts = avSynchronizer.mVideoSeekPositionMs * 1000
                        if (mSeekPts >= 0) {
                            if (Math.abs(info.presentationTimeUs /1_000_000- mediaExtractor.sampleTime/1_000_000) <=1L ) {
                                isSeeking = false
                                mStartTimeForSync = SystemClock.uptimeMillis() - info.presentationTimeUs / 1_000
                                presentationTimeMs = info.presentationTimeUs / 1000
                                loge {
                                    """
                                ${identity()}
                                after so many times , finally found valid buffer , buffer pts is ${info.presentationTimeUs / 1_000_000} and we already seek to ${mSeekPts / 1_000_000}, so we will continue
                                next sleep time is ${info.presentationTimeUs / 1000 - (SystemClock.uptimeMillis() - mStartTimeForSync)} ms
                                mediaExtractor.sampleTime  = ${mediaExtractor.sampleTime/1_000_000 }
                                ${if (seekStartTime>-1L)"took me ${SystemClock.uptimeMillis() - seekStartTime} ms to get render target frame" else ""}
                              """.lineSequence().joinToString(transform = String::trimStart, separator = System.lineSeparator())
                                }
                                avSynchronizer.seekVideoCompleted()
                                context.avSynchronizer?._videoPtsMicroSeconds = info.presentationTimeUs
                            } else {
                                logw{
                                    "【Video】 invalid buffer , buffer pts is ${info.presentationTimeUs / 1_000_000} but we already seek to ${mSeekPts / 1_000_000}, so we will discard"
                                }
                            }
                        } else {
                            sleepRender(info)
                            context.avSynchronizer?._videoPtsMicroSeconds = info.presentationTimeUs
                            presentationTimeMs = info.presentationTimeUs / 1000
                        }
                        if (!stop){
                            codec.releaseOutputBuffer(outIndex, true)
                        }
                    }
                }

            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
        codec.stop()
//
//        try {
//        } catch (e: MediaCodec.CodecException) {
//            loge { "${e.stackTraceToString()}" }
//        } catch (e: IllegalStateException) {
//            loge { "${e.stackTraceToString()}" }
//        }
        codec.release()
        mediaExtractor.release()
    }

    private fun sleepRender(info: MediaCodec.BufferInfo) {
        //防止视频播放过快
        var diff: Long = info.presentationTimeUs / 1000 - (SystemClock.uptimeMillis() - mStartTimeForSync)
        while (diff > 0 && !isSeeking) {
            try {
                logd { "${identity()} sleep $diff ms" }
                sleep(33)
                diff = info.presentationTimeUs / 1000 - (SystemClock.uptimeMillis() - mStartTimeForSync)
            } catch (e: InterruptedException) {
                stop = true
                break
            }
        }
    }

    /**
     *
     * @param position 微秒
     */
    fun seek(position: Long) {

    }

    fun identity(): String {
        return "【Video】: ${Thread.currentThread().name}"
    }

    /**
     * ms
     */
    fun currentPosition(): Long {
        return presentationTimeMs
    }

    private inline fun logd(crossinline e: (() -> String)) {
        Log.d("=A=", e())
    }

    private inline fun logi(crossinline e: (() -> String)) {
        Log.i("=A=", e())
    }


    private inline fun logw(crossinline e: (() -> String)) {
        Log.w("=A=", e())
    }

    private inline fun loge(crossinline e: (() -> String)) {
        Log.e("=A=", e())
    }


    private fun selectTrack(mediaExtractor: MediaExtractor){

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
                mediaCodec?.configure(format, surface, null, 0)
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
            lock.withLock {
                logw { "${identity()} now will lock ,waiting for signal " }
                val sleepStartTime = SystemClock.uptimeMillis()
                condition.await() // block this
                val diffTime = SystemClock.uptimeMillis()-sleepStartTime
                mStartTimeForSync+=diffTime
                logw { "${identity()} wake up from  lock , continue processing, we slept for ${diffTime/1_000} seconds " }
            }
        }
    }

    fun wakeUp(){
        require(!lock.isLocked)
        lock.withLock {
            logw { "${identity()} prepare signal ${lock.holdCount} ${lock.hasQueuedThread(this)}  ${lock.hasQueuedThreads()}" }
            condition.signal()
            logw { "${identity()} done signal ${lock.holdCount} ${lock.hasQueuedThread(this)}  ${lock.hasQueuedThreads()}" }
        }
    }

    fun pause() {
        if (lock.isHeldByCurrentThread){
            lock.unlock()
        }
    }
}
