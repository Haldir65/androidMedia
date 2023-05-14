package com.me.harris.playerLibrary

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.contextaware.withContextAvailable
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import bzijkplayer.IJKPlayerTestActivity
import com.me.harris.IJKPlayerSourcingActivity
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.playerLibrary.exoplayer.ExoplayerSampleActivity
import com.me.harris.playerLibrary.ffmediaplayer.FFMediaPlayerActivity
import com.me.harris.playerLibrary.openglplayvideo.MediaPlayerSurfaceStubActivity
import com.me.harris.playerLibrary.textureview.TextureViewMediaCodecVideoPlayerActivity
import com.me.harris.playerLibrary.video.MediaCodecVideoMainActivity
import com.me.harris.playerLibrary.video.sharedSurface.SharedSurfaceTextureListActivity
import com.me.harris.playerLibrary.viewmodels.VideoPlayEntryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class VideoPlayExtryActivity:AppCompatActivity() {

    private val viewModel by viewModels<VideoPlayEntryViewModel>()
    init {
        lifecycleScope.launch {
            withContextAvailable {
                launch(Dispatchers.IO) {
                    VideoUtil.setUrl()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_entry)
        findViewById<Button>(R.id.btn1)?.setOnClickListener {
            startActivity(Intent(this, MediaCodecVideoMainActivity::class.java))
        }
        findViewById<Button>(R.id.btn2)?.setOnClickListener {
            startActivity(Intent(this, TextureViewMediaCodecVideoPlayerActivity::class.java))
        }
        findViewById<Button>(R.id.btn3)?.setOnClickListener {
            startActivity(Intent(this, MediaPlayerSurfaceStubActivity::class.java))
        }
        findViewById<Button>(R.id.btn4)?.setOnClickListener {
            startActivity(Intent(this, SharedSurfaceTextureListActivity::class.java))
        }
        findViewById<Button>(R.id.btn5)?.setOnClickListener {
            startActivity(Intent(this, ExoplayerSampleActivity::class.java))
        }
        findViewById<Button>(R.id.btn6)?.setOnClickListener {
            startActivity(Intent(this, FFMediaPlayerActivity::class.java))
        }
        findViewById<Button>(R.id.btn7)?.setOnClickListener {
            startActivity(Intent(this, IJKPlayerSourcingActivity::class.java))
        }
        viewModel.doStuff()
    }
}