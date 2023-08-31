

#include "MediaKitBlue.h"
#include <jni.h>

extern "C"
JNIEXPORT void JNICALL
Java_com_jadyn_mediakit_native_MediaKitJNI_mediakitProbeInfo(JNIEnv *env, jobject thiz,
                                                             jstring filepath) {
    MediaInsighter sight;
    char *c_str = const_cast<char *>(env->GetStringUTFChars(filepath, nullptr));
    std::string fullName(c_str);
    std::string fullInfo = sight.probeBasicInfo(std::string(c_str));
    std::ostringstream stringStream;
    size_t lastindex = fullName.find_last_of(".");
    if (lastindex != std::string::npos) {
        std::string filenameWithoutExtension = fullName.substr(0, lastindex);
        stringStream << filenameWithoutExtension << " = /\n";
    }
    stringStream << fullName << "/\n";
    ALOGW("mediaformat info of file %s is %s", c_str, stringStream.str().c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_jadyn_mediakit_native_MediaKitJNI_mediakitExtractFrame(JNIEnv *env, jobject thiz,
                                                                jstring filepath,
                                                                jstring storage_dir,
                                                                jlong gap_in_between_seconds) {
    // TODO: implement mediakitExtractFrame()
}

std::string_view MediaKitBlue::probeBasicInfo() {
    return std::string_view("");
}
