package com.me.harris.filterlibrary.opengl.enrty.egl;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.util.Random;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class RenderThread extends Thread{
    private SurfaceTexture mSurface;
    private EGL10 mEgl;
    private EGLDisplay mEglDisplay;
    private EGLConfig mEglConfig;
    private EGLContext mEglContext;
    private EGLSurface mEglSurface;


    public volatile boolean stop = false;

    public RenderThread(SurfaceTexture surfaceTexture) {
        mSurface = surfaceTexture;
    }
    @Override
    public void run() {
        if (stop) return;
        initGL();
        //开始画一帧数据
        while (!stop){
            drawFrame();
            // 一帧完成之后，调用eglSwapBuffers(EGLDisplay dpy, EGLContext ctx)来显示
            // 这一句不能少啊，少了就什么都没有
            mEgl.eglSwapBuffers(mEglDisplay, mEglSurface);
        }

    }
    private void initGL(){
        /*Get EGL handle*/
        mEgl = (EGL10) EGLContext.getEGL();
        /*Get EGL display*/
        mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (EGL10.EGL_NO_DISPLAY == mEglDisplay){
            throw new RuntimeException("eglGetDisplay failed:"+ GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }
        /*Initialize & Version*/
        int versions[] = new int[2];
        if (!mEgl.eglInitialize(mEglDisplay, versions)){
            throw new RuntimeException("eglInitialize failed:"+GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }
        /*Configuration*/
        int configsCount[] = new int[1];
        EGLConfig configs[] = new EGLConfig[1];
        int configSpec[] = new int[]{
                EGL10.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_DEPTH_SIZE, 0,
                EGL10.EGL_STENCIL_SIZE, 0,
                EGL10.EGL_NONE
        };
        mEgl.eglChooseConfig(mEglDisplay, configSpec, configs, 1, configsCount);
        if (configsCount[0] <= 0){
            throw new RuntimeException("eglChooseConfig failed:"+ GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }
        mEglConfig = configs[0];
        /*Create Context*/
        int contextSpec[] = new int[]{
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL10.EGL_NONE
        };
        mEglContext = mEgl.eglCreateContext(mEglDisplay, mEglConfig, EGL10.EGL_NO_CONTEXT, contextSpec);
        if (EGL10.EGL_NO_CONTEXT == mEglContext){
            throw new RuntimeException("eglCreateContext failed: "+GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }
        /*Create window surface*/
        mEglSurface = mEgl.eglCreateWindowSurface(mEglDisplay, mEglConfig, mSurface, null);
        if (null == mEglSurface || EGL10.EGL_NO_SURFACE == mEglSurface){
            throw new RuntimeException("eglCreateWindowSurface failed"+GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }
        /*Make current*/
        if (!mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)){
            throw new RuntimeException("eglMakeCurrent failed:"+GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }
    }


    float r = 0f;
    float g = 0f;
    float b = 0f;
    Random random = new Random(1089);

    private void drawFrame() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        float r2 = r++;
        float g2 = g++;
        float b2 = b++;
        float rcolor1 = (random.nextInt((int) 255.0f)%255.0f)/255.0f;
        float rcolor2 = (random.nextInt((int) 255.0f)%255.0f)/255.0f;
        float rcolor3 = (b2%255.0f)/255.0f;
        //将背景设置为灰色
//        GLES20.glClearColor(0f,0f,1f,1.0f); glClearColor(184.0f/255.0f, 213.0f/255.0f, 238.0f/255.0f, 1.0f);
        GLES20.glClearColor(1.0f,rcolor3,1.0f,1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }

}
