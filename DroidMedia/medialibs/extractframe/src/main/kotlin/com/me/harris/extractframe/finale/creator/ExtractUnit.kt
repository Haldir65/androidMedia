package com.me.harris.extractframe.finale.creator

import android.app.Application
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.media.Image
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.annotation.WorkerThread
import com.me.harris.awesomelib.utils.LogUtil
import com.me.harris.extractframe.NV21ToBitmap
import com.me.harris.extractframe.VideoDecoder
import com.me.harris.extractframe.VideoDecoder.COLOR_FORMAT_NV21
import com.me.harris.extractframe.contract.ExtractConfiguration
import com.me.harris.extractframe.parallel.Range
import com.me.harris.extractframe.parallel.saveBitMapToDir
import com.me.harris.libyuv.ImageToBitmap
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import okio.AsyncTimeout.Companion.lock
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.CountDownLatch

const val TAG = ExtractConfiguration.LOG_TAG

internal class ExtractUnit(
    val id: Int,
    val config: ExtractConfig,
    val range: Range,
    val context: Application
) {

    private val DEFAULT_TIMEOUT_US = 100000L

    private val useLibyuv = config.useLibYuv

    private lateinit var mYuvBuffer: ByteArray

    private val renderScriptConverter = NV21ToBitmap(context.applicationContext)

    private val bmpStorage: ExtractBitMapStoreK by lazy { ExtractBitMapStoreK() }

    @WorkerThread
    suspend fun doingExtract() {
        val filePath: String = config.filepath
        val durationMicroSeconds = config.videoDurationMicroSecond
        val gapMicroSeconds = config.gapInBetweenSeconds * 1000_000
        var extractor: MediaExtractor? = null
        var decoder: MediaCodec? = null
        try {
            extractor = MediaExtractor()
            extractor.setDataSource(filePath)
            val index = selectVideoTrack(extractor = extractor)
            if (index < 0) {
                error("failed to selectVideoTrack for material ${filePath}")
            }
            val format = extractor.getTrackFormat(index)
            val mime = format.getString(MediaFormat.KEY_MIME)!!
            decoder = requireNotNull(MediaCodec.createDecoderByType(mime))
            val width = format.getInteger(MediaFormat.KEY_WIDTH)
            val height = format.getInteger(MediaFormat.KEY_HEIGHT)
            val yuvLength = width * height * 3 / 2
            mYuvBuffer = ByteArray(yuvLength)
            decoder.configure(format, null, null, 0)
            decoder.start()
            val info = MediaCodec.BufferInfo()
            var sawInputEOS = false
            var sawOutputEOS = false
            var outputFrameCount = 0
            var rangeIndex = 0
            var startTime = range.points[rangeIndex]
            extractor.seekTo(startTime, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
            Log.i(TAG, "extractor ${identity()} initially seek to ${startTime} ")
            while (!sawOutputEOS) {
                currentCoroutineContext().ensureActive()
                if (!sawInputEOS) {
                    val inputBufferId = kotlin.runCatching { decoder.dequeueInputBuffer(DEFAULT_TIMEOUT_US) }.onFailure { sawOutputEOS = true }.getOrElse { -1 }
                    if (inputBufferId >= 0) {
                        val inputBuffer = requireNotNull(decoder.getInputBuffer(inputBufferId))
                        val sampleSize = extractor.readSampleData(inputBuffer, 0)
                        if (sampleSize < 0) {
                            decoder.queueInputBuffer(
                                inputBufferId,
                                0,
                                0,
                                0L,
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM
                            )
//                            decoder.signalEndOfInputStream()
                            sawInputEOS = true
                            Log.i(TAG, " ${identity()} sawInputEos set to true")
                        } else {
                            val time = extractor.sampleTime
                            rangeIndex++
                            if (rangeIndex >= range.points.size || Math.abs(time - range.points.last()) < 1_000_000) {
//                                decoder.signalEndOfInputStream()
                                decoder.queueInputBuffer(
                                    inputBufferId,
                                    0,
                                    0,
                                    0L,
                                    MediaCodec.BUFFER_FLAG_END_OF_STREAM
                                )
                                sawInputEOS = true
                                Log.i(
                                    TAG,
                                    "${identity()} current sampleTime is ${time} manually sawInputEos set to true"
                                )
                            } else {
                                decoder.queueInputBuffer(inputBufferId, 0, sampleSize, time, 0)
                                val t = range.points.getOrNull(rangeIndex)
                                if (t != null) {
                                    extractor.seekTo(t, MediaExtractor.SEEK_TO_NEXT_SYNC)
                                    Log.i(
                                        TAG,
                                        "extractor ${identity()} seek to ${range.points[rangeIndex]} "
                                    )
                                } else {
                                    Log.e(
                                        TAG, """
                                        extractor ${identity()} fatal flaw ,
                                        ranges = ${range.points.joinToString(separator = " | ") { a -> a.toString() }}
                                        rangeIndex = ${rangeIndex}
                                        range.points.size = ${range.points.size}
                                    """.trimIndent()
                                    )
                                }
                            }
                        }
                    }
                }
                val outputBufferId = decoder.dequeueOutputBuffer(info, DEFAULT_TIMEOUT_US)
                if (outputBufferId >= 0) {
                    if ((info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        sawOutputEOS = true
                        Log.i(
                            TAG,
                            " ${identity()} saw output = true pt = ${info.presentationTimeUs} "
                        )
                    }
                    if ((info.flags and MediaCodec.BUFFER_FLAG_KEY_FRAME) != 0) {
                        Log.w(
                            TAG,
                            " ${identity()} 【I帧】 BUFFER_FLAG_KEY_FRAME pt = ${info.presentationTimeUs} "
                        )
                    }
                    if ((info.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        Log.w(
                            TAG,
                            " ${identity()} 【首帧信息帧】 BUFFER_FLAG_CODEC_CONFIG pt = ${info.presentationTimeUs} "
                        )
                        // flags = 4；End of Stream。
                        //flags = 2；首帧信息帧。
                        //flags = 1；关键帧。
                        //flags = 0；普通帧。
                    }
                    if ((info.flags and MediaCodec.BUFFER_FLAG_PARTIAL_FRAME) != 0) {
                        Log.w(
                            TAG,
                            " ${identity()} 【B帧】 BUFFER_FLAG_PARTIAL_FRAME pt = ${info.presentationTimeUs} "
                        )
                    }
                    if ((info.flags and 16) != 0) {
                        Log.w(
                            TAG,
                            " ${identity()} 【Muxer帧】 BUFFER_FLAG_MUXER_DATA pt = ${info.presentationTimeUs} "
                        )
                    }
                    if (info.flags == 0) {
                        Log.w(TAG, " ${identity()} 【普通帧】 0 pt = ${info.presentationTimeUs} ")
                    }
                    if (info.size > 0) {
                        val presentationTimeUs = info.presentationTimeUs
                        outputFrameCount++;
                        val image = requireNotNull(decoder.getOutputImage(outputBufferId))
                        val imageFormat = image.format
                        require(imageFormat == ImageFormat.YUV_420_888)
                        if (useLibyuv) {
                            // image to jpeg file store
                            var now = System.currentTimeMillis()
//                            val bmp = ImageToBitmap.getBitmapFromImageUsingLibYUV(image)
                            val bmp = bmpStorage.getBitmapFromImageUsingLibYUV(image)
                            Log.w(
                                TAG,
                                " ${identity()} transforming yuv ${mYuvBuffer?.size} to bmp at presentationTimeUs ${presentationTimeUs / 1000_000} cost me ${System.currentTimeMillis() - now} ms"
                            )
                            now = System.currentTimeMillis()
                            val abspath =
                                "${config.storageDir}${File.separator}image_${(id + 1) * 1000 + outputFrameCount}.jpg"
                            savePicFile(bmp, abspath)
                            Log.w(
                                TAG,
                                " ${identity()} [libyuv] saving  bmp at presentationTimeUs ${presentationTimeUs / 1000_000} to ${abspath} cost me ${System.currentTimeMillis() - now} ms"
                            )
                            image.close()
                        } else {
                            // image to
                            // getDataFromImage to mYuvBuffer
                            getDataFromImage(image, COLOR_FORMAT_NV21, width, height)
                            image.close()
                            var now = System.currentTimeMillis()
                            val bmp = renderScriptConverter.nv21ToBitmap(mYuvBuffer, width, height)
                            Log.w(
                                TAG,
                                " ${identity()} [renderscript] transforming yuv ${mYuvBuffer?.size} to bmp at presentationTimeUs ${presentationTimeUs / 1000_000} cost me ${System.currentTimeMillis() - now} ms"
                            )
                            now = System.currentTimeMillis()
                            val abspath =
                                "${config.storageDir}${File.separator}image_${(id + 1) * 1000 + outputFrameCount}.jpg"
                            savePicFile(bmp, abspath)
                            Log.w(
                                TAG,
                                " ${identity()} saving  bmp at presentationTimeUs ${presentationTimeUs / 1000_000} to ${abspath} cost me ${System.currentTimeMillis() - now} ms"
                            )
                        }
                        decoder.releaseOutputBuffer(outputBufferId, false)
                    } else {
                        Log.i(TAG, "${identity()} codec info.size = ${info.size} ")
                    }
                } else {
                    Log.w(TAG, "${identity()} dequeueOutputBuffer = $outputBufferId ")
                }
            }
        } finally {
            Log.w(TAG, "${identity()} release codec and extractor")
            extractor?.release()
            kotlin.runCatching {
                decoder?.stop()
                decoder?.release()
            }

        }
    }


    @WorkerThread
    suspend fun doingExtractAsync() {
        val filePath: String = config.filepath
        val durationMicroSeconds = config.videoDurationMicroSecond
        val gapMicroSeconds = config.gapInBetweenSeconds * 1000_000
        val lock = Mutex(locked = true)
        var decoder: MediaCodec? = null

        val handlerThread = object :HandlerThread("${id}-extract-unit"){

        }.apply {
            start()
        }
        val handler = object :Handler(handlerThread.looper) {

            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                Log.w("=A=","${identity()} handle message ")
            }
        }

        var extractor: MediaExtractor = MediaExtractor()
        extractor.setDataSource(filePath)
        val index = selectVideoTrack(extractor = extractor!!)
        if (index < 0) {
            error("failed to selectVideoTrack for material ${filePath}")
        }
        var rangeIndex = 0
        val format = extractor.getTrackFormat(index)
        val mime = format.getString(MediaFormat.KEY_MIME)!!
        decoder = requireNotNull(MediaCodec.createDecoderByType(mime))
        val width = format.getInteger(MediaFormat.KEY_WIDTH)
        val height = format.getInteger(MediaFormat.KEY_HEIGHT)
        val yuvLength = width * height * 3 / 2
        //            val info = MediaCodec.BufferInfo()
        var sawInputEOS = false
        var sawOutputEOS = false
        var outputFrameCount = 0
        var startTime = range.points[rangeIndex]
        mYuvBuffer = ByteArray(yuvLength)


        decoder!!.setCallback(object : MediaCodec.Callback() {
                    override fun onInputBufferAvailable(codec: MediaCodec, inputBufferId: Int) {
                        val inputBuffer = codec.getInputBuffer(inputBufferId)?:return
                        if (sawInputEOS) return
                        val sampleSize = extractor!!.readSampleData(inputBuffer, 0)
                        if (sampleSize < 0) {
                            codec.queueInputBuffer(
                                inputBufferId,
                                0,
                                0,
                                0L,
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM
                            )
                            sawInputEOS = true
//                            decoder.signalEndOfInputStream()
                            Log.i(TAG, " ${identity()} sawInputEos set to true")
                        } else {
                            val time = extractor!!.sampleTime
                            rangeIndex++
                            if (rangeIndex >= range.points.size || Math.abs(time - range.points.last()) < 1_000_000) {
//                                decoder.signalEndOfInputStream()
                                codec.queueInputBuffer(
                                    inputBufferId,
                                    0,
                                    0,
                                    0L,
                                    MediaCodec.BUFFER_FLAG_END_OF_STREAM
                                )
                                Log.i(
                                    TAG,
                                    "${identity()} current sampleTime is ${time} manually sawInputEos set to true"
                                )
                                sawInputEOS = true

                            } else {
                                codec.queueInputBuffer(inputBufferId, 0, sampleSize, time, 0)
                                val t = range.points.getOrNull(rangeIndex)
                                if (t != null) {
                                    extractor!!.seekTo(t, MediaExtractor.SEEK_TO_NEXT_SYNC)
                                    Log.i(
                                        TAG,
                                        "extractor ${identity()} seek to ${range.points[rangeIndex]} "
                                    )
                                } else {
                                    Log.e(
                                        TAG, """
                                        extractor ${identity()} fatal flaw ,
                                        ranges = ${range.points.joinToString(separator = " | ") { a -> a.toString() }}
                                        rangeIndex = ${rangeIndex}
                                        range.points.size = ${range.points.size}
                                    """.trimIndent()
                                    )
                                }
                            }
                        }
                    }

                    override fun onOutputBufferAvailable(
                        codec: MediaCodec,
                        outputBufferId: Int,
                        info: MediaCodec.BufferInfo
                    ) {

                        if (outputBufferId >= 0) {
                            if ((info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                                sawOutputEOS = true
                                Log.i(
                                    TAG,
                                    " ${identity()} saw output = true pt = ${info.presentationTimeUs} "
                                )
                            }
                            if ((info.flags and MediaCodec.BUFFER_FLAG_KEY_FRAME) != 0) {
                                Log.w(
                                    TAG,
                                    " ${identity()} 【I帧】 BUFFER_FLAG_KEY_FRAME pt = ${info.presentationTimeUs} "
                                )
                            }
                            if ((info.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                                Log.w(
                                    TAG,
                                    " ${identity()} 【首帧信息帧】 BUFFER_FLAG_CODEC_CONFIG pt = ${info.presentationTimeUs} "
                                )
                                // flags = 4；End of Stream。
                                //flags = 2；首帧信息帧。
                                //flags = 1；关键帧。
                                //flags = 0；普通帧。
                            }
                            if ((info.flags and MediaCodec.BUFFER_FLAG_PARTIAL_FRAME) != 0) {
                                Log.w(
                                    TAG,
                                    " ${identity()} 【B帧】 BUFFER_FLAG_PARTIAL_FRAME pt = ${info.presentationTimeUs} "
                                )
                            }
                            if ((info.flags and 16) != 0) {
                                Log.w(
                                    TAG,
                                    " ${identity()} 【Muxer帧】 BUFFER_FLAG_MUXER_DATA pt = ${info.presentationTimeUs} "
                                )
                            }
                            if (info.flags == 0) {
                                Log.w(
                                    TAG,
                                    " ${identity()} 【普通帧】 0 pt = ${info.presentationTimeUs} "
                                )
                            }
                            if (info.size > 0) {
                                val presentationTimeUs = info.presentationTimeUs
                                outputFrameCount++;
                                val image = requireNotNull(codec.getOutputImage(outputBufferId))
                                val imageFormat = image.format
                                require(imageFormat == ImageFormat.YUV_420_888)
                                if (useLibyuv) {
                                    // image to jpeg file store
                                    var now = System.currentTimeMillis()
//                            val bmp = ImageToBitmap.getBitmapFromImageUsingLibYUV(image)
//                                    val bmp = bmpStorage.getBitmapFromImageUsingLibYUV(image)
                                    val bmp = ImageToBitmap.getBitMapFromImageUsingYUVImage(image,1,0) // Java YUVImage，一切正常

                                    Log.w(
                                        TAG,
                                        " ${identity()} transforming yuv ${mYuvBuffer?.size} to bmp at presentationTimeUs ${presentationTimeUs / 1000_000} cost me ${System.currentTimeMillis() - now} ms"
                                    )
                                    now = System.currentTimeMillis()
                                    val abspath =
                                        "${config.storageDir}${File.separator}image_${(id + 1) * 1000 + outputFrameCount}.jpg"
                                    savePicFile(bmp, abspath)
                                    Log.w(
                                        TAG,
                                        " ${identity()} [libyuv] saving  bmp at presentationTimeUs ${presentationTimeUs / 1000_000} to ${abspath} cost me ${System.currentTimeMillis() - now} ms"
                                    )
                                    image.close()
                                } else {
                                    // image to
                                    // getDataFromImage to mYuvBuffer
                                    getDataFromImage(image, COLOR_FORMAT_NV21, width, height)
                                    image.close()
                                    var now = System.currentTimeMillis()
                                    val bmp = renderScriptConverter.nv21ToBitmap(
                                        mYuvBuffer,
                                        width,
                                        height
                                    )
                                    Log.w(
                                        TAG,
                                        " ${identity()} [renderscript] transforming yuv ${mYuvBuffer?.size} to bmp at presentationTimeUs ${presentationTimeUs / 1000_000} cost me ${System.currentTimeMillis() - now} ms"
                                    )
                                    now = System.currentTimeMillis()
                                    val abspath =
                                        "${config.storageDir}${File.separator}image_${(id + 1) * 1000 + outputFrameCount}.jpg"
                                    savePicFile(bmp, abspath)
                                    Log.w(
                                        TAG,
                                        " ${identity()} saving  bmp at presentationTimeUs ${presentationTimeUs / 1000_000} to ${abspath} cost me ${System.currentTimeMillis() - now} ms"
                                    )
                                }
                                codec.releaseOutputBuffer(outputBufferId, false)
                            } else {
                                Log.i(TAG, "${identity()} codec info.size = ${info.size} ")
                            }
                        } else {
                            Log.w(TAG, "${identity()} dequeueOutputBuffer = $outputBufferId ")
                        }
                        if (sawOutputEOS) {
                            lock.unlock()
                        }

            }

            override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
                        Log.e("=A=","""
                            ${identity()} codec ${codec}
                           diagnosticInfo =  ${e.diagnosticInfo}
                           ${e.errorCode}
                           ${e.isRecoverable}
                           ${e.isTransient}
                           ${e.stackTraceToString()}
                           ///    codec android.media.MediaCodec@ad6239b
//                         diagnosticInfo =  android.media.MediaCodec.error_1100
//                         1100   AMEDIACODEC_ERROR_INSUFFICIENT_RESOURCE = 1100,
//                         false
//                         false
//                         android.media.MediaCodec CodecException: Error 0xfffffff4

                        """.trimIndent())
                        lock.unlock()

            }

            override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {

            }

                }, handler)
                decoder!!.configure(format, null, null, 0)
                decoder!!.start()
                extractor!!.seekTo(startTime, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
                Log.i(TAG, "extractor ${identity()} initially seek to ${startTime} ")
        lock.lock()
        handlerThread.quitSafely()
        extractor?.release()
        kotlin.runCatching {
            decoder?.stop()
            decoder?.release()
        }
        Log.w(TAG, "${identity()} release codec and extractor")
    }


    private fun byteArrayToBitMapUsingRenderScript() {
    }

    private fun getDataFromImage(image: Image, colorFormat: Int, width: Int, height: Int) {
        require(!(colorFormat != VideoDecoder.COLOR_FORMAT_I420 && colorFormat != VideoDecoder.COLOR_FORMAT_NV21 && colorFormat != VideoDecoder.COLOR_FORMAT_NV12)) { "only support COLOR_FormatI420 " + "and COLOR_FormatNV21" }
        val crop = image.cropRect
        //        Log.v(TAG,"crop width: " + crop.width() + " ,height: "+ crop.height() + " format = "+ image.getFormat() );
        // image.getFormat()  == ImageFormat.YUV_420_888
        val planes = image.planes
        val rowData = ByteArray(planes[0].rowStride)
        var channelOffset = 0
        var outputStride = 1
        for (i in planes.indices) {
            when (i) {
                0 -> {
                    channelOffset = 0
                    outputStride = 1
                }

                1 -> if (colorFormat == VideoDecoder.COLOR_FORMAT_I420) {
                    channelOffset = width * height
                    outputStride = 1
                } else if (colorFormat == VideoDecoder.COLOR_FORMAT_NV21) {
                    channelOffset = width * height + 1
                    outputStride = 2
                } else if (colorFormat == VideoDecoder.COLOR_FORMAT_NV12) {
                    channelOffset = width * height
                    outputStride = 2
                }

                2 -> if (colorFormat == VideoDecoder.COLOR_FORMAT_I420) {
                    channelOffset = (width * height * 1.25).toInt()
                    outputStride = 1
                } else if (colorFormat == VideoDecoder.COLOR_FORMAT_NV21) {
                    channelOffset = width * height
                    outputStride = 2
                } else if (colorFormat == VideoDecoder.COLOR_FORMAT_NV12) {
                    channelOffset = width * height + 1
                    outputStride = 2
                }

                else -> {}
            }
            val buffer = planes[i].buffer
            val rowStride = planes[i].rowStride
            val pixelStride = planes[i].pixelStride
            val shift = if (i == 0) 0 else 1
            val w = width shr shift
            val h = height shr shift
            buffer.position(rowStride * (crop.top shr shift) + pixelStride * (crop.left shr shift))
            for (row in 0 until h) {
                var length: Int
                if (pixelStride == 1 && outputStride == 1) {
                    length = w
                    buffer[mYuvBuffer, channelOffset, length]
                    channelOffset += length
                } else {
                    length = (w - 1) * pixelStride + 1
                    buffer[rowData, 0, length]
                    for (col in 0 until w) {
                        mYuvBuffer[channelOffset] = rowData[col * pixelStride]
                        channelOffset += outputStride
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length)
                }
            }
        }
    }

    private fun selectVideoTrack(extractor: MediaExtractor): Int {
        val numTracks = extractor.trackCount
        for (i in 0 until numTracks) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime!!.startsWith("video/")) {
                extractor.selectTrack(i)
                return i
            }
        }
        return -1
    }

    fun identity(): String {
        val str = """

            [codec ${id}]
            Thread.currentThread().name = ${Thread.currentThread().name}
            Thread.currentThread().id = ${Thread.currentThread().id}
            isMainThread = ${Looper.getMainLooper() == Looper.myLooper()}

        """.trimIndent()
        return str
    }

    @Throws(IOException::class)
    private fun savePicFile(bitmap: Bitmap?, savePath: String) {
        if (bitmap == null) {
            LogUtil.d("savePicFile failed, bitmap is null.")
            return
        }
        LogUtil.d("savePicFile step 1, bitmap is not null. size = ${bitmap.allocationByteCount / (1024 * 1024)}MB")
        val ratio = ExtractConfiguration.BITMAP_RESIZE_FACTOR
        val bmp = if(ratio>0) Bitmap.createScaledBitmap(bitmap,bitmap.width/ratio,bitmap.height/ratio,false) else bitmap
        if (ExtractConfiguration.SAVE_BIT_MAP_USE_TURBO_JPEG){
            saveJpeg(bmp,100,"",savePath,true,false)
        }else {
            val file = File(savePath)
            if (!file.exists()) {
                file.createNewFile()
            }
            val outputStream = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.WEBP, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        }



    }
}
