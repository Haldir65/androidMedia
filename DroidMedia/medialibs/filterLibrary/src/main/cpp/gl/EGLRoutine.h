//
// Created by harris on 2023/6/24.
//

#ifndef DROIDMEDIA_EGLROUTINE_H
#define DROIDMEDIA_EGLROUTINE_H


#include <jni.h>
#include "GDog.h"
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <EGL/egl.h>
#include <GLES3/gl3.h>

class EGLRoutine{
public:
    EGLRoutine();

    virtual ~EGLRoutine();

    GLint initShader(const char *source, int type);


    void eglSetup(JNIEnv *env, jobject surface);

    void eglSwapBuffer();


private:
    EGLDisplay *mEglDisplay;
    EGLSurface *mGlSurface;
};




#endif //DROIDMEDIA_EGLROUTINE_H
