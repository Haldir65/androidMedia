package com.me.harris.playerLibrary.ffmediaplayer

import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.awesomelib.viewBinding
import com.me.harris.awesomelib.surfaceFlow
import com.me.harris.awesomelib.utils.VideoUtil.adjustPlayerViewPerVideoAspectRation
import com.me.harris.awesomelib.whenProgressChanged
import com.me.harris.awesomelib.withSurfaceAvailable
import com.me.harris.playerLibrary.R
import com.me.harris.playerLibrary.databinding.ActivityFfmediaPlayerBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import wseemann.media.FFmpegMediaPlayer
import java.util.concurrent.atomic.AtomicBoolean

class FFMediaPlayerActivity : AppCompatActivity(R.layout.activity_ffmedia_player) {


    private val binding by viewBinding(ActivityFfmediaPlayerBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPlayer()
    }

    private fun initPlayer() {
        binding.playerSurfaceView.withSurfaceAvailable(::setUpplayerWithSurface)
        binding.seekbar.whenProgressChanged(::seekWhenStopTracking)
    }

    private var player:FFmpegMediaPlayer? = null

    override fun onDestroy() {
        super.onDestroy()
        player?.run {
            stop()
            release()
        }
    }

    private fun setUpplayerWithSurface(holder:SurfaceHolder){
        val player = FFmpegMediaPlayer()
        player.setOnPreparedListener { pl ->
            Log.w("=A=","onPrepared")
            pl.start()
        }
        player.setOnErrorListener { a,b,c ->
            Log.w("=A=","error ${b} ${c}")
            a.release()
            false
        }
        val url = VideoUtil.strVideo
        player.setDataSource(url)
        binding.playerSurfaceView.adjustPlayerViewPerVideoAspectRation(url)
        player.setSurface(holder.surface)
        Log.w("=A=","setDataSource ${url}")
        player.prepareAsync()
        Log.w("=A=","prepareAsync")
        this@FFMediaPlayerActivity.player = player
    }

    fun seekWhenStopTracking(seekBar: SeekBar){
        player?.run {
            val duration = this.duration
            val targetDuration = duration * (seekBar.progress*1.0f/seekBar.max)
            seekTo(targetDuration.toInt())
        }
    }
}
