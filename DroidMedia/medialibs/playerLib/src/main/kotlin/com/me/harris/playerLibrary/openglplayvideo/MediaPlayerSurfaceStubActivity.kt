package com.me.harris.playerLibrary.openglplayvideo

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.me.harris.awesomelib.updateProgressWithMediaPlayer
import com.me.harris.awesomelib.utils.Utils
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.awesomelib.whenProgressChanged
import com.me.harris.playerLibrary.R
import com.me.harris.playerLibrary.video.GLVideoSurfaceView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MediaPlayerSurfaceStubActivity:AppCompatActivity() {

    companion object {
        const val KEY_VIDEO_PATH = "key_video_path"
    }

    private lateinit var mVideoView: GLVideoSurfaceView
    private lateinit var seekbar: SeekBar
    private lateinit var player:MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mediaplayer_glsurfaceview)
        seekbar = findViewById(R.id.seekbar)
        val filePath = intent.getStringExtra(KEY_VIDEO_PATH).orEmpty().ifEmpty { VideoUtil.strVideo }
        player = MediaPlayer()
        mVideoView =
            GLVideoSurfaceView(
                this,
                player.apply {
                    setDataSource(filePath)
                })
        findViewById<LinearLayout>(R.id.container)?.addView(mVideoView,0,
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Utils.dip2px(this,200F)))

        seekbar.whenProgressChanged(::seekWhenStopTracking)
        lifecycleScope.launch{
            delay(1000)
            seekbar.updateProgressWithMediaPlayer(player)
        }
    }

    override fun onResume() {
        super.onResume()
        mVideoView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
       mVideoView.onPause()
    }

    private fun seekWhenStopTracking(seekBar: SeekBar){
        player?.run {
            val duration = this.duration
            val targetDuration = duration * (seekBar.progress*1.0f/seekBar.max)
            seekTo(targetDuration.toInt())
        }
    }

}