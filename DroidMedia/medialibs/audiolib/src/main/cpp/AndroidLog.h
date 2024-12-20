//
// Created by cain on 2017/12/28.
//
#pragma once

#ifndef CAINCAMERA_NATIVE_LOG_H
#define CAINCAMERA_NATIVE_LOG_H
#include <android/log.h>

#define DEBUG 1
#define TAG "=A="

#define ALOGE(format, ...) if (DEBUG) { __android_log_print(ANDROID_LOG_ERROR, TAG, format, ##__VA_ARGS__); }
#define ALOGI(format, ...) if (DEBUG) { __android_log_print(ANDROID_LOG_INFO,  TAG, format, ##__VA_ARGS__); }
#define ALOGD(format, ...) if (DEBUG) { __android_log_print(ANDROID_LOG_DEBUG, TAG, format, ##__VA_ARGS__); }
#define ALOGW(format, ...) if (DEBUG) { __android_log_print(ANDROID_LOG_WARN,  TAG, format, ##__VA_ARGS__); }


#define LOGD(...) ALOGD(__VA_ARGS__)
#define LOGI(...) ALOGI(__VA_ARGS__)
#define LOGW(...) ALOGW(__VA_ARGS__)
#define LOGE(...) ALOGE(__VA_ARGS__)

#endif //CAINCAMERA_NATIVE_LOG_H

