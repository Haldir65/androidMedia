package com.me.harris.playerLibrary.openglplayvideo

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.awesomelib.utils.Utils
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.awesomelib.whenProgressChanged
import com.me.harris.playerLibrary.R
import com.me.harris.playerLibrary.video.GLVideoSurfaceView

class MediaPlayerSurfaceStubActivity:AppCompatActivity() {

    private lateinit var mVideoView: GLVideoSurfaceView
    private lateinit var seekbar: SeekBar
    private lateinit var player:MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mediaplayer_glsurfaceview)
        seekbar = findViewById(R.id.seekbar)
        player = MediaPlayer()
        mVideoView =
            GLVideoSurfaceView(
                this,
                player.apply {
                    setDataSource(VideoUtil.strVideo)
                })
        findViewById<LinearLayout>(R.id.container)?.addView(mVideoView,0,
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Utils.dip2px(this,200F)))

        seekbar.whenProgressChanged(::seekWhenStopTracking)
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