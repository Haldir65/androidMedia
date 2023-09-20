
#include "PngHub.h"
#include <jni.h>
#include "media/PngSpoon.h"
#include "media/PngHandle.h"

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
