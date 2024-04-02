//
// Created by cain on 2017/12/28.
//

#ifndef CAINCAMERA_NATIVE_LOG_H
#define CAINCAMERA_NATIVE_LOG_H
#include <android/log.h>
#include <sstream>
#include <fmt/core.h>
#include <fmt/color.h>
#include <fmt/ranges.h>
#include <fmt/chrono.h>

#define DEBUG 1
#define TAG "=A="



#define ALOGE(format, ...) if (DEBUG) { __android_log_print(ANDROID_LOG_ERROR, TAG, format, ##__VA_ARGS__); }
#define ALOGI(format, ...) if (DEBUG) { __android_log_print(ANDROID_LOG_INFO,  TAG, format, ##__VA_ARGS__); }
#define ALOGD(format, ...) if (DEBUG) { __android_log_print(ANDROID_LOG_DEBUG, TAG, format, ##__VA_ARGS__); }
#define ALOGW(format, ...) if (DEBUG) { __android_log_print(ANDROID_LOG_WARN,  TAG, format, ##__VA_ARGS__); }


inline std::string Timestamp()
{
    namespace c = std::chrono;
    auto tp = c::time_point_cast<c::microseconds>(c::system_clock::now());
    auto us = tp.time_since_epoch().count() % std::micro::den;
    return fmt::format("{:%Y-%m-%d_%H:%M:%S}.{:06d}", tp, us);
}

inline std::string thread_naming()
{
    std::stringstream ss;
    ss << std::this_thread::get_id();
    // fmt::print(fmt::fg(fmt::color::yellow), "Thread ID: {0}\n", ss.str());
    std::string a = fmt::format(" Thread id  {0} at {1} \n", ss.str(), Timestamp());
    return a;
}



#endif //CAINCAMERA_NATIVE_LOG_H
