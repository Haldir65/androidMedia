package com.me.harris.playerLibrary.exoplayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.LoadControl
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.Util
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.awesomelib.viewBinding
import com.me.harris.playerLibrary.R
import com.me.harris.playerLibrary.databinding.ActivityExoplayerSampleBinding

class ExoplayerSampleActivity:AppCompatActivity(R.layout.activity_exoplayer_sample) {

    private val binding:ActivityExoplayerSampleBinding by viewBinding(ActivityExoplayerSampleBinding::bind)


    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun initializePlayer() {
        /*How many milliseconds of media data to buffer at any time. */
        val loadControlMaxBufferMs = DefaultLoadControl.DEFAULT_MAX_BUFFER_MS;
        val loadControl = DefaultLoadControl.Builder().setBufferDurationsMs(loadControlMaxBufferMs,
            loadControlMaxBufferMs,
            DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
            DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS)
            .build()
        player = ExoPlayer.Builder(this).setLoadControl(loadControl)
            .build()
            .also { exoPlayer ->
                binding.videoView.player = exoPlayer

            } .also { exoPlayer ->
                val mediaItem = MediaItem.fromUri(VideoUtil.strVideo)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentWindow, playbackPosition)
                exoPlayer.prepare()
            }
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        binding.videoView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initializePlayer()
        }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUi()
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer()
        }
    }

    public override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    public override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentWindowIndex
            playWhenReady = this.playWhenReady
            release()
        }
        player = null
    }

}