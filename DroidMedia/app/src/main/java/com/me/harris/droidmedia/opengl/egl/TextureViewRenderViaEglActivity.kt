package com.me.harris.droidmedia.opengl.egl

import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.droidmedia.R

class TextureViewRenderViaEglActivity:AppCompatActivity() {

    lateinit var mTextureView:TextureView
     var mRenderThread: RenderThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_texture_view_egl)
        mTextureView = findViewById(R.id.textureview)
        mTextureView.surfaceTextureListener = object :TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                 RenderThread(surface).also {
                     mRenderThread = it
                     it.start()
                 }
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {

            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
               return false
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mRenderThread?.stop = true
        mRenderThread = null
    }

}