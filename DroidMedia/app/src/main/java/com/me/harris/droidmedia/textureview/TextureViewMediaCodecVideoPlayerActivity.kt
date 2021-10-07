package com.me.harris.droidmedia.textureview

import android.graphics.SurfaceTexture
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.droidmedia.R
import com.me.harris.droidmedia.utils.VideoUtil
import com.me.harris.droidmedia.video.VideoPlayView
import kotlin.concurrent.thread


// https://blog.csdn.net/King1425/article/details/81263327
class TextureViewMediaCodecVideoPlayerActivity:AppCompatActivity(),
    TextureView.SurfaceTextureListener {

    lateinit var mTextureView:TextureView
    lateinit var mButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_textureview_mediacodec)
        mButton = findViewById(R.id.button)
        mTextureView = findViewById(R.id.textureView)

        mButton.setOnClickListener {
            startPlay()
        }
        mTextureView.surfaceTextureListener = this
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