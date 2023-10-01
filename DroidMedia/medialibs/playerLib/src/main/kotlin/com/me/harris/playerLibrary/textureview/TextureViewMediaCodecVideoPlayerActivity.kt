package com.me.harris.playerLibrary.textureview

import android.graphics.SurfaceTexture
import android.media.MediaExtractor
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import android.widget.Button
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.awesomelib.utils.VideoUtil.adjustPlayerViewPerVideoAspectRation
import com.me.harris.awesomelib.utils.VideoUtil.adjustTextureViewPerVideoAspectRation
import com.me.harris.awesomelib.viewBinding
import com.me.harris.awesomelib.whenProgressChanged
import com.me.harris.awesomelib.withSurfaceAvailable
import com.me.harris.playerLibrary.R
import com.me.harris.playerLibrary.databinding.ActivityTextureviewMediacodecBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit


// https://blog.csdn.net/King1425/article/details/81263327
class TextureViewMediaCodecVideoPlayerActivity:AppCompatActivity(R.layout.activity_textureview_mediacodec)
{

    private val binding by viewBinding(ActivityTextureviewMediacodecBinding::bind)
    private val viewModel by viewModels<TextureViewMediaCodecViewModel>()

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
        binding.seekbar.whenProgressChanged(::seekWhenStopTracking)

    }

    private fun navigateBackward() {
        mVideoDecoder?.backWard(TimeUnit.MILLISECONDS.toMillis(20 * 1000))
//        mVideoDecoder?.extractor()?.let {
//            val cur = it.sampleTime
//            mVideoDecoder!!.timBase-= TIME_1_MINUTES /1000
//            it.seekTo(cur- TIME_1_MINUTES,MediaExtractor.SEEK_TO_NEXT_SYNC)
//        }
//        mAudioDecoder?.extractor()?.let {
//            val cur = it.sampleTime
//            mAudioDecoder!!.timBase-= TIME_1_MINUTES /1000
//            it.seekTo(cur- TIME_1_MINUTES,MediaExtractor.SEEK_TO_NEXT_SYNC)
//        }
    }
    private fun navigateForward() {
        mVideoDecoder?.forward(TimeUnit.MILLISECONDS.toMillis(20 * 1000))
//        mVideoDecoder?.extractor()?.let {
//            val cur = it.sampleTime
//            mVideoDecoder!!.timBase+= TIME_1_MINUTES /1000
//            it.seekTo(cur+ TIME_1_MINUTES,MediaExtractor.SEEK_TO_NEXT_SYNC)
//        }
//        mAudioDecoder?.extractor()?.let {
//            val cur = it.sampleTime
//            mAudioDecoder!!.timBase+= TIME_1_MINUTES /1000
//            it.seekTo(cur+ TIME_1_MINUTES,MediaExtractor.SEEK_TO_NEXT_SYNC)
//        }
    }

    private fun startPlay(surfaceTexture: SurfaceTexture){
        val url = VideoUtil.strVideo
        binding.textureView.adjustTextureViewPerVideoAspectRation(url)


        lifecycleScope.launch {
            mVideoDecoder = VideoDecoder(Surface(surfaceTexture)).also { viewModel.addCloseable(it) }
            mAudioDecoder = AudioDecoder().also { viewModel.addCloseable(it) }
            launch(Dispatchers.IO)  {
                mVideoDecoder?.start(url)
            }
            launch(Dispatchers.IO)  {
                mAudioDecoder?.start(url)
            }
        }
    }



    private var mVideoDecoder: VideoDecoder? = null
    private var mAudioDecoder: AudioDecoder? = null


    fun seekWhenStopTracking(seekBar: SeekBar){
//        player?.run {
//            val duration = this.duration
//            val targetDuration = duration * (seekBar.progress*1.0f/seekBar.max)
//            seekTo(targetDuration.toInt())
//        }
    }



}