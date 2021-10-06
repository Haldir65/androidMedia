package com.me.harris.droidmedia.openglplayvideo

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.droidmedia.R
import com.me.harris.droidmedia.utils.Utils
import com.me.harris.droidmedia.video.VideoPlayView

class MediaPlayerSurfaceStubActivity:AppCompatActivity() {

    private lateinit var mVideoView:VideoSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mediaplayer_glsurfaceview)
        mVideoView = VideoSurfaceView(this,MediaPlayer().apply {
            setDataSource(VideoPlayView.strVideo)
        })
        findViewById<FrameLayout>(R.id.container)?.addView(mVideoView,
            FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,Utils.dip2px(this,200F)))
    }

    override fun onResume() {
        super.onResume()
        mVideoView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
       mVideoView.onPause()
    }

}