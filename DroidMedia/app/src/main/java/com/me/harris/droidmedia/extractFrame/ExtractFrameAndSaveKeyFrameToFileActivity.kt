package com.me.harris.droidmedia.extractFrame

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.me.harris.droidmedia.utils.LogUtil
import com.me.harris.droidmedia.utils.VideoUtil
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import kotlin.concurrent.thread


class ExtractFrameAndSaveKeyFrameToFileActivity:AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkSelfPermission(Manifest.permission_group.STORAGE)!=PackageManager.PERMISSION_GRANTED){

        }
        thread {
            val fPath = VideoUtil.strVideo
            getKeyFrames(fPath)
            Log.e("=A=","all Completed!")
        }


    }

    @Throws(IOException::class)
    fun getKeyFrames(inputPath: String?): Boolean {
        val mRetriever = MediaMetadataRetriever()
        mRetriever.setDataSource(inputPath)
        val mediaExtractor = MediaExtractor()
        mediaExtractor.setDataSource(inputPath!!)
        var sourceVideoTrack = -1
        for (index in 0 until mediaExtractor.trackCount) {
            val format = mediaExtractor.getTrackFormat(index)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime!!.startsWith("video/")) {
                sourceVideoTrack = index
                break
            }
        }
        if (sourceVideoTrack == -1) return false
        mediaExtractor.selectTrack(sourceVideoTrack)
        val buffer: ByteBuffer = ByteBuffer.allocate(500 * 1024)
        val frameTimeList: MutableList<Long> = ArrayList()
        var sampleSize = 0
        while (mediaExtractor.readSampleData(buffer, 0).also { sampleSize = it } > 0) {
            val flags = mediaExtractor.sampleFlags
            if (flags > 0 && flags and MediaExtractor.SAMPLE_FLAG_SYNC != 0) {
                frameTimeList.add(mediaExtractor.sampleTime)
            }
            mediaExtractor.advance()
        }
        LogUtil.d("getKeyFrames keyFrameCount = " + frameTimeList.size)
        val parentPath: String = File(inputPath).getParent() + File.separator
        LogUtil.d("getKeyFrames parent Path=$parentPath")
        for (index in frameTimeList.indices) {
            val bitmap = mRetriever.getFrameAtTime(
                frameTimeList[index],
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC
            )
            savePicFile(bitmap, parentPath + "test_pic_" + index + ".jpg")
        }
        return true
    }

    @Throws(IOException::class)
    private fun savePicFile(bitmap: Bitmap?, savePath: String) {
        if (bitmap == null) {
            LogUtil.d("savePicFile failed, bitmap is null.")
            return
        }
        LogUtil.d("savePicFile step 1, bitmap is not null.")
        val file = File(savePath)
        if (!file.exists()) {
            file.createNewFile()
        }
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
    }


}