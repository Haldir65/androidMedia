package com.me.harris.filterlibrary.opengl.shadertoy

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.filterlibrary.databinding.ActivityGlShaderToyBinding
import com.me.harris.filterlibrary.opengl.shadertoy.art.GLToyRender

class ShaderToyEntryActivity:AppCompatActivity() {

    private lateinit var binding: ActivityGlShaderToyBinding;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlShaderToyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.glsurfaceview.setEGLContextClientVersion(3)
        binding.glsurfaceview.setRenderer(GLToyRender(this))
        binding.glsurfaceview.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }
}
