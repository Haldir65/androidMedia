package com.me.harris.playerLibrary.exoplayer

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.Renderer
import androidx.media3.exoplayer.audio.AudioRendererEventListener
import androidx.media3.exoplayer.metadata.MetadataOutput
import androidx.media3.exoplayer.text.TextOutput
import androidx.media3.exoplayer.video.DecoderVideoRenderer
import androidx.media3.exoplayer.video.VideoRendererEventListener
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

    @OptIn(UnstableApi::class) private fun initializePlayer() {
        /*How many milliseconds of media data to buffer at any time. */
        val loadControlMaxBufferMs = DefaultLoadControl.DEFAULT_MAX_BUFFER_MS;
        val loadControl = DefaultLoadControl.Builder().setBufferDurationsMs(loadControlMaxBufferMs,
            loadControlMaxBufferMs,
            DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
            DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS)
            .build()
        player = ExoPlayer.Builder(this).setLoadControl(loadControl)
            .setRenderersFactory(object :DefaultRenderersFactory(this){
               init {
                   setExtensionRendererMode(EXTENSION_RENDERER_MODE_PREFER)
               }

                override fun createRenderers(
                    eventHandler: Handler,
                    videoRendererEventListener: VideoRendererEventListener,
                    audioRendererEventListener: AudioRendererEventListener,
                    textRendererOutput: TextOutput,
                    metadataRendererOutput: MetadataOutput
                ): Array<Renderer> {
                    val red = super.createRenderers(
                        eventHandler,
                        videoRendererEventListener,
                        audioRendererEventListener,
                        textRendererOutput,
                        metadataRendererOutput
                    )
                    red.filterIsInstance<DecoderVideoRenderer>().forEach { videoRender ->
                        Log.e("videoRender" , "${videoRender.name}")

                    }
                    return red
                }

            })
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
        if (Build.VERSION.SDK_INT >= 24) {
            initializePlayer()
        }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUi()
        if ((Build.VERSION.SDK_INT < 24 || player == null)) {
            initializePlayer()
        }
    }

    public override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT < 24) {
            releasePlayer()
        }
    }

    public override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    @OptIn(UnstableApi::class) private fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentWindowIndex
            playWhenReady = this.playWhenReady
            release()
        }
        player = null
    }

}
