package com.me.harris.filterlibrary.opengl

import android.os.Bundle
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.filterlibrary.databinding.ActivityGlGaussianBlurBinding
import com.me.harris.filterlibrary.databinding.ActivityGlTransformationBinding
import kotlin.concurrent.thread

// https://juejin.cn/post/7228220230634864699#heading-8
class GLGaussianBlurActivity:AppCompatActivity() {

    private lateinit var binding: ActivityGlGaussianBlurBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlGaussianBlurBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.glsurfaceview.holder.addCallback(object: SurfaceHolder.Callback{
            override fun surfaceCreated(holder: SurfaceHolder) {
                thread {
                    GLAccess.loadYuvGaussianBlur(binding.glsurfaceview.holder.surface,resources.assets,)
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
    }

}