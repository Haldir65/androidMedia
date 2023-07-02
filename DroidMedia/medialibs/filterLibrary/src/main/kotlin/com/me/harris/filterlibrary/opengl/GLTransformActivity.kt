package com.me.harris.filterlibrary.opengl

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.filterlibrary.R
import com.me.harris.filterlibrary.databinding.ActivityGlMixTwoPictureBinding
import com.me.harris.filterlibrary.databinding.ActivityGlTransformationBinding
import kotlin.concurrent.thread



class GLTransformActivity:AppCompatActivity() {


   private companion object FILTER__TYPES {
        const val filter_type_gray = 1
        const val filter_type_oppo = 2
        const val filter_type_oppo_gray = 3
        const val filter_type_divide_2 = 4
        const val filter_type_divide_4 = 5
    }

    private lateinit var binding: ActivityGlTransformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlTransformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.glsurfaceview.holder.addCallback(object: SurfaceHolder.Callback{
            override fun surfaceCreated(holder: SurfaceHolder) {
                thread {
                    GLAccess.loadYuvTransform(binding.glsurfaceview.holder.surface,resources.assets,filter_type_divide_4)
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