package com.me.harris.extractframe.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.media.Image
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.os.CancellationSignal
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.me.harris.awesomelib.utils.LogUtil
import com.me.harris.extractframe.ImageUtil
import com.me.harris.extractframe.NV21ToBitmap
import com.me.harris.extractframe.VideoDecoder
import com.me.harris.extractframe.yuvrelated.MediaCodecFrameExtractor
import com.me.harris.extractframe.yuvrelated.getSyncFrameTimestamps
import com.me.harris.libyuv.ImageToBitmap
import kotlinx.coroutines.*
import okhttp3.internal.wait
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import kotlin.coroutines.resume

//** ref : https://github.com/deepsadness/MediaMetadataRetrieverWrapper , 确实比MediaMetaDataRetriever快不少
// result
// 1 .mediaCodec + mediaCode.getImage + RenderScript ，每隔5s抽关键帧， 2s左右搞定
// 2 .mediaCodec + mediaCode.getImage + libYuv ，每隔5s抽关键帧， 4s左右搞定
// 3 .mediaCodec + mediaCode.getImage + JavaYuvImage ，每隔5s抽关键帧， 5s左右搞定
// https://blog.minhazav.dev/how-to-convert-yuv-420-sp-android.media.Image-to-Bitmap-or-jpeg/


class ExtractFrameViewModel(application: Application) :AndroidViewModel(application){

    companion object CONST {
        const val TAG = "ExtractFrameViewModel"
    }


    val SAVE_EXTRACT_FRAME_DIR_PATH = "${application.filesDir}${File.separator}photos"
    val SAVE_EXTRACT_FRAME_DIR_PATH2 = "${application.filesDir}${File.separator}photos2"

    suspend fun extractFrameAtTimeUs(filePath:String,timeUs:Long):Bitmap{
        return suspendCancellableCoroutine { con ->
            val ext = MediaCodecFrameExtractor()
            ext.setDataSource(filePath)
            ext.getFrameAtTime(timeUs = timeUs, scale = 1) {
                con.resume(it)
            }
            con.invokeOnCancellation {
                ext.release()
            }
        }
    }


    private val saveBmpToDirCallback = { bitmap:Bitmap,index:Int ->
        val saveDir = SAVE_EXTRACT_FRAME_DIR_PATH2
        if (!File(saveDir).exists()){
            File(saveDir).mkdirs()
        }
        savePicFile(bitmap, "${saveDir}${File.separator}"+ "test_pic_" + index + ".jpg")
    }

    suspend fun extractFrameInterval(filePath:String, intervalMs:Long, scale:Int, cancellationSignal: CancellationSignal,
                                     callback:((bmp:Bitmap,index:Int) -> Unit) = saveBmpToDirCallback ){
        val ext = MediaCodecFrameExtractor()
        ext.setDataSource(filePath)
        var index = 0
        ext.getFramesInterval(intervalMs = intervalMs,scale = scale,cancellationSignal = cancellationSignal) {
            callback(it,++index)
        }
        Log.w(TAG,"release called")
    }

    @Throws(IOException::class)
    suspend fun getKeyFrames(inputPath: String?,saveDir:String): Boolean {
        val mRetriever = MediaMetadataRetriever()
        mRetriever.setDataSource(inputPath)
        val mediaExtractor = MediaExtractor()
        LogUtil.d("getKeyFrames keyFrameCount = setDataSource ${inputPath}" )
        mediaExtractor.setDataSource(inputPath!!)
        var sourceVideoTrack = -1
        for (index in 0 until mediaExtractor.trackCount) {
            val format = mediaExtractor.getTrackFormat(index)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime!!.startsWith("video/")) {
                format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 65536)
                sourceVideoTrack = index
                break
            }
        }
        if (sourceVideoTrack == -1) return false
        mediaExtractor.selectTrack(sourceVideoTrack)
        val buffer: ByteBuffer = ByteBuffer.allocate(20500 * 1024)
        val frameTimeList: MutableList<Long> = ArrayList()
        var sampleSize = 0
        kotlin.runCatching {
            // todo readSampleData throws java.lang.IllegalArgumentException sometimes
            while (mediaExtractor.readSampleData(buffer, 0).also { sampleSize = it } > 0) {
                buffer.clear() // Note:As of API 21, on success the position and limit of byteBuf is updated to point to the data just read.
                val flags = mediaExtractor.sampleFlags
                if (flags > 0 && flags and MediaExtractor.SAMPLE_FLAG_SYNC != 0) {
                    frameTimeList.add(mediaExtractor.sampleTime)
                }
                mediaExtractor.advance()
            }
        }.onFailure {
            LogUtil.w("ExtractFrameAndSaveKeyFrameToFileActivity", it.stackTraceToString())
        }
        LogUtil.d("getKeyFrames keyFrameCount = " + frameTimeList.size)
