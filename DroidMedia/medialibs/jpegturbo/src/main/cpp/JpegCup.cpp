#include <jni.h>
#include "JpegSpoon.h"
#include "AndroidLog.h"


extern "C"
JNIEXPORT void JNICALL
Java_com_me_harris_libjpeg_JpegSpoon_basic(JNIEnv *env, jobject thiz, jstring string) {
    char* c_str = const_cast<char *>(env->GetStringUTFChars(string, nullptr));
    ALOGD("some %s",c_str);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_me_harris_libjpeg_JpegSpoon_compressbitmap(JNIEnv *env, jobject thiz, jobject bitmap,
                                                    jint quality, jstring out_file_path,
                                                    jboolean optimize) {

    JpegSpoon p;
    jint result = p.compressBitmap(env,thiz,bitmap,quality,out_file_path,optimize);
    std::string a(" result from compressBitmap = ");
    a.append(std::to_string(result));
    ALOGD("some %s",a.c_str());
    return result;
}





