#include <jni.h>
#include <chrono>
#include <thread>
#include "source/AndroidLog.h"
#include "source/SimdJsonUseCase.h"
#include "source/BPThreadPoolUseCase.h"

extern "C"
JNIEXPORT void JNICALL
Java_com_me_harris_simdjson_JsonJni_loadJsonFile(JNIEnv *env, jobject thiz, jstring filepath) {
    const char* path = env->GetStringUTFChars(filepath, nullptr);

    reading_content_of_json_file(std::string{path});

    if (path){
        env->ReleaseStringUTFChars(filepath,path);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_me_harris_simdjson_JsonJni_testCPPThreadPool(JNIEnv *env, jobject thiz, jstring filepath) {
    const char* path = env->GetStringUTFChars(filepath, nullptr);
    BPThreadPoolUseCase p;
    auto num_of_cores = std::thread::hardware_concurrency();
    for (int i = 0; i < 100; ++i) {
        p.scheduleTask([core = num_of_cores](){
            ALOGI("%s ",fmt::format("[thread] {0} core num {1}",thread_naming(),core).c_str());
            std::this_thread::sleep_for(std::chrono::milliseconds(6000));
        });
    }
    p.wait();
    ALOGW("  %s ","completed");
    if (path){
        env->ReleaseStringUTFChars(filepath,path);
    }
}
