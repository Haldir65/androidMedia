package com.me.harris.extractframe.yuvrelated

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Rect
import android.media.Image
import android.media.Image.Plane
import android.media.ImageReader
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.CancellationSignal
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.me.harris.extractframe.ImageUtil
import com.me.harris.libyuv.ImageToBitmap
import com.me.harris.libyuv.YuvUtils
import com.me.harris.extractframe.yuvrelated.MediaCodecFrameExtractor.Companion.TAG
import java.nio.ByteBuffer

class MediaCodecFrameExtractor {


    private lateinit var videoFormat: MediaFormat
    private var duration = 0L
    private lateinit var mediaCodec: MediaCodec
    private lateinit var imageReader: ImageReader
    private lateinit var imageReaderHandlerThread: ImageReaderHandlerThread

    private val extractor = MediaExtractor()


    var requestStop = false

    private var filepath:String = ""


    companion object {
        const val TAG = "MediaCodecFrameExtractor"
    }



    fun release(){
        requestStop = true
        extractor.release()
        kotlin.runCatching {
            mediaCodec.stop()
        }
        kotlin.runCatching {
            mediaCodec.release()
        }
        kotlin.runCatching {
            imageReaderHandlerThread.quit()
        }
    }


    fun setDataSource(filepath:String){
        this.filepath = filepath
        extractor.setDataSource(filepath)
        val trackCount = extractor.trackCount
        for (i in 0 until trackCount) {
            val format = extractor.getTrackFormat(i)
            if (format.getString(MediaFormat.KEY_MIME).orEmpty().contains("video")) {
                videoFormat = format
                extractor.selectTrack(i)
                break
            }
        }
        requireNotNull(videoFormat)
        showFrameCountTotal(filepath)
        val imageFormat = ImageFormat.YUV_420_888
        val colorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible
        videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat)
        videoFormat.setInteger(MediaFormat.KEY_WIDTH, videoFormat.getInteger(MediaFormat.KEY_WIDTH))
        videoFormat.setInteger(
            MediaFormat.KEY_HEIGHT,
            videoFormat.getInteger(MediaFormat.KEY_HEIGHT)
        )
        duration = videoFormat.getLong(MediaFormat.KEY_DURATION)
        mediaCodec =
            MediaCodec.createDecoderByType(requireNotNull(videoFormat.getString(MediaFormat.KEY_MIME)))
        imageReader = ImageReader.newInstance(
            videoFormat.getInteger(MediaFormat.KEY_WIDTH),
            videoFormat.getInteger(MediaFormat.KEY_HEIGHT),
            imageFormat, //  因为返回JPEG格式需要进行encode，时间必然会长，这时候就不能用JPEG了，我们需要用到前面提到的ImageFormat.YUV_420_888
            3
        )
        imageReaderHandlerThread = ImageReaderHandlerThread("ImageReader")
        mediaCodec.configure(videoFormat, imageReader.surface, null, 0)
        mediaCodec.start()
    }

    fun getFrameAtTime(timeUs: Long, scale: Int, callback: (bmp: Bitmap) -> Unit) {
        require(filepath.isNotEmpty())
        showFrameCountTotal(filepath)
        var rotation = 0
        if (videoFormat.containsKey(MediaFormat.KEY_ROTATION)) {
            rotation = videoFormat.getInteger(MediaFormat.KEY_ROTATION)
        }
        imageReader.setOnImageAvailableListener(
            MyOnImageAvailableListener(
                callback = callback,
                scale = scale,
                rotation = rotation
            ), imageReaderHandlerThread.handler
        )
        extractor.seekTo(timeUs, MediaExtractor.SEEK_TO_NEXT_SYNC)
        val bufferInfo = MediaCodec.BufferInfo()
        val timeOut = 5 * 1000L //5ms
        var inputDone = false
        var outputDone = false
        while (!outputDone) {
            if (requestStop) return

            if (!inputDone) {
                val inputBufferIndex = mediaCodec.dequeueInputBuffer(timeOut)
                if (inputBufferIndex >= 0) {
                    val inputBuffers = mediaCodec.getInputBuffer(inputBufferIndex)
                    val sampleData = extractor.readSampleData(inputBuffers!!, 0)
                    if (sampleData > 0) {
                        val sampleTime = extractor.sampleTime
                        mediaCodec.queueInputBuffer(inputBufferIndex, 0, sampleData, sampleTime, 0)
                        extractor.advance()
                    } else {
                        //小于0 ，说明读完了
                        mediaCodec.queueInputBuffer(
                            inputBufferIndex,
                            0,
                            0,
                            0L,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM
                        )
                        inputDone = true
                    }
                }
            }

            if (!outputDone) {
                val status = mediaCodec.dequeueOutputBuffer(bufferInfo, timeOut)
                if (status == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    Log.w(TAG, "dequeueOutputBuffer  INFO_TRY_AGAIN_LATER")
                } else if (status == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    Log.w(TAG, "dequeueOutputBuffer  INFO_OUTPUT_FORMAT_CHANGED")
                } else if (status == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    Log.w(TAG, "dequeueOutputBuffer  INFO_OUTPUT_BUFFERS_CHANGED")

                } else {
                    if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM)!=0){
                        outputDone = true
                    }
                    outputDone = true
                    val dorender = bufferInfo.size!=0
                    val presentationTimeUs = bufferInfo.presentationTimeUs
                    Log.w(TAG,"surface decoder given buffer duartion = ${duration/1000_000} $status (size = ${bufferInfo.size}) dorender = $dorender  presentationTimeUs = $presentationTimeUs")
                    //直接送显
                    mediaCodec.releaseOutputBuffer(status,dorender)
                }
            }

        }
        release()
    }

    fun getFramesInterval(intervalMs:Long, scale:Int, cancellationSignal:CancellationSignal, callback: (bmp: Bitmap) -> Unit){
        require(filepath.isNotEmpty())
        showFrameCountTotal(filepath)
        var lastPresentationTimeUs = 0L
        var rotation = 0
        if (videoFormat.containsKey(MediaFormat.KEY_ROTATION)) {
            rotation = videoFormat.getInteger(MediaFormat.KEY_ROTATION)
        }
        extractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
        imageReader.setOnImageAvailableListener(
            MyOnImageAvailableListener(
                callback = callback,
                scale = scale,
                rotation = rotation
            ), imageReaderHandlerThread.handler
        )

        val bufferInfo = MediaCodec.BufferInfo()
        val timeOut = 5 * 1000L //5ms
        var inputDone = false
        var outputDone = false
        //开始进行解码。
        var count = 1
        while (!outputDone) {
            if (cancellationSignal.isCanceled) requestStop = true
            if (requestStop) return

            if (!inputDone) {
                val inputBufferIndex = mediaCodec.dequeueInputBuffer(timeOut)
                if (inputBufferIndex >= 0) {
                    val inputBuffers = mediaCodec.getInputBuffer(inputBufferIndex)
                    val sampleData = extractor.readSampleData(inputBuffers!!, 0)
                    if (sampleData > 0) {
                        val sampleTime = extractor.sampleTime
                        mediaCodec.queueInputBuffer(inputBufferIndex, 0, sampleData, sampleTime, 0)
                        if (intervalMs ==0L){
                            extractor.advance()
                        }else {
                            val nextTime = count * intervalMs *1000
                            if (nextTime > duration){
                                extractor.advance()
                            }else {
                                extractor.seekTo(nextTime,MediaExtractor.SEEK_TO_CLOSEST_SYNC)
                               count++
                            }
                            Log.w(TAG, "seekTo nextTime = ${nextTime/1000_000} ")
                        }
                    } else {
                        Log.w(TAG, "seekTo sampleData = $sampleData ")
                        //小于0 ，说明读完了
                        mediaCodec.queueInputBuffer(
                            inputBufferIndex,
                            0,
                            0,
                            0L,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM
                        )
                        inputDone = true
                    }
                }
            }

            if (!outputDone) {
                val status = mediaCodec.dequeueOutputBuffer(bufferInfo, timeOut)
                if (status == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    Log.w(TAG, "dequeueOutputBuffer  INFO_TRY_AGAIN_LATER")
                } else if (status == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    Log.w(TAG, "dequeueOutputBuffer  INFO_OUTPUT_FORMAT_CHANGED")
                } else if (status == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    Log.w(TAG, "dequeueOutputBuffer  INFO_OUTPUT_BUFFERS_CHANGED")

                } else {
                    if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM)!=0){
                        Log.w(TAG,"saw output eos")
                        outputDone = true
                    }
                    var dorender = bufferInfo.size!=0
                    val presentationTimeUs = bufferInfo.presentationTimeUs
                    if (lastPresentationTimeUs == 0L){
                        lastPresentationTimeUs = presentationTimeUs
                    } else {
                        val diff = presentationTimeUs - lastPresentationTimeUs
                        if (intervalMs!=0L){
                            if (diff /1000 <(intervalMs-10)){
                                val lastDiff = duration-presentationTimeUs
                                Log.d(TAG, "duration=$duration, lastDiff=$lastDiff")
                                if (lastDiff < 50 *1000 && diff >0){ // 距离最后50ms
                                    //输出最后一帧.强制输出最后一帧附近的帧的话，会比用metaRetiriever多一帧
                                    lastPresentationTimeUs = duration
                                }else {
                                    dorender = false
                                }
                            }else {
                                lastPresentationTimeUs = presentationTimeUs
                            }
                            Log.d(TAG,"diff time in ms = ${diff/1000}")
                        }
                    }
                    if (dorender){
                        Log.w(TAG," ${presentationTimeUs/1000_000}秒 surface decoder given buffer $status (size = )${bufferInfo.size} dorender = $dorender  presentationTimeUs = $presentationTimeUs")
                    }
                    //直接送显
                    mediaCodec.releaseOutputBuffer(status,dorender && (bufferInfo.flags and MediaCodec.BUFFER_FLAG_KEY_FRAME)!=0)
                }
            }

        }
        release()

    }

}

private class ImageReaderHandlerThread(name: String) : HandlerThread(name) {

    init {
        start()
    }

    val handler = Handler(looper)

}


fun showFrameCountTotal(path:String){
    if (path.isNotEmpty() && Build.VERSION.SDK_INT>= Build.VERSION_CODES.P){
        val retriver = MediaMetadataRetriever()
        retriver.setDataSource(path)
        val frameCountStr = retriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT)
        Log.w(TAG,"video ${path} has total ${frameCountStr} frames")
    }
}

// https://stackoverflow.com/a/69886048
 fun getSyncFrameTimestamps(source: MediaExtractor): List<Long> {
    val result = mutableListOf<Long>()

    var lastSampleTime = -1L
    var sampleTime = source.sampleTime
    var first = true

    while ((sampleTime >= 0L || first)&& sampleTime != lastSampleTime) {
        if ((source.sampleFlags and MediaExtractor.SAMPLE_FLAG_SYNC )!= 0) {
            result.add(sampleTime)
        }
        first = false
        source.seekTo(sampleTime + 1L, MediaExtractor.SEEK_TO_NEXT_SYNC)
        lastSampleTime = sampleTime
        sampleTime = source.sampleTime
    }

    return result
}