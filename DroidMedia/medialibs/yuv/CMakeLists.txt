cmake_minimum_required(VERSION 3.21.0)
project(ijksource)

set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_STANDARD 17)

#libyuv
include_directories(${CMAKE_SOURCE_DIR}/libs/libyuv/include)
add_subdirectory(${CMAKE_SOURCE_DIR}/libs/libyuv)

#native-lib
add_library(yuvconvert-lib
        SHARED
        src/main/cpp/yuv-handle.cpp)

find_library( # Sets the name of the path variable.
        log-lib
        log)

target_link_libraries( # Specifies the target library.
        yuvconvert-lib
        yuv
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})
