#pragma once

#ifndef DROIDMEDIA_FILE_FAST_MAP_H
#define DROIDMEDIA_FILE_FAST_MAP_H



#define  FILE_MAP_LOG_TAG    "FileObserver"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_INFO,FILE_MAP_LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,FILE_MAP_LOG_TAG,__VA_ARGS__)


#ifdef __cplusplus
extern "C" {
#endif
#include <sys/mman.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <unistd.h>
#include <jni.h>
#ifdef __cplusplus
}
#endif
#include <cstddef> // template 不能放在extern里面 https://stackoverflow.com/questions/4877705/why-cant-templates-be-within-extern-c-blocks
#include <string>



class FileFastMap {

        private:
        public:

        static const char *fastRead(const char *path);

};


#endif //DROIDMEDIA_FILE_FAST_MAP_H