//        val parentPath: String = File(inputPath).getParent() + File.separator
        LogUtil.d("getKeyFrames parent Path=$saveDir")
        if (!File(saveDir).exists()){
            File(saveDir).mkdirs()
        }
        for (index in frameTimeList.indices ) {
            if (currentCoroutineContext().isActive){
                val bitmap = mRetriever.getFrameAtTime(
                    frameTimeList[index],
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                )
//            MediaFormat.KEY_MAX_B_FRAMES
                savePicFile(bitmap, "${saveDir}${File.separator}"+ "test_pic_" + index + ".jpg")
            }else {
                LogUtil.w("getKeyFrames parent Path=$saveDir   abort!!!!!! ${index}")
            }
        }
        return true
    }

    @Throws(IOException::class)
    private fun savePicFile(bitmap: Bitmap?, savePath: String) {
        if (bitmap == null) {
            LogUtil.d("savePicFile failed, bitmap is null.")
            return
        }
        LogUtil.d("savePicFile step 1, bitmap is not null. size = ${bitmap.allocationByteCount/(1024*1024)}MB")
        val file = File(savePath)
        if (!file.exists()) {
            file.createNewFile()
        }
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
    }

    suspend fun getKeyFramesViaMediaCodec(inputPath: String?,saveDir:String): Boolean {
        if (!File(saveDir).exists()){
            File(saveDir).mkdirs()
        }
            VideoDecoder().decode(inputPath,object :VideoDecoder.DecodeCallback{
                var count = 0
                val renderScriptConverter = NV21ToBitmap(getApplication())
                var yuvToBmpTotal = 0L
                override fun onDecode(
                    yuv: ByteArray?,
                    width: Int,
                    height: Int,
                    formatCount: Int,
                    presentationTimeUs: Long,
                    format: Int
                ) {
                    val now = System.currentTimeMillis()
                    val bmp =  renderScriptConverter.nv21ToBitmap(yuv,width, height) // 7ms左右
//                    val bmp = ImageUtil.yuvDataToBitMap(yuv,width,height,width,height) // 50ms左右
                    Log.w("=A=","transforming yuv ${yuv?.size} to bmp at presentationTimeUs ${presentationTimeUs/1000_000} cost me ${System.currentTimeMillis()-now} ms")
                    savePicFile(bmp,"${saveDir}${File.separator}image_${count++}.jpg")
                    // transforming yuv 3110400 to bmp using renderScript at presentationTimeUs 210 cost me 6 ms
                    // the ideal solution
                }

                override fun onDecode2(
                    image: Image,
                    width: Int,
                    height: Int,
                    formatCount: Int,
                    presentationTimeUs: Long,
                    format: Int
                ) {
                    super.onDecode2(image, width, height, formatCount, presentationTimeUs, format)
                    val now = System.currentTimeMillis()
                    val bmp = ImageToBitmap.getBitmapFromImageUsingLibYUV(image)
                    val cost = System.currentTimeMillis() - now
                    Log.w("=A=","transforming Image to bmp at presentationTimeUs ${presentationTimeUs/1000_000} cost me ${System.currentTimeMillis()-now} ms yuvToBmpTotal toal = $yuvToBmpTotal")
                    yuvToBmpTotal+=cost
                    savePicFile(bmp,"${saveDir}${File.separator}image_${count++}.jpg")
                }

                override fun onFinish() {
                }

                override fun onStop() {

                }
            })
        return false
    }

    fun getAllSyncFrameTimeStamp(path:String):List<Long>{
        val source = MediaExtractor()
        source.setDataSource(path)
        source.seekTo(1_000_000, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
        val resoult = getSyncFrameTimestamps(source)
        return resoult
    }


}
