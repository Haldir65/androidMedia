package com.me.harris.playerLibrary.textureview

import android.graphics.SurfaceTexture
import android.media.MediaExtractor
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.awesomelib.utils.VideoUtil.adjustPlayerViewPerVideoAspectRation
import com.me.harris.awesomelib.viewBinding
import com.me.harris.awesomelib.withSurfaceAvailable
import com.me.harris.playerLibrary.R
import com.me.harris.playerLibrary.databinding.ActivityTextureviewMediacodecBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


// https://blog.csdn.net/King1425/article/details/81263327
class TextureViewMediaCodecVideoPlayerActivity:AppCompatActivity(R.layout.activity_textureview_mediacodec)
{

    private val binding by viewBinding(ActivityTextureviewMediacodecBinding::bind)


    companion object {
        const val TIME_1_MINUTES =  1*60*1000*1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.button.setOnClickListener {

        }
        binding.buttonForward.setOnClickListener {
            navigateForward()
        }
        binding.button.setOnClickListener {
            navigateBackward()
        }
        binding.textureView.withSurfaceAvailable(::startPlay)
    }

    private fun navigateBackward() {
        mVideoDecoder?.extractor()?.let {
            val cur = it.sampleTime
            mVideoDecoder!!.timBase-= TIME_1_MINUTES /1000
            it.seekTo(cur- TIME_1_MINUTES,MediaExtractor.SEEK_TO_NEXT_SYNC)
        }
        mAudioDecoder?.extractor()?.let {
            val cur = it.sampleTime
            mAudioDecoder!!.timBase-= TIME_1_MINUTES /1000
            it.seekTo(cur- TIME_1_MINUTES,MediaExtractor.SEEK_TO_NEXT_SYNC)
        }
    }
    private fun navigateForward() {
        mVideoDecoder?.extractor()?.let {
            val cur = it.sampleTime
            mVideoDecoder!!.timBase+= TIME_1_MINUTES /1000
            it.seekTo(cur+ TIME_1_MINUTES,MediaExtractor.SEEK_TO_NEXT_SYNC)
        }
        mAudioDecoder?.extractor()?.let {
            val cur = it.sampleTime
            mAudioDecoder!!.timBase+= TIME_1_MINUTES /1000
            it.seekTo(cur+ TIME_1_MINUTES,MediaExtractor.SEEK_TO_NEXT_SYNC)
        }
    }

    private fun startPlay(surfaceTexture: SurfaceTexture){
        val url = VideoUtil.strVideo
        binding.textureView.adjustPlayerViewPerVideoAspectRation(url)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                mVideoDecoder = VideoDecoder(Surface(surfaceTexture))
                mVideoDecoder?.start(url)
            }
            withContext(Dispatchers.IO) {
                mAudioDecoder = AudioDecoder()
                mAudioDecoder?.start(url)
            }
        }
//
//
//        thread {
//            mVideoDecoder = VideoDecoder(mSurface!!)
//            mVideoDecoder?.start(url)
//        }
//
//        thread {
//            mAudioDecoder = AudioDecoder()
//            mAudioDecoder?.start(url)
//        }
    }



    private var mVideoDecoder: VideoDecoder? = null
    private var mAudioDecoder: AudioDecoder? = null



    override fun onDestroy() {
        super.onDestroy()
        mVideoDecoder?.stop()
        mAudioDecoder?.stop()
    }


}