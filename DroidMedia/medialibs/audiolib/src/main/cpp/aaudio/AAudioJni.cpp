// for jni
#include <jni.h>
// for c++ 要引入，否则NULL关键字找不到
#include <stdio.h>
#include <memory.h>
#include <iostream>
#include "../AndroidLog.h"
#include "AAudioEngine.h"

extern "C"
JNIEXPORT jlong JNICALL
Java_com_me_harris_audiolib_audioPlayer_AAudioPlayer_nativeCreateAAudioEngine(JNIEnv *env,
                                                                              jobject thiz,
                                                                              jstring file_path,
                                                                              jint sampleRate,
                                                                              jint audioChannel,
                                                                              jint audioFormat) {
    using namespace aaudiodemo;
    jboolean isCopy;
    const char *convertedValue = (env)->GetStringUTFChars(file_path, &isCopy);
    std::string string = convertedValue;
    auto playEngine = new AAudioEngine( string,
                                       sampleRate, audioChannel, audioFormat);
    env->ReleaseStringUTFChars(file_path, convertedValue);
    bool ret = playEngine->Init();
    LOGI("nativeCreateAAudioEngine, init ret:%d", ret);
    if (ret) {
        return reinterpret_cast<jlong> (playEngine);
    } else {
        return 0;
    }


}
extern "C"
JNIEXPORT void JNICALL
Java_com_me_harris_audiolib_audioPlayer_AAudioPlayer_nativeDestroyAAudioEngine(JNIEnv *env,
                                                                               jobject thiz,
                                                                               jlong engine_handle) {
    using namespace aaudiodemo;
    LOGI("nativeDestroyAAudioEngine");
    auto playEngine = reinterpret_cast<AAudioEngine *>(engine_handle);
    delete playEngine;
}
extern "C"
JNIEXPORT void JNICALL
Java_com_me_harris_audiolib_audioPlayer_AAudioPlayer_nativeAAudioEnginePlay(JNIEnv *env,
                                                                            jobject thiz,
                                                                            jlong engine_handle) {
    using namespace aaudiodemo;
    LOGI("nativeAAudioEnginePlay");
    auto playEngine = reinterpret_cast<AAudioEngine *>(engine_handle);
    playEngine->Start();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_me_harris_audiolib_audioPlayer_AAudioPlayer_nativeAAudioEnginePause(JNIEnv *env,
                                                                             jobject thiz,
                                                                             jlong engine_handle) {
    using namespace aaudiodemo;
    LOGI("nativeAAudioEnginePause");
    auto playEngine = reinterpret_cast<AAudioEngine *>(engine_handle);
    playEngine->Pause();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_me_harris_audiolib_audioPlayer_AAudioPlayer_nativeAAudioEngineStop(JNIEnv *env,
                                                                            jobject thiz,
                                                                            jlong engine_handle) {
    using namespace aaudiodemo;
    LOGI("nativeAAudioEngineStop");
    auto playEngine = reinterpret_cast<AAudioEngine *>(engine_handle);
    playEngine->Stop();
}