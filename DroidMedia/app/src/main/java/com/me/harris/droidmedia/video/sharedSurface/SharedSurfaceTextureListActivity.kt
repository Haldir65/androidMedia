package com.me.harris.droidmedia.video.sharedSurface

import android.content.Intent
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.droidmedia.R
import com.me.harris.droidmedia.utils.VideoUtil

class SharedSurfaceTextureListActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SharedSurfaceList"
    }

    lateinit var mTextureView: TextureView
    lateinit var mTextView: TextView
    lateinit var mButton: Button

    private var mMediaPlayer: MediaPlayer? = null

    private var mSurfaceTexture:SurfaceTexture? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shared_surface_list)
        mTextureView = findViewById(R.id.player_texture_view_list)
        mTextView = findViewById(R.id.player_text_view_list)
        mButton = findViewById(R.id.player_list_to_detail_button)
        mButton.setOnClickListener {
//            mMediaPlayer?.stop()
            startActivity(Intent(this,SharedSurfaceTextureDetailActivity::class.java))
        }
        initTexture()
    }


    private fun initTexture() {
        mTextureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                Log.e(TAG,"onSurfaceTextureAvailable $surface")
                if (SharedSurfaceManager.mPlayer==null || SharedSurfaceManager.mSurfaceTexture == null){
                    initPlayer(surface)
                    SharedSurfaceManager.mSurfaceTexture = surface
                    mSurfaceTexture = surface
                }
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                Log.e(TAG,"onSurfaceTextureSizeChanged $surface")
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture):Boolean {
                Log.e(TAG,"onSurfaceTextureDestroyed $surface")
                return false
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
//                Log.v(TAG,"onSurfaceTextureUpdated $surface")
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        mSurfaceTexture?.let {
            mMediaPlayer?.setSurface(Surface(it))
        }
    }


    private fun initPlayer(surfaceTexture: SurfaceTexture) {
        MediaPlayer().also {
            mMediaPlayer = it
            SharedSurfaceManager.mPlayer = it
            VideoUtil.setUrl()
            SharedSurfaceManager.playingUrl = VideoUtil.strVideo
            it.setDataSource(SharedSurfaceManager.playingUrl)
            val surface = SharedSurfaceManager.mSurfaceTexture ?: kotlin.run {
                surfaceTexture
            }
            it.setSurface(Surface(surface))
            it.setOnPreparedListener {
                it?.start()
            }
            it.prepareAsync()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            mMediaPlayer?.stop()
            mMediaPlayer?.release()
        }catch (e:IllegalStateException){
            e.printStackTrace()
        }
        SharedSurfaceManager.mPlayer = null
        mSurfaceTexture = null
        SharedSurfaceManager.mSurfaceTexture?.release()
        SharedSurfaceManager.mSurfaceTexture = null
    }

}