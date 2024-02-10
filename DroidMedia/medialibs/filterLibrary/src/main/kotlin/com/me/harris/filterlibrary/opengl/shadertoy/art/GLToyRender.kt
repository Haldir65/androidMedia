package com.me.harris.filterlibrary.opengl.shadertoy.art

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLToyRender(private val context: Context) : GLSurfaceView.Renderer, OnTouchListener {
    private var shaderToy: ShaderToy? = null
    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        shaderToy = ShaderToy(context).also { it.onSurfaceCreated() }
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        shaderToy?.onSurfaceChanged(width, height)
    }

    override fun onDrawFrame(gl: GL10) {
        shaderToy?.draw()
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return shaderToy?.onTouch(v, event) == true
    }
}

