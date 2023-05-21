package com.me.harris.droidmedia.extractFrame

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import coil.load
import coil.transform.RoundedCornersTransformation
import com.me.harris.awesomelib.viewBinding
import com.me.harris.droidmedia.R
import com.me.harris.droidmedia.databinding.ActivityExtractFrameToFileBinding
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.droidmedia.databinding.ItemExtractingFrameNailBinding
import com.me.harris.droidmedia.extractFrame.viewmodel.ExtractFrameViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

/**
 * todo
 * 1， 使用com.me.harris.droidmedia.extractFrame.VideoDecoder抽帧  time cost is 65374 太慢
 * 2. 用两个mediaCodec抽抽看，计时
 * 3. 四个，8个呢？
 * 4. https://android.googlesource.com/platform/cts/+/8797a6e061b264906f36f0f5f5d71f518cd25949/tests/tests/media/src/android/media/cts
 */
class ExtractFrameAndSaveKeyFrameToFileActivity:AppCompatActivity(R.layout.activity_extract_frame_to_file) {


    private val binding by viewBinding<ActivityExtractFrameToFileBinding>(ActivityExtractFrameToFileBinding::bind)
    private val adapter = VideoFrameDisplayAdapter()
    private val viewModel by viewModels<ExtractFrameViewModel>()
    @OptIn(ExperimentalTime::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        binding.recyclerview.addItemDecoration(object : ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                val position = parent.getChildAdapterPosition(view)
                val isLastOne = position ==parent.adapter!!.itemCount-1
                outRect.set(10,10,10,if (isLastOne)260 else 10)
            }
        })
        lifecycleScope.launch {
            val refreshUi = suspend {
                withContext(Dispatchers.Main){
                    val resultDir = viewModel.SAVE_EXTRACT_FRAME_DIR_PATH2
                    val allFiles = File(resultDir).listFiles { f -> f.absolutePath.endsWith("jpg") }?.map { f -> f.absolutePath }.orEmpty()
                    Log.w("=A=","allfiles ${allFiles.size}")
                    showAllExtractedFrames(allFiles)
                }
            }

            refreshUi()
            val fPath = VideoUtil.strVideo
            withContext(Dispatchers.IO){
                val time = measureTimedValue {
//                    viewModel.getKeyFrames(fPath,viewModel.SAVE_EXTRACT_FRAME_DIR_PATH)
//                    viewModel.getKeyFramesViaMediaCodec(fPath,viewModel.SAVE_EXTRACT_FRAME_DIR_PATH2)
                    viewModel.extractFrameInterval(fPath,1000,4)
                    Log.e("=A=","we have bitmap now")
                    val bmp = viewModel.extractFrameAtTimeUs(fPath,6_000_000)
                    withContext(Dispatchers.Main.immediate){
                        binding.image.setImageBitmap(bmp)
                    }
                }
                Log.w("=A=","time cost is ${time.duration.inWholeMilliseconds}")
                // time cost is 9974, or 10797
                // time cost is 12208

                // time cost is 63755 mediaCodec
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




    private fun videoTransCoding(){

    }

}


private class VideoFrameDisplayViewHolder(val binding:ItemExtractingFrameNailBinding):RecyclerView.ViewHolder(binding.root) {
    fun bindData(imageUrl:String,position:Int){
        val ctx = binding.root.context.applicationContext
        binding.image.load(File(imageUrl)){
//            transformations(CircleCropTransformation())
            transformations(RoundedCornersTransformation(25f))
//            transformations(GrayscaleTransformation())
//            transformations(BlurTransformation(ctx))
//            transformations(BlurTransformation(ctx, 5f))
        }
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