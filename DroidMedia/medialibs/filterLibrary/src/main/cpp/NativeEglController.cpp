#include <jni.h>
#include "common/Looper.h"
#include "common/MyLooper.h"
#include <android/native_window.h>
#include <android/native_window_jni.h>

MyLooper *mLooper = NULL;
ANativeWindow *mWindow = NULL;

//
// Created by harris on 2023/6/10.
//

extern "C"
JNIEXPORT void JNICALL
Java_com_cgfay_filterlibrary_EGLRender_nativeInit(JNIEnv *env, jobject thiz) {
    mLooper = new MyLooper();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_cgfay_filterlibrary_EGLRender_nativeRelease(JNIEnv *env, jobject thiz) {
    if (mLooper != NULL) {
        mLooper->quit();
        delete mLooper;
        mLooper = NULL;
    }
    if (mWindow) {
        ANativeWindow_release(mWindow);
        mWindow = NULL;
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_com_cgfay_filterlibrary_EGLRender_onSurfaceCreated(JNIEnv *env, jobject thiz,
                                                        jobject surface) {
    if (mWindow) {
        ANativeWindow_release(mWindow);
        mWindow = NULL;
    }
    mWindow = ANativeWindow_fromSurface(env, surface);
    if (mLooper) {
        mLooper->postMessage(kMsgSurfaceCreated, mWindow);
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_com_cgfay_filterlibrary_EGLRender_onSurfaceChanged(JNIEnv *env, jobject thiz, jint width,
                                                        jint height) {
    if (mLooper) {
        mLooper->postMessage(kMsgSurfaceChanged, width, height);
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_com_cgfay_filterlibrary_EGLRender_onSurfaceDestroyed(JNIEnv *env, jobject thiz) {
    if (mLooper) {
        mLooper->postMessage(kMsgSurfaceDestroyed);
    }
}