package com.me.harris.droidmedia.extractFrame

import android.graphics.Bitmap
import android.graphics.Rect
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import coil.load
import com.me.harris.awesomelib.viewBinding
import com.me.harris.droidmedia.R
import com.me.harris.droidmedia.databinding.ActivityExtractFrameToFileBinding
import com.me.harris.awesomelib.utils.LogUtil
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.droidmedia.databinding.ItemExtractingFrameNailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer

/**
 * todo
 * 1， 使用com.me.harris.droidmedia.extractFrame.VideoDecoder抽帧
 * 2. 用两个mediaCodec抽抽看，计时
 * 3. 四个，8个呢？
 */
class ExtractFrameAndSaveKeyFrameToFileActivity:AppCompatActivity(R.layout.activity_extract_frame_to_file) {


    private val binding by viewBinding<ActivityExtractFrameToFileBinding>(ActivityExtractFrameToFileBinding::bind)
    private val adapter = VideoFrameDisplayAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        binding.recyclerview.addItemDecoration(object : ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.set(10,10,10,10)
            }
        })
        lifecycleScope.launch {
            val refreshUi = suspend {
                withContext(Dispatchers.Main){
                    val resultDir = "${filesDir.absolutePath}${File.separator}photos"
                    val allFiles = File(resultDir).listFiles { f -> f.absolutePath.endsWith("jpg") }?.map { f -> f.absolutePath }.orEmpty()
                    Log.w("=A=","allfiles ${allFiles.size}")
                    showAllExtractedFrames(allFiles)
                }
            }

            refreshUi()
            val fPath = VideoUtil.strVideo
            withContext(Dispatchers.IO){
                getKeyFrames(fPath,filesDir.absolutePath)
            }
            refreshUi()
            binding.text.text = "all frames done ,saved to ${filesDir.absolutePath}, you should be able to see result"
            Log.e("=A=","all Completed!")
        }


        if (false){
            lifecycleScope.launch {
                val savedPath = VideoTranscodingUtils.transcoding2Mp4(VideoUtil.strVideo,this@ExtractFrameAndSaveKeyFrameToFileActivity).orEmpty()
                withContext(Dispatchers.Main.immediate){
                    binding.text.text = "重新合成视频到$savedPath"
                }
            }
        }


    }

    private fun showAllExtractedFrames(allFiles: List<String>) {
        adapter.list.clear()
        adapter.list.addAll(allFiles)
        adapter.notifyDataSetChanged()
    }

    @Throws(IOException::class)
    fun getKeyFrames(inputPath: String?,saveDir:String?): Boolean {
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
        val buffer: ByteBuffer = ByteBuffer.allocate(500 * 1024)
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
        if (!File("${saveDir}${File.separator}photos").exists()){
            File("${saveDir}${File.separator}photos").mkdirs()
        }
        for (index in frameTimeList.indices) {
            val bitmap = mRetriever.getFrameAtTime(
                frameTimeList[index],
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC
            )
            savePicFile(bitmap, "${saveDir}${File.separator}photos${File.separator}"+ "test_pic_" + index + ".jpg")
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


    private fun videoTransCoding(){

    }

}


private class VideoFrameDisplayViewHolder(val binding:ItemExtractingFrameNailBinding):RecyclerView.ViewHolder(binding.root) {
    fun bindData(imageUrl:String,position:Int){
        binding.image.load(File(imageUrl))
        binding.desc.text = position.toString()
    }
}
private class VideoFrameDisplayAdapter(): RecyclerView.Adapter<VideoFrameDisplayViewHolder>() {

     val list = mutableListOf<String>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoFrameDisplayViewHolder {
        val binding = ItemExtractingFrameNailBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return VideoFrameDisplayViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: VideoFrameDisplayViewHolder, position: Int) {
        holder.bindData(list[position],position)
    }
}