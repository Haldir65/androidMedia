
#include "PngHub.h"
#include <jni.h>
#include "media/PngSpoon.h"
#include "media/PngHandle.h"
#include "media/PngHelper.h"
#include "AndroidLog.h"
#include <cstring>
#include <string.h>

extern "C"
JNIEXPORT jint JNICALL
Java_com_me_harris_pnglib_PngSpoon_probeFileInfo(JNIEnv *env, jobject thiz, jstring png_file_path) {

    char *filepath = (char *) env->GetStringUTFChars(png_file_path, nullptr);
    PngHandle p ;
    jint result = p.probePngFileInfo(std::string{filepath});
    std::string a(" result from probePngFileInfo = ");
    a.append(std::to_string(result));
    ALOGD("probe png %s",a.c_str());
    return result;
}



extern "C"
JNIEXPORT jint JNICALL
Java_com_me_harris_pnglib_PngSpoon_getPngWidth(JNIEnv *env, jobject thiz, jstring png_filepath) {
    char *filepath = (char *) env->GetStringUTFChars(png_filepath, nullptr);
    if (!filesystem::exists(std::string_view{filepath})) {
        ALOGE("file %s not exists ",filepath);
        return -1;
    }
    PngHelper helper { std::string {filepath}};
    int width = helper.getWidth();
    return width;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_me_harris_pnglib_PngSpoon_getPngHeight(JNIEnv *env, jobject thiz, jstring png_filepath) {
    char *filepath = (char *) env->GetStringUTFChars(png_filepath, nullptr);

    if (!filesystem::exists(std::string_view{filepath})) {
        ALOGE("file %s not exists ",filepath);
        return -1;
    }
    PngHelper helper { std::string {filepath}};
    int height = helper.getHeight();
    return height;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_me_harris_pnglib_PngSpoon_decodePngToDirectBuffer(JNIEnv *env, jobject thiz,
                                                           jstring png_file_path, jobject buffer) {
    char *filepath = (char *) env->GetStringUTFChars(png_file_path, nullptr);

    if (!filesystem::exists(std::string_view{filepath})) {
        ALOGE("file %s not exists ",filepath);
        return -1;
    }
    void* bufferaddress = env->GetDirectBufferAddress(buffer);
    PngHelper helper { std::string {filepath}};
    void* pixelData = static_cast<void*>(helper.getPixelData());
    memcpy(bufferaddress, pixelData, helper.sizeOfPixels);
    return 0;

}
extern "C"
JNIEXPORT jint JNICALL
Java_com_me_harris_pnglib_PngSpoon_compressBitmapToPngFile(JNIEnv *env, jobject thiz,
                                                           jstring destfile, jobject buffer,jint width,jint height) {

    char *filepath = (char *) env->GetStringUTFChars(destfile, nullptr);

//    if (!filesystem::exists(std::string_view{filepath})) {
//        ALOGE("file %s not exists ",filepath);
//        return false;
//    }
    void* bufferaddress = env->GetDirectBufferAddress(buffer);
    unsigned long bufferSize = width *height * 4;
    unsigned char* rgbaBuffer = static_cast<unsigned char *> (new unsigned char[bufferSize]);
    memcpy(rgbaBuffer, bufferaddress, bufferSize);
    // pnghelper save rgba to png file?
    PngHandle handle;
    std::string fpp = std::string{filepath};
    handle.saveRgbaBufferToPngFile(fpp, rgbaBuffer, width, height);
    delete[] rgbaBuffer;
    return -1;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_me_harris_pnglib_PngSpoon_pngHasAlpha(JNIEnv *env, jobject thiz, jstring png_filepath) {
    char *filepath = (char *) env->GetStringUTFChars(png_filepath, nullptr);

    if (!filesystem::exists(std::string_view{filepath})) {
        ALOGE("file %s not exists ",filepath);
        return false;
    }
    PngHelper helper { std::string {filepath}};
    return helper.has_alpha();
}
