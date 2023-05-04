package com.me.harris.playerLibrary.openglplayvideo

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.awesomelib.utils.Utils
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.playerLibrary.R
import com.me.harris.playerLibrary.video.GLVideoSurfaceView

class MediaPlayerSurfaceStubActivity:AppCompatActivity() {

    private lateinit var mVideoView: GLVideoSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mediaplayer_glsurfaceview)
        mVideoView =
            GLVideoSurfaceView(
                this,
                MediaPlayer().apply {
                    setDataSource(VideoUtil.strVideo)
                })
        findViewById<FrameLayout>(R.id.container)?.addView(mVideoView,
            FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, Utils.dip2px(this,200F)))
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