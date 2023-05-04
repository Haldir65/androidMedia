package com.me.harris.droidmedia.extractFrame

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import com.me.harris.awesomelib.utils.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception
import java.lang.RuntimeException
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

/**
 * 视频转码工具
 */
@Suppress("BlockingMethodInNonBlockingContext")
object VideoTranscodingUtils {

    /**
     * 转码输入视频为mp4
     */
    @JvmStatic
    suspend fun transcoding2Mp4(origin: String,context:Context): String? {
        return withContext(Dispatchers.IO) {
            val dir = context.getExternalFilesDir("transcodingVideos")
                ?: File(context.cacheDir, "transcodingVideos")
            try {
                dir.mkdirs()
            } catch (ignore: Exception) {
            }
            val output = File(dir, "${System.currentTimeMillis()}.mp4")
            var mediaMuxer: MediaMuxer? = null
            try {
                val videoExtractor = MediaExtractor()
                val audioExtractor = MediaExtractor()
                mediaMuxer =
//                    MediaMuxer(output.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_WEBM) // audio/
                    MediaMuxer(output.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

//                2022-04-27 10:21:17.698 11824-11868/? V/NuMediaExtractor: track of type 'video/x-vnd.on2.vp9' does not publish bitrate
//                2022-04-27 10:21:17.699 11824-11868/? V/NuMediaExtractor: track of type 'audio/opus' does not publish bitrate
//                2022-04-27 10:21:17.700 11824-11868/? E/MPEG4Writer: Unsupported mime 'video/x-vnd.on2.vp9'

                videoExtractor.setDataSource(origin)
                audioExtractor.setDataSource(origin)

                val numTracks: Int = videoExtractor.trackCount
                var muxerTrackNum = 0
                for (idx in 0 until numTracks) {
                    val trackFormat = videoExtractor.getTrackFormat(idx)
                    val mime = trackFormat.getString(MediaFormat.KEY_MIME) ?: ""
                    if (mime.contains("video")) {
                        videoExtractor.selectTrack(idx)
                        mediaMuxer.addTrack(trackFormat)
                        muxerTrackNum++
                    } else if (mime.contains("audio")) {
                        audioExtractor.selectTrack(idx)
                        mediaMuxer.addTrack(trackFormat)
                        muxerTrackNum++
                    }
                    if (muxerTrackNum >= 2) break
                    LogUtil.i("convert video track idx:$idx mime:$mime")
                }

                val atomicInteger = AtomicInteger(0)

                mediaMuxer.start()
                thread {
                    DecodeThread(videoExtractor, mediaMuxer, 0, atomicInteger).run()
                }
                thread {
                    DecodeThread(audioExtractor, mediaMuxer, 1, atomicInteger).run()
                }
                while (atomicInteger.get() < 2) {
                    Thread.sleep(0)
                }
                if (atomicInteger.get() > 2) throw RuntimeException("解码异常");
                LogUtil.e("=A=", output.absolutePath)
                output.absolutePath
            } catch (e: Throwable) {
                e.printStackTrace()
                output.delete()
                return@withContext null
            } finally {
                LogUtil.i("convert transcoding compile")
                mediaMuxer?.stop()
                mediaMuxer?.release()
            }
        }
    }

    /**
     * 解码线程
     */
    private class DecodeThread(
        val extractor: MediaExtractor,
        val mediaMuxer: MediaMuxer,
        val model: Int, //0:视频轨道,1:音频轨道
        val atomicInteger: AtomicInteger
    ) : Runnable {

        @SuppressLint("WrongConstant")
        override fun run() {
            try {
                val buffer = ByteBuffer.allocate(500 * 1024)
                val bufferInfo = MediaCodec.BufferInfo()

                var firstTime = 0L
                var size: Int

                while (extractor.readSampleData(buffer, 0).also { size = it } > 0) {
                    val rackIndex = extractor.sampleTrackIndex
                    if (firstTime == 0L) firstTime = extractor.sampleTime
                    bufferInfo.presentationTimeUs = extractor.sampleTime - firstTime
                    bufferInfo.flags = extractor.sampleFlags
                    bufferInfo.size = size
                    mediaMuxer.writeSampleData(rackIndex, buffer, bufferInfo)
                    extractor.advance()
                    LogUtil.i(" ${model} writing sample data at time:${bufferInfo.presentationTimeUs} ")
                }
                LogUtil.i("convert write success mode:$model")
                atomicInteger.addAndGet(1)
            } catch (e: Throwable) {
                e.printStackTrace()
                LogUtil.e("convert write has an exception mode:$model")
                atomicInteger.addAndGet(4)
            } finally {
                extractor.release()
            }
        }

    }
}