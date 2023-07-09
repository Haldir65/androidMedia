package com.me.harris.extractframe

import android.graphics.Rect
import android.os.Bundle
import android.os.CancellationSignal
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
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.extractframe.databinding.ActivityExtractFrameToFileBinding
import com.me.harris.extractframe.databinding.ItemExtractingFrameNailBinding
import com.me.harris.extractframe.viewmodel.ExtractFrameViewModel
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
 * 2023-07-08 18:29:51.417 11920-11920 crash_dump64            pid-11920                            I  obtaining output fd from tombstoned, type: kDebuggerdTombstone
2023-07-08 18:29:51.428 11920-11920 DEBUG                   pid-11920                            A  *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***
2023-07-08 18:29:51.428 11920-11920 DEBUG                   pid-11920                            A  Build fingerprint: 'Xiaomi/equuleus/equuleus:10/QKQ1.190828.002/V12.5.2.0.QECCNXM:user/release-keys'
2023-07-08 18:29:51.429 11920-11920 DEBUG                   pid-11920                            A  Revision: '0'
2023-07-08 18:29:51.429 11920-11920 DEBUG                   pid-11920                            A  ABI: 'arm64'
2023-07-08 18:29:51.429 11920-11920 DEBUG                   pid-11920                            A  Timestamp: 2023-07-08 18:29:51+0800
2023-07-08 18:29:51.429 11920-11920 DEBUG                   pid-11920                            A  pid: 11743, tid: 11761, name: HeapTaskDaemon  >>> com.me.harris.droidmedia <<<
2023-07-08 18:29:51.429 11920-11920 DEBUG                   pid-11920                            A  uid: 10248
2023-07-08 18:29:51.429 11920-11920 DEBUG                   pid-11920                            A  signal 11 (SIGSEGV), code 1 (SEGV_MAPERR), fault addr 0x44
2023-07-08 18:29:51.429 11920-11920 DEBUG                   pid-11920                            A  Cause: null pointer dereference
2023-07-08 18:29:51.429 11920-11920 DEBUG                   pid-11920                            A      x0  000000737a0b8200  x1  000000007a9e2000  x2  000000736e214a90  x3  0000000000000000
2023-07-08 18:29:51.429 11920-11920 DEBUG                   pid-11920                            A      x4  000000000000002b  x5  0000000000000000  x6  000000737a0c3540  x7  000000737a0c3540
2023-07-08 18:29:51.429 11920-11920 DEBUG                   pid-11920                            A      x8  0000000000000000  x9  0000000012c00000  x10 0000000032c00000  x11 0000000400000000
2023-07-08 18:29:51.429 11920-11920 DEBUG                   pid-11920                            A      x12 0000000400000000  x13 0000007312b5b040  x14 000000736e214958  x15 00000072f3985040
2023-07-08 18:29:51.429 11920-11920 DEBUG                   pid-11920                            A      x16 00000073fddee768  x17 00000073fdde28f0  x18 000000731a188000  x19 000000007a9e2000
2023-07-08 18:29:51.429 11920-11920 DEBUG                   pid-11920                            A      x20 000000737a0b8200  x21 0000000000000000  x22 000000737a0b84f0  x23 0000000000000000
2023-07-08 18:29:51.429 11920-11920 DEBUG                   pid-11920                            A      x24 000000737a0c3540  x25 000000736e216020  x26 ffffffffffffffff  x27 000000736e216020
2023-07-08 18:29:51.429 11920-11920 DEBUG                   pid-11920                            A      x28 000000737a0b8520  x29 000000736e214b00
2023-07-08 18:29:51.429 11920-11920 DEBUG                   pid-11920                            A      sp  000000736e214a60  lr  00000073790b2cc8  pc  00000073790b1878
2023-07-08 18:29:51.658 11920-11920 DEBUG                   pid-11920                            A
backtrace:
2023-07-08 18:29:51.658 11920-11920 DEBUG                   pid-11920                            A        #00 pc 0000000000212878  /apex/com.android.runtime/lib64/libart.so (art::gc::collector::ConcurrentCopying::AddLiveBytesAndScanRef(art::mirror::Object*)+356) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.658 11920-11920 DEBUG                   pid-11920                            A        #01 pc 0000000000213cc4  /apex/com.android.runtime/lib64/libart.so (art::gc::collector::ConcurrentCopying::ProcessMarkStackForMarkingAndComputeLiveBytes()+464) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.658 11920-11920 DEBUG                   pid-11920                            A        #02 pc 000000000020d870  /apex/com.android.runtime/lib64/libart.so (art::gc::collector::ConcurrentCopying::MarkingPhase()+1136) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.658 11920-11920 DEBUG                   pid-11920                            A        #03 pc 000000000020ca04  /apex/com.android.runtime/lib64/libart.so (art::gc::collector::ConcurrentCopying::RunPhases()+264) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.658 11920-11920 DEBUG                   pid-11920                            A        #04 pc 000000000022b3f4  /apex/com.android.runtime/lib64/libart.so (art::gc::collector::GarbageCollector::Run(art::gc::GcCause, bool)+288) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.658 11920-11920 DEBUG                   pid-11920                            A        #05 pc 000000000024aa8c  /apex/com.android.runtime/lib64/libart.so (art::gc::Heap::CollectGarbageInternal(art::gc::collector::GcType, art::gc::GcCause, bool)+3200) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.658 11920-11920 DEBUG                   pid-11920                            A        #06 pc 000000000025d13c  /apex/com.android.runtime/lib64/libart.so (art::gc::Heap::ConcurrentGC(art::Thread*, art::gc::GcCause, bool)+124) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.658 11920-11920 DEBUG                   pid-11920                            A        #07 pc 0000000000262d0c  /apex/com.android.runtime/lib64/libart.so (art::gc::Heap::ConcurrentGCTask::Run(art::Thread*)+36) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.658 11920-11920 DEBUG                   pid-11920                            A        #08 pc 0000000000290a0c  /apex/com.android.runtime/lib64/libart.so (art::gc::TaskProcessor::RunAllTasks(art::Thread*)+64) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.658 11920-11920 DEBUG                   pid-11920                            A        #09 pc 000000000033aa3c  /system/framework/arm64/boot-core-libart.oat (art_jni_trampoline+124) (BuildId: 9efb9d3c44c620bfb17dab760d258fbc29de86cd)
2023-07-08 18:29:51.658 11920-11920 DEBUG                   pid-11920                            A        #10 pc 0000000000137334  /apex/com.android.runtime/lib64/libart.so (art_quick_invoke_stub+548) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.658 11920-11920 DEBUG                   pid-11920                            A        #11 pc 0000000000145fec  /apex/com.android.runtime/lib64/libart.so (art::ArtMethod::Invoke(art::Thread*, unsigned int*, unsigned int, art::JValue*, char const*)+244) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.658 11920-11920 DEBUG                   pid-11920                            A        #12 pc 00000000002e37d0  /apex/com.android.runtime/lib64/libart.so (art::interpreter::ArtInterpreterToCompiledCodeBridge(art::Thread*, art::ArtMethod*, art::ShadowFrame*, unsigned short, art::JValue*)+384) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.658 11920-11920 DEBUG                   pid-11920                            A        #13 pc 00000000002dea30  /apex/com.android.runtime/lib64/libart.so (bool art::interpreter::DoCall<false, false>(art::ArtMethod*, art::Thread*, art::ShadowFrame&, art::Instruction const*, unsigned short, art::JValue*)+892) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.658 11920-11920 DEBUG                   pid-11920                            A        #14 pc 00000000005a0fa4  /apex/com.android.runtime/lib64/libart.so (MterpInvokeVirtual+648) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.658 11920-11920 DEBUG                   pid-11920                            A        #15 pc 0000000000131814  /apex/com.android.runtime/lib64/libart.so (mterp_op_invoke_virtual+20) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.659 11920-11920 DEBUG                   pid-11920                            A        #16 pc 00000000001b435a  /apex/com.android.runtime/javalib/core-libart.jar (java.lang.Daemons$HeapTaskDaemon.runInternal+38)
2023-07-08 18:29:51.659 11920-11920 DEBUG                   pid-11920                            A        #17 pc 00000000005a1264  /apex/com.android.runtime/lib64/libart.so (MterpInvokeVirtual+1352) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.659 11920-11920 DEBUG                   pid-11920                            A        #18 pc 0000000000131814  /apex/com.android.runtime/lib64/libart.so (mterp_op_invoke_virtual+20) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.659 11920-11920 DEBUG                   pid-11920                            A        #19 pc 00000000001b3b3e  /apex/com.android.runtime/javalib/core-libart.jar (java.lang.Daemons$Daemon.run+50)
2023-07-08 18:29:51.659 11920-11920 DEBUG                   pid-11920                            A        #20 pc 00000000005a2a84  /apex/com.android.runtime/lib64/libart.so (MterpInvokeInterface+1788) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.659 11920-11920 DEBUG                   pid-11920                            A        #21 pc 0000000000131a14  /apex/com.android.runtime/lib64/libart.so (mterp_op_invoke_interface+20) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.659 11920-11920 DEBUG                   pid-11920                            A        #22 pc 00000000000ea988  /apex/com.android.runtime/javalib/core-oj.jar (java.lang.Thread.run+8)
2023-07-08 18:29:51.659 11920-11920 DEBUG                   pid-11920                            A        #23 pc 00000000002b4ae4  /apex/com.android.runtime/lib64/libart.so (_ZN3art11interpreterL7ExecuteEPNS_6ThreadERKNS_20CodeItemDataAccessorERNS_11ShadowFrameENS_6JValueEbb.llvm.17460956533834400288+240) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.659 11920-11920 DEBUG                   pid-11920                            A        #24 pc 000000000059250c  /apex/com.android.runtime/lib64/libart.so (artQuickToInterpreterBridge+1032) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.659 11920-11920 DEBUG                   pid-11920                            A        #25 pc 0000000000140468  /apex/com.android.runtime/lib64/libart.so (art_quick_to_interpreter_bridge+88) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.659 11920-11920 DEBUG                   pid-11920                            A        #26 pc 0000000000137334  /apex/com.android.runtime/lib64/libart.so (art_quick_invoke_stub+548) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.659 11920-11920 DEBUG                   pid-11920                            A        #27 pc 0000000000145fec  /apex/com.android.runtime/lib64/libart.so (art::ArtMethod::Invoke(art::Thread*, unsigned int*, unsigned int, art::JValue*, char const*)+244) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.659 11920-11920 DEBUG                   pid-11920                            A        #28 pc 00000000004b0f10  /apex/com.android.runtime/lib64/libart.so (art::(anonymous namespace)::InvokeWithArgArray(art::ScopedObjectAccessAlreadyRunnable const&, art::ArtMethod*, art::(anonymous namespace)::ArgArray*, art::JValue*, char const*)+104) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.659 11920-11920 DEBUG                   pid-11920                            A        #29 pc 00000000004b2024  /apex/com.android.runtime/lib64/libart.so (art::InvokeVirtualOrInterfaceWithJValues(art::ScopedObjectAccessAlreadyRunnable const&, _jobject*, _jmethodID*, jvalue const*)+416) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.659 11920-11920 DEBUG                   pid-11920                            A        #30 pc 00000000004f29e0  /apex/com.android.runtime/lib64/libart.so (art::Thread::CreateCallback(void*)+1176) (BuildId: 03574df7f7eb01baf6baa6348024974c)
2023-07-08 18:29:51.659 11920-11920 DEBUG                   pid-11920                            A        #31 pc 00000000000d7110  /apex/com.android.runtime/lib64/bionic/libc.so (__pthread_start(void*)+36) (BuildId: f6cc5d2d702265511937b56460b37693)
2023-07-08 18:29:51.659 11920-11920 DEBUG                   pid-11920                            A        #32 pc 0000000000075314  /apex/com.android.runtime/lib64/bionic/libc.so (__start_thread+64) (BuildId: f6cc5d2d702265511937b56460b37693)
---------------------------- PROCESS ENDED (11743) for package com.me.harris.droidmedia ----------------------------
 *
 *
 */
