package com.me.harris.filterlibrary.opengl.enrty.egl

import android.graphics.SurfaceTexture
import android.opengl.EGL14
import android.opengl.GLES20
import android.opengl.GLUtils
import java.util.*
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import javax.microedition.khronos.egl.EGLSurface

class RenderThread(val mSurface: SurfaceTexture):Thread() {

    private var mEgl: EGL10? = null
    private var mEglDisplay: EGLDisplay? = null
    private var mEglConfig: EGLConfig? = null
    private var mEglContext: EGLContext? = null
    private var mEglSurface: EGLSurface? = null

    @Volatile
    var stop = false


    override fun run() {
        if (stop) return
        initGL()
        //开始画一帧数据
        while (!stop) {
            drawFrame()
            // 一帧完成之后，调用eglSwapBuffers(EGLDisplay dpy, EGLContext ctx)来显示
            // 这一句不能少啊，少了就什么都没有
            mEgl!!.eglSwapBuffers(mEglDisplay, mEglSurface)
        }
    }

    private fun initGL() {
        /*Get EGL handle*/
        mEgl = EGLContext.getEGL() as EGL10
        /*Get EGL display*/mEglDisplay = mEgl!!.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
        if (EGL10.EGL_NO_DISPLAY === mEglDisplay) {
            throw RuntimeException("eglGetDisplay failed:" + GLUtils.getEGLErrorString(mEgl!!.eglGetError()))
        }
        /*Initialize & Version*/
        val versions = IntArray(2)
        if (!mEgl!!.eglInitialize(mEglDisplay, versions)) {
            throw RuntimeException("eglInitialize failed:" + GLUtils.getEGLErrorString(mEgl!!.eglGetError()))
        }
        /*Configuration*/
        val configsCount = IntArray(1)
        val configs = arrayOfNulls<EGLConfig>(1)
        val configSpec = intArrayOf(
            EGL10.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
            EGL10.EGL_RED_SIZE, 8,
            EGL10.EGL_GREEN_SIZE, 8,
            EGL10.EGL_BLUE_SIZE, 8,
            EGL10.EGL_ALPHA_SIZE, 8,
            EGL10.EGL_DEPTH_SIZE, 0,
            EGL10.EGL_STENCIL_SIZE, 0,
            EGL10.EGL_NONE
        )
        mEgl!!.eglChooseConfig(mEglDisplay, configSpec, configs, 1, configsCount)
        if (configsCount[0] <= 0) {
            throw RuntimeException("eglChooseConfig failed:" + GLUtils.getEGLErrorString(mEgl!!.eglGetError()))
        }
        mEglConfig = configs[0]
        /*Create Context*/
        val contextSpec = intArrayOf(
            EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL10.EGL_NONE
        )
        mEglContext = mEgl!!.eglCreateContext(mEglDisplay, mEglConfig, EGL10.EGL_NO_CONTEXT, contextSpec)
        if (EGL10.EGL_NO_CONTEXT === mEglContext) {
            throw RuntimeException("eglCreateContext failed: " + GLUtils.getEGLErrorString(mEgl!!.eglGetError()))
        }
        /*Create window surface*/mEglSurface = mEgl!!.eglCreateWindowSurface(mEglDisplay, mEglConfig, mSurface, null)
        if (null == mEglSurface || EGL10.EGL_NO_SURFACE === mEglSurface) {
            throw RuntimeException("eglCreateWindowSurface failed" + GLUtils.getEGLErrorString(mEgl!!.eglGetError()))
        }
        /*Make current*/if (!mEgl!!.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {
            throw RuntimeException("eglMakeCurrent failed:" + GLUtils.getEGLErrorString(mEgl!!.eglGetError()))
        }
    }

    var r = 0f
    var g = 0f
    var b = 0f
    var random = Random(1089)

    private fun drawFrame() {
        try {
            sleep(10)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        val r2 = r++
        val g2 = g++
        val b2 = b++
        val rcolor1 = random.nextInt(255.0f.toInt()) % 255.0f / 255.0f
        val rcolor2 = random.nextInt(255.0f.toInt()) % 255.0f / 255.0f
        val rcolor3 = b2 % 255.0f / 255.0f
        //将背景设置为灰色
//        GLES20.glClearColor(0f,0f,1f,1.0f); glClearColor(184.0f/255.0f, 213.0f/255.0f, 238.0f/255.0f, 1.0f);
        GLES20.glClearColor(1.0f, rcolor3, 1.0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    }
}
