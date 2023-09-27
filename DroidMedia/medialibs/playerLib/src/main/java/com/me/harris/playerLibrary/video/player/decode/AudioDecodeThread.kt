package com.me.harris.playerLibrary.video.player.decode

import android.media.AudioFormat
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.SystemClock
import android.util.Log
import com.me.harris.playerLibrary.video.player.MediaCodecPlayerContext
import com.me.harris.playerLibrary.video.player.audio.AudioPlayer
import com.me.harris.playerLibrary.video.player.datasource.LocalFileDataSource
import com.me.harris.playerLibrary.video.player.internal.PlayerState
import com.me.harris.playerLibrary.video.player.misc.CodecExceptions
import java.io.IOException
import java.nio.ByteBuffer
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class AudioDecodeThread(val path: String, val context: MediaCodecPlayerContext) : Thread("【Audio-Decoder-Thread】") {

    private val TAG = "AudioDecodeThread"

    private var mediaCodec: MediaCodec? = null

    @Volatile
    var stop = false

    private var mPlayer: AudioPlayer? = null

    @Volatile
    var mute: Boolean = false

    var mStartTimeForSync: Long = 0

    private var isSeeking = false

    private fun isSeeking(): Boolean = context.state == PlayerState.SEEKING

    override fun run() {
        val mediaExtractor = MediaExtractor()
        mediaExtractor.setDataSource(LocalFileDataSource(path)) // 设置数据源
        selectTrack(mediaExtractor)
        val codec = requireNotNull(mediaCodec)
        mStartTimeForSync = SystemClock.uptimeMillis()
        codec.start() // 启动MediaCodec ，等待传入数据
        var buffer: Array<out ByteBuffer>? = null
        for (i in 0..30) {
            buffer = try {
                codec.inputBuffers
            } catch (e: IllegalStateException) {
                loge { e.stackTraceToString() }
                null
            }
            if (buffer == null) try {
                Thread.sleep(300)
            }catch (e:InterruptedException){
                e.printStackTrace()
                stop = true
                break
            }
            else break
        }
        if (buffer == null) return
        val inputBuffers = requireNotNull(buffer)
        var outputBuffers = codec.outputBuffers // 解码后的数据
        val info = MediaCodec.BufferInfo() // 用于描述解码得到的byte[]数据的相关信息
        var bIsEos = false
        val avSynchronizer = requireNotNull(context.avSynchronizer)

        var seekStartTime = -1L

        while (!stop && !interrupted()) {

            waitIfPaused() // pause


            val inIndex = codec.dequeueInputBuffer(0)
            if (!bIsEos) {
                if (inIndex >= 0) {
                    val buffer = inputBuffers[inIndex]
                    val nSampleSize = mediaExtractor.readSampleData(buffer, 0) // 读取一帧数据至buffer中
                    if (nSampleSize < 0) {
                        loge { "【Audio】 InputBuffer BUFFER_FLAG_END_OF_STREAM" }
                        codec.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                        bIsEos = true
                    } else {
                        // 填数据
                        logd {  "${identity()}【Audio】queue inputBufer at ${mediaExtractor.sampleTime / 1_000_000} s" }
                        codec.queueInputBuffer(inIndex, 0, nSampleSize, mediaExtractor.sampleTime, 0) // 通知MediaDecode解码刚刚传入的数据
                        val mSeekPts = avSynchronizer.mAudioSeekPositionMs * 1000
                        if (mSeekPts >= 0 ) {
                            if (!isSeeking){
                                mediaExtractor.seekTo(mSeekPts, MediaExtractor.SEEK_TO_NEXT_SYNC)
                                seekStartTime = SystemClock.uptimeMillis()
                                isSeeking = true
                            }else {
                               loge { "${identity()}【Audio】skip seek to ${mSeekPts} since we are done seeking, but output buffer pt is still not satisfied? mediaExtractor.sampleTime = ${ mediaExtractor.sampleTime} " }
                            }

                        } else {
                            mediaExtractor.advance() // 继续下一取样
                        }
                    }
                } else {
//                    bIsEos = true
                   loge {
                       "【Audio】dequeueInputBuffer return -1!!!!" + avSynchronizer.mAudioSeekPositionMs + "  current pos = " + mediaExtractor.sampleTime
                   }
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
                    logd { "INFO_OUTPUT_BUFFERS_CHANGED" }
                    outputBuffers = codec.outputBuffers
                }

                MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> Log.d(TAG, "New format " + codec.outputFormat)

                MediaCodec.INFO_TRY_AGAIN_LATER -> Log.d(TAG, "dequeueOutputBuffer timed out!")

                else -> {
                    val buffer = outputBuffers[outIndex]
                    val mSeekPts = avSynchronizer.mAudioSeekPositionMs * 1000
                    if (mSeekPts >= 0 ) { // 说明是刚刚seek过出一次
//                        if (info.presentationTimeUs - mSeekPts > 0) {
                        if (Math.abs(info.presentationTimeUs /1_000_000- mediaExtractor.sampleTime/1_000_000) <=1L  ) {
                            isSeeking = false
                            mStartTimeForSync = SystemClock.uptimeMillis() - info.presentationTimeUs / 1_000
                            loge {
                                """
                                ${identity()}
                                 after so many times , finally found valid buffer , buffer pts is ${info.presentationTimeUs / 1_000_000} and we already seek to ${mSeekPts / 1_000_000}, so we will continue
                                next sleep time is ${info.presentationTimeUs / 1000 - (SystemClock.uptimeMillis() - mStartTimeForSync)} ms
                                mediaExtractor.sampleTime  = ${mediaExtractor.sampleTime/1_000_000 }
                                ${if (seekStartTime>-1L)"took me ${SystemClock.uptimeMillis() - seekStartTime} ms to get render target frame" else ""}
                             """.lineSequence()
                                    .joinToString(transform = String::trimStart, separator = System.lineSeparator())
                            }
                            seekStartTime = -1
                            avSynchronizer.seekAudioCompleted()
                            context.avSynchronizer?._audioPtsMicroSeconds = info.presentationTimeUs
                        } else {
                            logw{
                                "【Audio】 invalid buffer , buffer pts is ${info.presentationTimeUs / 1_000_000} but we already seek to ${mSeekPts / 1_000_000}, so we will discard"
                            }

                        }

                    } else if (Math.abs(info.presentationTimeUs /1_000_000- mediaExtractor.sampleTime/1_000_000) <=1L ){
                        val diff = info.presentationTimeUs / 1000 - (SystemClock.uptimeMillis() - mStartTimeForSync)
                        logd {
                            "【Audio】 normal buffer , buffer pts is ${info.presentationTimeUs / 1_000_000} , we will sleep for $diff "
                        }
                        sleepRender(info)
                        context.avSynchronizer?._audioPtsMicroSeconds = info.presentationTimeUs
                        //用来保存解码后的数据
                        val outData = ByteArray(info.size)
                        buffer[outData]
                        //清空缓存
                        buffer.clear()
                        //播放解码后的数据
                        if (!mute && !stop ) {
                            mPlayer!!.play(outData, 0, info.size)
                        }
                    }
                    codec.releaseOutputBuffer(outIndex, false)
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
        while (diff > 0 && !isSeeking) {
            diff = try {
                logd { "【Audio】sleep  $diff ms" }
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

    private fun waitIfPaused() {
        if (context.state == PlayerState.PAUSED) {
            lock.withLock {
                logw { "${identity()} now will lock ,waiting for signal " }
                val sleepStartTime = SystemClock.uptimeMillis()
                condition.await() // block this
                val diffTime = SystemClock.uptimeMillis() - sleepStartTime
                mStartTimeForSync += diffTime
                logw { "${identity()} wake up from  lock , continue processing, we slept for ${diffTime / 1_000} seconds " }
            }
        }
    }

    fun wakeUp() {
        require(!lock.isLocked)
        lock.withLock {
            logw { "${identity()} prepare signal ${lock.holdCount} ${lock.hasQueuedThread(this)}  ${lock.hasQueuedThreads()}" }
            condition.signal()
            logw { "${identity()} done signalAll ${lock.holdCount} ${lock.hasQueuedThread(this)}  ${lock.hasQueuedThreads()}" }
        }
    }

    fun identity(): String {
        return "【Audio】 ${Thread.currentThread().name}"
    }

    fun pause() {
        if (lock.isHeldByCurrentThread) {
            lock.unlock()
        }
//       lock.unlock()
    }
}
