package com.me.harris.droidmedia.video.sharedSurface

import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.droidmedia.R

class SharedSurfaceTextureDetailActivity:AppCompatActivity() {

    companion object {
        const val TAG = "SharedSurfaceDetail"
    }

    lateinit var mTextureView: TextureView
    lateinit var mTextView: TextView

    var mMediaPlayer:MediaPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shared_surface_detail)
        mTextureView = findViewById(R.id.player_texture_view_detail)
        mTextView = findViewById(R.id.player_text_view_detail)
        initTexture()
    }

    private fun initTexture(){
//        mTextureView.setSurfaceTexture(SharedSurfaceManager.mSurfaceTexture!!)
        initPlayer(SharedSurfaceManager.mSurfaceTexture!!)

        mTextureView.surfaceTextureListener = object :TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                mMediaPlayer?.setSurface(Surface(surface))
//                if (SharedSurfaceManager.mSurfaceTexture!=null ){
//                    mTextureView.setSurfaceTexture(SharedSurfaceManager.mSurfaceTexture!!)
//                }else{
//                }
//                Log.e(TAG,"onSurfaceTextureAvailable ${mTextureView.surfaceTexture}")


            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {

            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture) :Boolean{

                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

            }
        }
    }


    private fun initPlayer(surfaceTexture:SurfaceTexture){
        SharedSurfaceManager.mPlayer?.let {
            mMediaPlayer = it
            if (!it.isPlaying){
                it.start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer = null
    }


}