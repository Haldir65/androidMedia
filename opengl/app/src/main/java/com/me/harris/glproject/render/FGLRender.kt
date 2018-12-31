package com.me.harris.glproject.render

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.view.View
import com.me.harris.glproject.shapes.Shape
import com.me.harris.glproject.shapes.Square
import com.me.harris.glproject.tools.LogUtil
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class FGLRender(val view:View) : GLSurfaceView.Renderer {

    lateinit var shape:Shape

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f)
        shape = Square(view)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        shape.onSurfaceChanged(gl, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        LogUtil.e(TAG,"onDrawFrame")
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        shape.onDrawFrame(gl)

    }

    companion object {
        const val TAG = "RGLRENDER"
    }


}