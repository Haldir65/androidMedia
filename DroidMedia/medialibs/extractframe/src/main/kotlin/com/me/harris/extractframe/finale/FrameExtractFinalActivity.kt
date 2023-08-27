package com.me.harris.extractframe.finale

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.awesomelib.viewBinding
import com.me.harris.extractframe.R
import com.me.harris.extractframe.contract.ExtractConfiguration
import com.me.harris.extractframe.databinding.FrameExtractFinalActivityBinding
import com.me.harris.extractframe.finale.creator.ExtractConfig
import com.me.harris.extractframe.finale.creator.ExtractStatement
import com.me.harris.extractframe.parallel.getVideoDurationInMicroSeconds
import com.me.harris.extractframe.ui.VideoFrameDisplayAdapter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File

class FrameExtractFinalActivity:AppCompatActivity(R.layout.frame_extract_final_activity)
{

    private val binding by viewBinding(FrameExtractFinalActivityBinding::bind)
    private val viewModel by viewModels<FrameExtractFinaleViewModel>()

    private val adapter = VideoFrameDisplayAdapter()



    val saveDir by lazy {
        "${application.filesDir}${File.separator}photos4"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.text.text = "当前并发工作unit个数 = ${ExtractConfiguration.EXTRACT_FRAME_PARALLEISM}"
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
        initObservers()
        start()
    }

    private fun start(){
        val filepath = VideoUtil.strVideo
        makeSureSaveDirExists()
        val config = ExtractConfig(filepath = filepath, storageDir = saveDir, statement = ExtractStatement(
            getVideoDurationInMicroSeconds(filepath = filepath)
        ))
        viewModel.startExtract(config)
    }

    private fun makeSureSaveDirExists(){
        val fileStorageDir = File(saveDir)
        fileStorageDir.deleteRecursively()
        fileStorageDir.mkdir()
    }



    private fun initObservers(){
        lifecycleScope.launch {
            viewModel.events.flowWithLifecycle(lifecycle)
                .onEach {
                    when(it){
                        is Event.ExtractDoneEvent -> {
                            refreshUi()
                            val displayText = """
                                当前并发工作unit个数 = ${ExtractConfiguration.EXTRACT_FRAME_PARALLEISM}
                                耗时 ${it.costMilliseconds}ms
                                使用mediaCodec +  ${if (it.config.useLibYuv) "Libyuv" else "RenderScript"}
                                一共抽出了 ${adapter.list.size}张图片
                            """.trimIndent()
                            binding.text.text = displayText
                        }
                        else  ->{

                        }
                    }
                }
                .launchIn(this)
        }
    }

    private fun refreshUi(){
        val allFiles = File(saveDir).listFiles { f -> f.absolutePath.endsWith("jpg") }?.map { f -> f.absolutePath }.orEmpty().sortedBy {
                a -> a.substringAfterLast("image_")
        }

        Log.w("=A=","file that ends with .jpg counts  ${allFiles.size} in folder ${saveDir}")
        adapter.list.clear()
        adapter.list.addAll(allFiles)
        adapter.notifyDataSetChanged()
    }




}
