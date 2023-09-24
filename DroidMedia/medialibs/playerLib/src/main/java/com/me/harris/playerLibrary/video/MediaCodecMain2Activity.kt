package com.me.harris.playerLibrary.video

import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.postDelayed
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.awesomelib.utils.VideoUtil.setUrl
import com.me.harris.awesomelib.videoutil.VideoInfoHelper
import com.me.harris.awesomelib.viewBinding
import com.me.harris.playerLibrary.R
import com.me.harris.playerLibrary.databinding.ActivityMediaCodecVideoPlayerBinding
import com.me.harris.playerLibrary.video.vm.MediaCodeMain2ViewModel
import kotlinx.coroutines.*

class MediaCodecMain2Activity : AppCompatActivity(R.layout.activity_media_codec_video_player) {


    private val binding by viewBinding(ActivityMediaCodecVideoPlayerBinding::bind)
    private val viewModel by viewModels<MediaCodeMain2ViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUrl()
        initViews()
        initViewModel()
    }

    private var isSeeking = false


    private fun initViews() {
        val url = VideoUtil.strVideo
        binding.player.strVideo = url
//        watchProgress(this, binding.seekbar, binding.player)
        val displayMetrics = DisplayMetrics()
        val arr = VideoInfoHelper.queryVideoInfo(VideoUtil.strVideo)
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        binding.player.updateLayoutParams<ViewGroup.LayoutParams> {
            this.width = displayMetrics.widthPixels
            this.height = (width * (arr[1].toFloat() / arr[0].toFloat())).toInt()
        }

        binding.player.holder.setKeepScreenOn(true)

        val videoDuration by lazy {
            getVideoDurationInMicroSeconds(url)
        }


        binding.seekbar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar!=null){
                    val percentage = with(seekBar) { progress.toFloat() / max }
                    binding.player.seek((videoDuration*percentage).toLong())
                    seekBar.postDelayed(20) {
                        isSeeking = false
                    }
                }
            }

        })

        lifecycleScope.launch {
            delay(1000)
            while (currentCoroutineContext().isActive){
                val total = binding.player.duration
                val current = binding.player.currentPosition
                val thismax = binding.seekbar.max
                val percentage = current.toFloat()/total.toFloat()
                if (!isSeeking){
                    binding.seekbar.setProgress((thismax*percentage).toInt(),false)
                }
                delay(100)
            }
        }
    }

    private fun initViewModel() {
//        viewModel.probeVideoInfo()
    }

    override fun onResume() {
        super.onResume()
        binding.player.start()
    }

    override fun onPause() {
        super.onPause()
        binding.player.stop()
    }

}
