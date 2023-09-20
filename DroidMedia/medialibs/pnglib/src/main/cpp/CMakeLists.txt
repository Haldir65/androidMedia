# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)

# Declares and names the project.

set(CMAKE_CXX_STANDARD 17)

project("mypng")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.


include_directories(${PROJECT_SOURCE_DIR}/include)
link_directories("${CMAKE_CURRENT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}")

message("link_directories=${CMAKE_CURRENT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}")

message("CMAKE_CURRENT_SOURCE_DIR=${CMAKE_CURRENT_SOURCE_DIR}")


file(GLOB media CONFIGURE_DEPENDS
        #        "*.h"
        media/*.cpp
        media/*.h
        )


# 日志输出在.cxx/cmake/debug/armeabi-v7a/build_output.txt
message("PROJECT_SOURCE_DIR=${PROJECT_SOURCE_DIR}")
message("ANDROID_ABI=${ANDROID_ABI}")
message("CMAKE_ANDROID_ARCH_ABI=${CMAKE_ANDROID_ARCH_ABI}")
message("CMAKE_CURRENT_SOURCE_DIR=${CMAKE_CURRENT_SOURCE_DIR}")
include_directories(${CMAKE_SOURCE_DIR}/include)


set(LIB_PNG_VENDER_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../../vendor/libpng-1.6.37")
message("LIB_PNG_VENDER_DIR=${LIB_PNG_VENDER_DIR}")


# OUR SOURCES
set(libpng_public_hdrs
        "${LIB_PNG_VENDER_DIR}/pnglibconf.h"
        "${LIB_PNG_VENDER_DIR}/png.h"
        "${LIB_PNG_VENDER_DIR}/pngconf.h"
        )

include_directories(${LIB_PNG_VENDER_DIR})

message("include_directories = ${LIB_PNG_VENDER_DIR}")

add_library( # Sets the name of the library.
        mypng

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        PngHub.cpp
        PngHub.h
        AndroidLog.h
        ${media}
        )



# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib
        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

find_library(
        jnigraphics-lib
        jnigraphics)



target_link_libraries( # Specifies the target library.
        mypng
        png
        z
        android

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib}
        ${jnigraphics-lib})

message("log= ${log-lib}")
## 输出log= $HOME/Library/Android/sdk/ndk/25.1.8937393/toolchains/llvm/prebuilt/darwin-x86_64/sysroot/usr/lib/aarch64-linux-android/21/liblog.so
