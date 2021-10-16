package com.me.harris.droidmedia.decode

import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jadyn.mediakit.video.decode.VideoDecoder
import com.me.harris.droidmedia.R
import com.me.harris.droidmedia.utils.VideoUtil
import com.me.harris.droidmedia.video.VideoPlayView
import kotlinx.android.synthetic.main.activity_decode.*
import java.io.File

class DecodeActivity : AppCompatActivity() {
//    private val decodeMP4Path = TextUtils.concat(Environment.getExternalStorageDirectory().path,
//        "/yazi.mp4").toString()



    private val decodeMP4Path = VideoUtil.strVideo


    private var videoDecoder: VideoDecoder? = null

    private var filePath = ""

    private var outputPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decode)

        // 2019/2/14-16:25 设置一个默认的测试视频地址
        file_et.setText(decodeMP4Path)
        resetOutputEt()

        sure_file_tv.setOnClickListener {
            checkFile()
        }

        sure_output_tv.setOnClickListener {
            checkOutputPath()
        }


        switch_iv.setOnClickListener {
            if (videoDecoder != null) {
                toast("正在解码中")
                return@setOnClickListener
            }

            switch_iv.isSelected = !switch_iv.isSelected
            switch_iv.setImageResource(if (switch_iv.isSelected) R.drawable.p else R.drawable.c)
        }

        start_tv.setOnClickListener {
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
                    val s = if (switch_iv.isSelected) "OpenGL渲染" else "YUV存储"
                    output_loading_tv.text = TextUtils.concat(s, "解码中，第${it}张")
                }
            })
        }
    }

    private fun toast(s: String) {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show()
    }

    private fun checkOutputPath() {
        val f = output_et.text.toString()
        if (f.isBlank()) {
            toast("不能为空")
            return
        }
        val file = File(f)
        if (!file.exists()) {
            file.mkdirs()
        }
        if (!file.isDirectory) {
            toast("必须为文件夹")
            resetOutputEt()
            return
        }
        outputPath = f
    }

    private fun checkFile() {
        val f = file_et.text.toString()
        if (f.isBlank()) {
            toast("不能为空")
            return
        }
        val file = File(f)
        if (!file.exists() || !file.isFile) {
            toast("文件错误")
            file_et.setText("")
            return
        }
        filePath = f
    }

    private fun resetOutputEt() {
        val path = TextUtils.concat(Environment.getExternalStorageDirectory().path,File.separator, Environment.DIRECTORY_MOVIES,File.separator,"Droidmedia").toString()
        if (!File(path).exists()){
            File(path).mkdirs()
        }
        output_et.setText(path)
        output_et.setSelection(output_et.text.toString().length)
    }

    override fun onDestroy() {
        super.onDestroy()
        videoDecoder?.release()
    }

}