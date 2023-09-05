#include <jni.h>
#include "JpegSpoon.h"
#include "AndroidLog.h"
#include <android/api-level.h>


extern "C"
JNIEXPORT void JNICALL
Java_com_me_harris_libjpeg_JpegSpoon_basic(JNIEnv *env, jobject thiz, jstring string) {
    char* c_str = const_cast<char *>(env->GetStringUTFChars(string, nullptr));
    int api = android_get_application_target_sdk_version();
    int min_api_version = __ANDROID_MIN_SDK_VERSION__;
    ALOGD("android_get_application_target_sdk_version = %d" ,api);
    ALOGD("__ANDROID_MIN_SDK_VERSION__ = %d" ,min_api_version);
    ALOGD("android_get_device_api_level = %d" ,android_get_device_api_level());
    ALOGD("some %s",c_str);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_me_harris_libjpeg_JpegSpoon_compressbitmap(JNIEnv *env, jobject thiz, jobject bitmap,
                                                    jint quality,jstring storageDir, jstring out_file_path,
                                                    jboolean optimize,jboolean turbo) {

    char *storagePath = (char *) env->GetStringUTFChars(storageDir, nullptr);
    JpegSpoon p {env,storagePath} ;
    jint result = p.compressBitmap(env,thiz,bitmap,quality,out_file_path,optimize,turbo);
    std::string a(" result from compressBitmap = ");
    a.append(std::to_string(result));
    ALOGD("some %s",a.c_str());
    return result;
}





