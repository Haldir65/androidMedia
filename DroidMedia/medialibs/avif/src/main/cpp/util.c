//
// Created by mao on 21-2-18.
//
#include "util.h"
#include "avif/avif.h"
#include "utils/log.h"
#include <stdlib.h>


void jniThrowException(JNIEnv *env, const char *className, const char *msg) {
    jclass clazz = (*env)->FindClass(env, className);
    if (!clazz) {
        ALOGE("Unable to find exception class %s", className);
        /* ClassNotFoundException now pending */
        return;
    }

    if ((*env)->ThrowNew(env, clazz, msg) != JNI_OK) {
        ALOGE("Failed throwing '%s' '%s'", className, msg);
        /* an exception, most likely OOM, will now be pending */
    }
    (*env)->DeleteLocalRef(env, clazz);
}

void throw_IOException(JNIEnv *env, const char *msg) {
    if (msg == NULL) {
        msg = "";
    }
    jniThrowException(env, "java/io/IOException", msg);
}

void print_lib_version() {
    const char* version = avifVersion();
    char outArray[256]  = {0};
    avifCodecVersions(outArray);
    ALOGE("avifVersion =  '%s' ", version); //avifVersion =  '1.0.4'
    ALOGE("avifCodecVersions =  '%s' ", outArray); //avifCodecVersions =  'dav1d [dec]:1.4.0-0-gbb64589'
}


