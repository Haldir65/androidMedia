#include <jni.h>
#include "source/AndroidLog.h"
#include "source/SimdJsonUseCase.h"

extern "C"
JNIEXPORT void JNICALL
Java_com_me_harris_simdjson_JsonJni_loadJsonFile(JNIEnv *env, jobject thiz, jstring filepath) {
    const char* path = env->GetStringUTFChars(filepath, nullptr);

    reading_content_of_json_file(std::string{path});

    if (path){
        env->ReleaseStringUTFChars(filepath,path);
    }
}
