package com.me.harris.glproject.view

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.me.harris.glproject.render.FGLRender

class FGLView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs){

    init {
        setEGLContextClientVersion(2)
        setRenderer(FGLRender(this))
        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }




}