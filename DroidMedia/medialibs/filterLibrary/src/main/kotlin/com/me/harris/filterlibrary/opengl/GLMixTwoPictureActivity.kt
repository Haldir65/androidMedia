package com.me.harris.filterlibrary.opengl

import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import android.opengl.GLSurfaceView.RENDERMODE_CONTINUOUSLY
import android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY
import android.os.Bundle
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.filterlibrary.R
import com.me.harris.filterlibrary.databinding.ActivityGlMixTwoPictureBinding
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.concurrent.thread

/**
 * https://juejin.cn/post/7155040552353234951
 */
class GLMixTwoPictureActivity:AppCompatActivity() {


    private lateinit var binding:ActivityGlMixTwoPictureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlMixTwoPictureBinding.inflate(layoutInflater).also { setContentView(it.root) }
        binding.button.setOnClickListener {
            GLAccess.readAssests("shaders/fragment_amaro.glsl",resources.assets)
        }

        binding.glsurfaceview.holder.addCallback(object:SurfaceHolder.Callback{
            override fun surfaceCreated(holder: SurfaceHolder) {
                thread {
                    val bmp1 = BitmapFactory.decodeResource(resources,R.drawable.shiyuanmeili)
                    val bmp2 = BitmapFactory.decodeResource(resources,R.drawable.liyingai)
                    GLAccess.drawTexture(bmp1,bmp2,binding.glsurfaceview.holder.surface,resources.assets)
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }
        })
//        binding.glsurfaceview.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

    }

}