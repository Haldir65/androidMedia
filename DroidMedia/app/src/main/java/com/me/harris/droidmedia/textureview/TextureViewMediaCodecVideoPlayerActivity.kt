package com.me.harris.droidmedia.textureview

import android.graphics.SurfaceTexture
import android.media.MediaExtractor
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.droidmedia.R
import com.me.harris.droidmedia.utils.VideoUtil
import kotlin.concurrent.thread


// https://blog.csdn.net/King1425/article/details/81263327
class TextureViewMediaCodecVideoPlayerActivity:AppCompatActivity(),
    TextureView.SurfaceTextureListener {

    lateinit var mTextureView:TextureView
    lateinit var mButton: Button
    lateinit var mButtonForward: Button
    lateinit var mButtonBackward: Button

    companion object {
        const val TIME_1_MINUTES =  1*60*1000*1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_textureview_mediacodec)
        mButton = findViewById(R.id.button)
        mTextureView = findViewById(R.id.textureView)
        mButtonForward = findViewById(R.id.button_forward)
        mButtonBackward = findViewById(R.id.button_backward)
        mButton.setOnClickListener {
            startPlay()
        }
        mButtonForward.setOnClickListener {
            navigateForward()
        }
        mButtonBackward.setOnClickListener {
            navigateBackward()
        }
        mTextureView.surfaceTextureListener = this
    }

    private fun navigateBackward() {
        mVideoDecoder?.extractor()?.let {
            val cur = it.sampleTime
            mVideoDecoder!!.timBase-=TIME_1_MINUTES/1000
            it.seekTo(cur- TIME_1_MINUTES,MediaExtractor.SEEK_TO_NEXT_SYNC)
        }
        mAudioDecoder?.extractor()?.let {
            val cur = it.sampleTime
            mAudioDecoder!!.timBase-=TIME_1_MINUTES/1000
            it.seekTo(cur- TIME_1_MINUTES,MediaExtractor.SEEK_TO_NEXT_SYNC)
        }
    }
        private fun navigateForward() {
        mVideoDecoder?.extractor()?.let {
            val cur = it.sampleTime
            mVideoDecoder!!.timBase+=TIME_1_MINUTES/1000
            it.seekTo(cur+ TIME_1_MINUTES,MediaExtractor.SEEK_TO_NEXT_SYNC)
        }
        mAudioDecoder?.extractor()?.let {
            val cur = it.sampleTime
            mAudioDecoder!!.timBase+=TIME_1_MINUTES/1000
            it.seekTo(cur+ TIME_1_MINUTES,MediaExtractor.SEEK_TO_NEXT_SYNC)
        }
    }

    private fun startPlay(){
        VideoUtil.setUrl()
        val url = VideoUtil.strVideo
        thread {
            mVideoDecoder = VideoDecoder(mSurface!!)
            mVideoDecoder?.start(url)
        }

        thread {
            mAudioDecoder = AudioDecoder()
            mAudioDecoder?.start(url)
        }
    }


    private var mVideoDecoder:VideoDecoder? = null
    private var mAudioDecoder:AudioDecoder? = null


    private var mSurface:Surface? = null

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        mSurface = Surface(surface)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {

    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
    }

    override fun onDestroy() {
        super.onDestroy()
        mVideoDecoder?.stop()
        mAudioDecoder?.stop()
    }


}