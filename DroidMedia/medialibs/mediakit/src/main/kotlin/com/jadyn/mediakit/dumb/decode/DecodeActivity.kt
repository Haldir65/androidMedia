package com.jadyn.mediakit.dumb.decode

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jadyn.mediakit.R
import com.jadyn.mediakit.databinding.ActivityDecodeBinding
import com.jadyn.mediakit.video.decode.VideoDecoder
import com.me.harris.awesomelib.utils.VideoUtil
import java.io.File

internal class DecodeActivity : AppCompatActivity() {
//    private val decodeMP4Path = TextUtils.concat(Environment.getExternalStorageDirectory().path,
//        "/yazi.mp4").toString()



    private val decodeMP4Path = VideoUtil.strVideo


    private var videoDecoder: VideoDecoder? = null

    private var filePath = ""

    private var outputPath = ""

    lateinit var binding: ActivityDecodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDecodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 2019/2/14-16:25 设置一个默认的测试视频地址
        resetOutputEt()

        binding.sureFileTv.setOnClickListener {
            checkFile()
        }


        binding.sureOutputTv.setOnClickListener {
            checkOutputPath()
        }


        binding.switchIv.setOnClickListener {
            if (videoDecoder != null) {
                toast("正在解码中")
                return@setOnClickListener
            }

            binding.switchIv.isSelected = !binding.switchIv.isSelected
            binding.switchIv.setImageResource(if (binding.switchIv.isSelected) R.drawable.p else R.drawable.c)
        }

        binding.startTv.setOnClickListener {
            checkFile()
            checkOutputPath()
            if (videoDecoder != null) {
                toast("正在解码中")
                return@setOnClickListener
            }

            videoDecoder = VideoDecoder(filePath, outputPath)
            videoDecoder!!.start({
                videoDecoder = null
            }, {
                Log.d("cece", "decode failed : $it")
                videoDecoder = null
            }, {
                this@DecodeActivity.runOnUiThread {
                    val s = if (binding.switchIv.isSelected) "OpenGL渲染" else "YUV存储"
                    binding.outputLoadingTv.text = TextUtils.concat(s, "解码中，第${it}张")
                }
            })
        }
    }

    private fun toast(s: String) {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show()
    }

    private fun checkOutputPath() {
        outputPath = File(filesDir.absolutePath,"Decoding").also { if (!it.exists()) it.mkdirs() }.absolutePath
    }

    private fun checkFile() {
//        val f = binding.fileEt.text.toString()
//        if (f.isBlank()) {
//            toast("不能为空")
//            return
//        }
//        val file = File(f)
//        if (!file.exists() || !file.isFile) {
//            toast("文件错误")
//            binding.fileEt.setText("")
//            return
//        }
//        filePath = f
        filePath = VideoUtil.strVideo
    }

    private fun resetOutputEt() {
//        val path = TextUtils.concat(Environment.getExternalStorageDirectory().path,File.separator, Environment.DIRECTORY_MOVIES,File.separator,"Droidmedia").toString()
//        if (!File(path).exists()){
//            File(path).mkdirs()
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        videoDecoder?.release()
    }

}
