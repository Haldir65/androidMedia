package com.me.harris.droidmedia.encode

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.SurfaceView
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.droidmedia.R
import com.me.harris.droidmedia.extractFrame.VideoDecoder
import com.me.harris.droidmedia.utils.ToastUtils
import com.me.harris.droidmedia.utils.VideoUtil
import java.io.File
import kotlin.concurrent.thread

// https://blog.csdn.net/afei__/article/details/114977673
class MediaCodecEncodeActivity : AppCompatActivity() {

    companion object {
        const val TAG = "VideoEncoder"
    }

    lateinit var mButtonStartEncode: Button
    lateinit var mTextSrcPath: TextView
    lateinit var mTextDstPath: TextView

     private var mVideoEncoder: VideoEncoder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encode_sample)
        mButtonStartEncode = findViewById(R.id.button_start)
        mTextSrcPath = findViewById(R.id.text_src)
        mTextDstPath = findViewById(R.id.text_dst)
        mButtonStartEncode.setOnClickListener {
            startEncode()
        }
    }

    private fun startEncode() {
        VideoUtil.setUrl()
        val src = VideoUtil.strVideo
        val outPutDir = File(Environment.getExternalStorageDirectory(),Environment.DIRECTORY_MOVIES+File.separator+"Encoder")
        if (!outPutDir.exists()){
            outPutDir.mkdirs()
        }
        val destFilePath = File(outPutDir,"${System.currentTimeMillis()}.mp4").absolutePath
        mTextSrcPath.text = "src文件路径${src}"
        mTextDstPath.text = "dst文件路径${destFilePath}"
        ToastUtils.showTextShort(this,"开始编码！")
        thread {
            val videoDecoder = VideoDecoder()
            videoDecoder.outputFormat = VideoDecoder.COLOR_FORMAT_NV12
            videoDecoder.decode(src, object : VideoDecoder.DecodeCallback {
                override fun onDecode(yuv: ByteArray?, width: Int, height: Int, formatCount: Int, presentationTimeUs: Long, format: Int
                ) {
                    Log.v(TAG,"formatCount : ${formatCount} presentationTimeUs : ${presentationTimeUs/1000_000}")
                    if (mVideoEncoder ==null ){
                        mVideoEncoder = VideoEncoder().also {
                            it.init(destFilePath,width, height)
                        }
                    }
                    mVideoEncoder?.encode(yuv,presentationTimeUs)
                }

                override fun onFinish() {
                    Log.i(TAG,"onFinish")
                    mVideoEncoder?.release()
                    this@MediaCodecEncodeActivity.runOnUiThread {
                        ToastUtils.showTextShort(this@MediaCodecEncodeActivity,"编码完成！")
                        mTextDstPath.text = "dst文件路径${destFilePath}已经生成"
                    }
                    mVideoEncoder = null
                }

                override fun onStop() {
                    Log.i(TAG,"onStop")
                    mVideoEncoder?.release()
                    this@MediaCodecEncodeActivity.runOnUiThread {
                        ToastUtils.showTextShort(this@MediaCodecEncodeActivity,"编码终止！")
                    }
                    mVideoEncoder = null
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mVideoEncoder?.release()
    }
}