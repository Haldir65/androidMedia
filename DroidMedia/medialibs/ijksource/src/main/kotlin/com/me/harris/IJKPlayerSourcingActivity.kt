package com.me.harris

import android.media.MediaPlayer
import android.os.Bundle
import android.system.Os.bind
import android.util.Log
import android.view.SurfaceHolder
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.me.harris.awesomelib.updateProgressWithMediaPlayer
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.awesomelib.utils.VideoUtil.adjustPlayerViewPerVideoAspectRation
import com.me.harris.awesomelib.viewBinding
import com.me.harris.awesomelib.whenProgressChanged
import com.me.harris.awesomelib.withSurfaceAvailable
import com.me.harris.ijksource.R
import com.me.harris.ijksource.databinding.ActivityIjkSourcingBinding
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_FORMAT

class IJKPlayerSourcingActivity:AppCompatActivity(R.layout.activity_ijk_sourcing) {

    private val binding by viewBinding(ActivityIjkSourcingBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPlayer()
    }

    private fun initPlayer() {
        binding.playerSurfaceView.withSurfaceAvailable(::setUpplayerWithSurface)
        binding.seekbar.whenProgressChanged(::seekWhenStopTracking)
    }

    private var player:IjkMediaPlayer? = null

    override fun onDestroy() {
        super.onDestroy()
        player?.run {
            stop()
            release()
        }
    }

    private fun setUpplayerWithSurface(holder: SurfaceHolder){
        val player = IjkMediaPlayer()
        // see ff_ffplay_options.h
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT,"opensles",1)
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT,"soundtouch",1)
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT,"mediacodec-all-videos",1)
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT,"start-on-prepared",1)
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
        this@IJKPlayerSourcingActivity.player = player
        lifecycleScope.launch {
            binding.seekbar.updateProgressWithMediaPlayer(player)
        }
    }

    fun seekWhenStopTracking(seekBar: SeekBar){
        player?.run {
            val duration = this.duration
            val targetDuration = duration * (seekBar.progress*1.0f/seekBar.max)
            seekTo(targetDuration.toLong())
        }
    }

   private suspend fun SeekBar.updateProgressWithMediaPlayer(player: IjkMediaPlayer){
        while (currentCoroutineContext().isActive){
            val total = player.duration
            val current = player.currentPosition
            val thismax = max
            val percentage = current.toFloat()/total.toFloat()
            setProgress((thismax*percentage).toInt(),false)
            delay(1000)
        }
    }
}