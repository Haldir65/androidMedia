package com.me.harris.filterlibrary.opengl

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.filterlibrary.R
import com.me.harris.filterlibrary.databinding.ActivityGlLoadYuvBinding
import kotlin.concurrent.thread

class GLLoadYUVActivity:AppCompatActivity() {

    private lateinit var binding:ActivityGlLoadYuvBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlLoadYuvBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.button.setOnClickListener {  }
        binding.glsurfaceview.holder.addCallback(object :SurfaceHolder.Callback{
            override fun surfaceCreated(holder: SurfaceHolder) {
                    thread {
                        GLAccess.loadYuv(surface = holder.surface, assetmanager = resources.assets)
                    }
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }

        })
    }
}