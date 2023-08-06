package com.me.harris.extractframe

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.awesomelib.viewBinding
import com.me.harris.extractframe.databinding.ActivityExtractFrameParallelBinding
import com.me.harris.extractframe.databinding.ActivityExtractFrameToFileBinding
import com.me.harris.extractframe.parallel.PARALLISM
import com.me.harris.extractframe.parallel.distributingTasksToIndividualMouse
import com.me.harris.extractframe.parallel.getVideoDurationInMicroSeconds
import com.me.harris.extractframe.ui.VideoFrameDisplayAdapter
import kotlinx.collections.immutable.immutableListOf
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

// tldr
// 1. 多个MediametaDataRetriver多线程并发，并发度1 -> 2 从7s -> 5s 有一定提升
// 2. >=2 以后几乎无影响，甚至试验过32个  （修改PARALLISM） ，均维持在5s-6s之间
// 3. bitmap -> compressToJpeg 10ms以内，getFrameAtTime在400ms上下，
class ExtractFrameUsingParallelExtractorActivity :AppCompatActivity(R.layout.activity_extract_frame_parallel){

    private val binding by viewBinding(ActivityExtractFrameParallelBinding::bind)

    private val adapter = VideoFrameDisplayAdapter()


    val saveDir by lazy {
        "${application.filesDir}${File.separator}photos3"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false)
        binding.recyclerview.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                val position = parent.getChildAdapterPosition(view)
                val isLastOne = position ==parent.adapter!!.itemCount-1
                outRect.set(10,10,10,if (isLastOne)260 else 10)
            }
        })

        loadingFramesInParallel()

    }

    private fun showAllExtractedFrames(allFiles: List<String>) {
        adapter.list.clear()
        adapter.list.addAll(allFiles)
        adapter.notifyDataSetChanged()
    }

    private fun loadingFramesInParallel(){

        val refreshUi = suspend {
            withContext(Dispatchers.Main){
                val allFiles = File(saveDir).listFiles { f -> f.absolutePath.endsWith("jpg") }?.map { f -> f.absolutePath }.orEmpty()
                Log.w("=A=","file that ends with .jpg counts  ${allFiles.size} in folder ${saveDir}")
                showAllExtractedFrames(allFiles)
            }
        }

        lifecycleScope.launch {
            val filepath = requireNotNull(VideoUtil.strVideo)
            val duration = getVideoDurationInMicroSeconds(filepath)
            Log.w("=A=","video duration for %s is %d seconds".format(filepath,duration/1000_000))
            val evans = distributingTasksToIndividualMouse(filepath,saveDir)
            val start = System.currentTimeMillis()
            evans.map { a -> async(Dispatchers.IO) {
              kotlin.runCatching { a.doExtractAndSaveToDir() }.getOrNull()
            } }.awaitAll()
            val end = System.currentTimeMillis()
            Log.w("=A=","with parallel limit at $PARALLISM , time cost is ${end - start}")
            refreshUi()

            // with parallel limit at 1 , time cost is 7962
            // with parallel limit at 1 , time cost is 8562

            //with parallel limit at 2 , time cost is 5124
            //with parallel limit at 2 , time cost is 4741
//            with parallel limit at 2 , time cost is 5111

            // with parallel limit at 3 , time cost is 4938

            //with parallel limit at 4 , time cost is 5204
            //with parallel limit at 4 , time cost is 5127

            // with parallel limit at 8 , time cost is 5153

            // with parallel limit at 16 , time cost is 5155
            // with parallel limit at 32 , time cost is 5490
        }
    }

    private fun testing(){

            val immutableList = persistentListOf("Apple", "Banana", "Cherry")

            // The following line would result in a compilation error, as the immutableList cannot be modified.
            immutableList.add("Durian")
    }


}