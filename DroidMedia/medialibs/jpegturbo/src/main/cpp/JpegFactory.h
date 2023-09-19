

#ifndef DROIDMEDIA_JPEGFACTORY_H
#define DROIDMEDIA_JPEGFACTORY_H
#include <string.h>
#include <string_view>

#ifdef __cplusplus
extern "C" {
#endif
#include <jni.h>
#include <sys/stat.h>
#include <limits.h>
#include <stdio.h>
#include <android/bitmap.h>
#ifdef __cplusplus
}
#endif

class JpegFactory{
    static jobject create_bitmap(JNIEnv *env, int width, int height);

};


#endif //DROIDMEDIA_JPEGFACTORY_H
