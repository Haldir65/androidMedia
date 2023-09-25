package com.me.harris.playerLibrary.video

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.SurfaceHolder
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.postDelayed
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.awesomelib.utils.VideoUtil.setUrl
import com.me.harris.awesomelib.videoutil.VideoInfoHelper
import com.me.harris.awesomelib.viewBinding
import com.me.harris.playerLibrary.R
import com.me.harris.playerLibrary.databinding.ActivityMediaCodecVideoPlayerBinding
import com.me.harris.playerLibrary.misc.CommonUtils
import com.me.harris.playerLibrary.video.player.MediaCodecVideoPlayer
import com.me.harris.playerLibrary.video.vm.MediaCodeMain2ViewModel
import com.me.harris.playerLibrary.video.vm.MuteState
import com.me.harris.playerLibrary.video.vm.PlayState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MediaCodecMain2Activity : AppCompatActivity(R.layout.activity_media_codec_video_player) {

    private val binding by viewBinding(ActivityMediaCodecVideoPlayerBinding::bind)
    private val viewModel by viewModels<MediaCodeMain2ViewModel>()
    private var player:MediaCodecVideoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUrl()
        initViews()
        initViewModel()
    }

    private var isSeeking = false

    private fun initViews() {
        val displayMetrics = DisplayMetrics()
        val arr = VideoInfoHelper.queryVideoInfo(VideoUtil.strVideo)
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        binding.surfaceView.updateLayoutParams<ViewGroup.LayoutParams> {
            this.width = displayMetrics.widthPixels
            this.height = (width * (arr[1].toFloat() / arr[0].toFloat())).toInt()
        }

        binding.surfaceView.holder.setKeepScreenOn(true)

        player = MediaCodecVideoPlayer()
        val videoDuration by lazy {
            requireNotNull(player).getDuration()
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    val percentage = with(seekBar) { progress.toFloat() / max }
                    player?.seekTo((videoDuration * percentage).toLong())
                    seekBar.postDelayed(20) {
                        isSeeking = false
                    }
                }
            }
        })

        lifecycleScope.launch {
            delay(1000)
            while (currentCoroutineContext().isActive && player!=null) {
                val total = videoDuration
                val current = player!!.getCurrentPosition()
                val thismax = binding.seekBar.max
                val percentage = current.toFloat() / total.toFloat()
                if (!isSeeking) {
                    binding.seekBar.setProgress((thismax * percentage).toInt(), false)
                    withContext(Dispatchers.Main.immediate) {
                        binding.tvTimer.text = "${CommonUtils.formatDuration(current / 1000.toLong())} / ${CommonUtils.formatDuration(total / 1000.toLong())}"
                    }
                }
                delay(100)
            }
        }

        binding.surfaceView.holder.addCallback(object :SurfaceHolder.Callback{
            override fun surfaceCreated(holder: SurfaceHolder) {

            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                val sf = holder.surface
                val p = player?: MediaCodecVideoPlayer()
                with(p){
                    setDataSource(getPlayableSource())
                    setSurface(sf)
                    prepare()
                    start()
                }

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                // player.release
                player?.release()
            }
        })

        binding.btnAudio.setOnClickListener {
            when(viewModel.playMuteState.value){
                is MuteState.Mute -> {
                    player?.setMute(false)
                    viewModel.playMuteState.value = MuteState.NotMute
                }
                is MuteState.NotMute ->{
                    player?.setMute(true)
                    viewModel.playMuteState.value = MuteState.Mute
                }
            }
        }

        binding.btnPlayer.setOnClickListener {
            when(viewModel.playButtonState.value){
                is PlayState.PlayPlaying -> {
                    player?.pause()
                    viewModel.playButtonState.value = PlayState.PlayPaused
                }
                is PlayState.PlayPaused,PlayState.PlayIdle -> {
                    player?.resume()
                    viewModel.playButtonState.value = PlayState.PlayPlaying
                }
            }
        }
    }


    private fun initViewModel() {
//        viewModel.probeVideoInfo()
        lifecycleScope.launch {
            launch {
                viewModel.playButtonState.onEach { p ->
                    when (p) {
                        is PlayState.PlayPlaying -> {
                            binding.btnPlayer.background =
                                ContextCompat.getDrawable(this@MediaCodecMain2Activity, R.drawable.play_resume)
                        }

                        is PlayState.PlayIdle -> {
                            binding.btnPlayer.background =
                                ContextCompat.getDrawable(this@MediaCodecMain2Activity, R.drawable.play_pause)
                        }

                        is PlayState.PlayPaused -> {
                            binding.btnPlayer.background =
                                ContextCompat.getDrawable(this@MediaCodecMain2Activity, R.drawable.play_pause)
                        }
                    }
                }.collect()
            }

            launch {
                viewModel.playMuteState.onEach { p ->
                    when (p) {
                        is MuteState.Mute -> {
                            binding.btnAudio.background =
                                ContextCompat.getDrawable(this@MediaCodecMain2Activity, R.drawable.audio_disable)
                        }

                        is MuteState.NotMute -> {
                            binding.btnAudio.background =
                                ContextCompat.getDrawable(this@MediaCodecMain2Activity, R.drawable.audio_enable)
                        }
                    }
                }.collect()
            }

        }
    }

    override fun onResume() {
        super.onResume()
        player?.resume()
        Log.w("=A=","onResume")
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
        Log.w("=A=","onPause")
    }

    private fun getPlayableSource():String = VideoUtil.strVideo
}
