package com.me.harris.playerLibrary

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.playerLibrary.exoplayer.ExoplayerSampleActivity
import com.me.harris.playerLibrary.openglplayvideo.MediaPlayerSurfaceStubActivity
import com.me.harris.playerLibrary.textureview.TextureViewMediaCodecVideoPlayerActivity
import com.me.harris.playerLibrary.video.MediaCodecVideoMainActivity
import com.me.harris.playerLibrary.video.sharedSurface.SharedSurfaceTextureListActivity

class VideoPlayExtryActivity:AppCompatActivity() {

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
        findViewById<Button>(R.id.btn5)?.setOnClickListener {
            startActivity(Intent(this, ExoplayerSampleActivity::class.java))
        }
    }
}