class ExtractFrameAndSaveKeyFrameToFileActivity:AppCompatActivity(R.layout.activity_extract_frame_to_file) {


    private val binding by viewBinding(ActivityExtractFrameToFileBinding::bind)
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
            val resultDir = viewModel.SAVE_EXTRACT_FRAME_DIR_PATH2

            val refreshUi = suspend {
                withContext(Dispatchers.Main){
                    val allFiles = File(resultDir).listFiles { f -> f.absolutePath.endsWith("jpg") }?.map { f -> f.absolutePath }.orEmpty()
                    Log.w("=A=","allfiles ${allFiles.size}")
                    showAllExtractedFrames(allFiles)
                }
            }
            withContext(Dispatchers.IO){
                File(resultDir).deleteRecursively()
            }
//            refreshUi()
            val fPath = VideoUtil.strVideo
            withContext(Dispatchers.IO){
                val time = measureTimedValue {
//                    viewModel.getKeyFrames(fPath,viewModel.SAVE_EXTRACT_FRAME_DIR_PATH) //1.  结果正确， 低端，MediaMetaDataRetriever
//                    viewModel.getKeyFramesViaMediaCodec(fPath,viewModel.SAVE_EXTRACT_FRAME_DIR_PATH2) // 2. 结果正确，速度慢，用的YuvImage
                    viewModel.extractFrameInterval(filePath = fPath, intervalMs = 1000, scale = 4, cancellationSignal = singal) // 搞定
                    Log.e("=A=","we have bitmap now")
                    val bmp = viewModel.extractFrameAtTimeUs(fPath,60_000_000)
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

            val keyFrameTimeLists = measureTimedValue {
                viewModel.getAllSyncFrameTimeStamp(fPath)
            }
            val cost = keyFrameTimeLists.duration.inWholeMilliseconds
            val frames = keyFrameTimeLists.value.joinToString(prefix = "[", postfix = "]", separator = System.lineSeparator(), transform = { a -> "${(a/1000_000)}秒"})
            Log.e("=A=","scan video ${fPath} frame count cost me $cost ms \n frames = $frames")

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

    val singal = CancellationSignal()

    override fun onDestroy() {
        super.onDestroy()
        singal.cancel()
    }


}


private class VideoFrameDisplayViewHolder(val binding: ItemExtractingFrameNailBinding):RecyclerView.ViewHolder(binding.root) {
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