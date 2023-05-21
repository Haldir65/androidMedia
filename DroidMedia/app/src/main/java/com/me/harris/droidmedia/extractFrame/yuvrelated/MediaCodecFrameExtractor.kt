package com.me.harris.droidmedia.extractFrame.yuvrelated

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.media.Image
import android.media.ImageReader
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.me.harris.droidmedia.extractFrame.yuvrelated.MediaCodecFrameExtractor.Companion.TAG
import com.me.harris.libyuv.YuvUtils
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
                    Log.w(TAG,"surface decoder given buffer $status (size = )${bufferInfo.size} dorender = $dorender  presentationTimeUs = $presentationTimeUs")
                    //直接送显
                    mediaCodec.releaseOutputBuffer(status,dorender)
                }
            }

        }
        release()
    }

    fun getFramesInterval(intervalMs:Long, scale:Int,callback: (bmp: Bitmap) -> Unit){
        require(filepath.isNotEmpty())
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
                                extractor.seekTo(nextTime,MediaExtractor.SEEK_TO_PREVIOUS_SYNC)
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
                    Log.w(TAG," ${presentationTimeUs/1000_000}秒 surface decoder given buffer $status (size = )${bufferInfo.size} dorender = $dorender  presentationTimeUs = $presentationTimeUs")
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

private open class MyOnImageAvailableListener(
    val callback: (bmp: Bitmap) -> Unit,
    val scale: Int,
    val rotation: Int
) : ImageReader.OnImageAvailableListener {


    override fun onImageAvailable(reader: ImageReader?) {

        var img: Image? = null
            try {
                img = reader?.acquireLatestImage() ?: return
                img.planes?.get(0)?.buffer ?: return
                if (img.format==ImageFormat.YUV_420_888){
                    Log.w(TAG,"image.format = ImageFormat.YUV_420_888")
                }
                val bmp = getBitmapScale(img, scale, rotation)
//            val bmp = getBitmapUsingJava(img!!)
//            val bmp = getBitmapUsingNV21(img!!)
                if (callback != null && bmp != null) {
                    Log.w(TAG,"got one bitmap not null!!")
                    callback(bmp)
                }
            }catch (e:Exception){
                Log.w(TAG,"having some trouble onImageAvailable ${e.stackTraceToString()} ")

            } finally {
                img?.close() /// crital !!!!  用完就关闭，否则只能回调3次
            }


    }

    private fun getBitmapUsingJava(image:Image):Bitmap{
        val width = image.width
        val height = image.height
        val i420bytes = CameraUtil.getDataFromImage(image, CameraUtil.COLOR_FormatI420)

        val i420RorateBytes = BitmapUtil.rotateYUV420Degree90(i420bytes, width, height)
        val nv21bytes = BitmapUtil.I420Tonv21(i420RorateBytes, height, width)
        //TODO check YUV数据是否正常
//                BitmapUtil.dumpFile("mnt/sdcard/1.yuv", i420bytes);

        //TODO check YUV数据是否正常
//                BitmapUtil.dumpFile("mnt/sdcard/1.yuv", i420bytes);
        val bitmap = BitmapUtil.getBitmapImageFromYUV(nv21bytes, height, width)
        return bitmap

    }

    private fun getBitmapUsingNV21(img: Image):Bitmap{
        val nv21bytes = BitmapUtil.toNv21(img)
        val width = img.width
        val height = img.height
        val yuvSrc = ByteBuffer.allocateDirect(nv21bytes.size)
        yuvSrc.put(nv21bytes)
        val outBuffer = ByteBuffer.allocateDirect(width / scale * height / scale * 4)
        require(img.format == ImageFormat.YUV_420_888)
        val strideY = img.planes[0].rowStride
        val uvStride = img.planes[1].rowStride
        YuvUtils.NV21ToRGBA(width,height,yuvSrc,strideY,uvStride,outBuffer)
        val bitmap  =  Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(outBuffer)
        return bitmap
    }

    private fun getBitmapScale(img: Image, scale: Int, rotation: Int): Bitmap {
        val width = img.width / scale
        val height = img.height / scale
        val bytes = getDataFromYUV420Scale(image = img, scale = scale, rotation = rotation)
        val bitmap = if (rotation == 90 || rotation == 270) {
            Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888)
        } else {
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        }
        bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(bytes))
        return bitmap
    }

    private fun getDataFromYUV420Scale(image: Image, scale: Int, rotation: Int): ByteArray {
        val width = image.width
        val height = image.height
        // Read image data
        // Read image data
        val planes = image.planes

        val argb = ByteArray(width / scale * height / scale * 4)

        //值得注意的是在Java层传入byte[]以RGBA顺序排列时，libyuv是用ABGR来表示这个排列
        //libyuv表示的排列顺序和Bitmap的RGBA表示的顺序是反向的。
        // 所以实际要调用libyuv::ABGRToI420才能得到正确的结果。
        val outBuffer = ByteBuffer.allocateDirect(width / scale * height / scale * 4)
        YuvUtils.yuvI420ToABGRWithScale(
            argb,
            planes[0].buffer, planes[0].rowStride,
            planes[1].buffer, planes[1].rowStride,
            planes[2].buffer, planes[2].rowStride,
            width, height,
            scale,
            rotation
        )
//        YuvUtils.yuvI420ToABGR(
//            argb,
//            planes[0].buffer, planes[0].rowStride,
//            planes[1].buffer, planes[1].rowStride,
//            planes[2].buffer, planes[2].rowStride,
//            width, height
//        )
        return argb
    }



}