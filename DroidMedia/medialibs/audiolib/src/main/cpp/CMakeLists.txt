# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)

# Declares and names the project.

#set(CMAKE_CXX_STANDARD_REQUIRED ON)
#set(CMAKE_CXX_STANDARD 17)

project("myaudio")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

file(GLOB audio CONFIGURE_DEPENDS
        #        "*.h"
        media/*.cpp
        media/*.h
        opensles/*.h
        opensles/*.cpp
        opensles/*.c
        aaudio/*.cpp
        aaudio/*.h
        oboe/*.h
        oboe/*.cpp
        oboe/audio/*.cpp
        oboe/audio/*.h
        oboe/utils/*.h
        oboe/utils/*.cpp
        )


add_library( # Sets the name of the library.
        ${CMAKE_PROJECT_NAME}

        # Sets the library as a shared library.
        SHARED
        OpenslPalate.cpp
        OpenslPalate.h
        ad_opensl_es.cpp
        aaudio/AAudioJni.cpp

        # Provides a relative path to your source file(s).
        AndroidLog.h
        ${audio}
        )

option(AAUDIO_CALLBACK "option for aaudio process data type , turn on to use callback style in aaudio" ON)
#option(AAUDIO_CALLBACK "option for aaudio process data type" OFF)

if (AAUDIO_CALLBACK)
    add_definitions(-DAAUDIO_CALLBACK)
endif ()

include_directories(${CMAKE_PROJECT_NAME} ${PROJECT_SOURCE_DIR}/include)
#link_directories(../jniLibs/${ANDROID_ABI})

option(USE_FFMPEG "option for using ffmpeg to extract mediadata in aaudio" OFF)


if(${USE_FFMPEG})
    add_definitions(-DUSE_FFMPEG=1)
    MESSAGE(STATUS "Using ffmpeg media extractor")
else()
    MESSAGE(STATUS "Using NDK media extractor")
    add_definitions(-DUSE_FFMPEG=0)
endif()

    target_sources( ${CMAKE_PROJECT_NAME} PRIVATE oboe/audio/FFMpegExtractor.cpp )

    # Add the local path to FFmpeg, you can use the ${ANDROID_ABI} variable to specify the ABI name
    # e.g. /Users/donturner/Code/ffmpeg/build/${ANDROID_ABI}
    get_filename_component(FFMPEG_DIR ../../../../ijksource/src/main/cpp ABSOLUTE)
    message("FFMPEG_DIR = ${FFMPEG_DIR}")

    #include_directories(native-lib ${FFMPEG_DIR}/include)
    include_directories(${CMAKE_PROJECT_NAME} ${FFMPEG_DIR}/ffmpeginclude)
#
#    add_library(avformat SHARED IMPORTED)
#    set_target_properties(avformat PROPERTIES IMPORTED_LOCATION
#            ${FFMPEG_DIR}/libavformat)
#    add_library( avutil SHARED IMPORTED)
#    set_target_properties(avutil PROPERTIES IMPORTED_LOCATION
#            ${FFMPEG_DIR}/libavutil)
#    add_library( avcodec SHARED IMPORTED)
#    set_target_properties(avcodec PROPERTIES IMPORTED_LOCATION
#            ${FFMPEG_DIR}/libavcodec)
#    add_library( swresample SHARED IMPORTED)
#    set_target_properties(swresample PROPERTIES IMPORTED_LOCATION
#            ${FFMPEG_DIR}/libswresample)
#    set (TARGET_LIBS ${TARGET_LIBS} avformat avutil avcodec swresample)

if(NOT TARGET IJKFFMPEG)
    message("adding IJKFFMPEG link")
    add_library(IJKFFMPEG SHARED IMPORTED)
    set_target_properties(IJKFFMPEG PROPERTIES
            IMPORTED_LOCATION "${FFMPEG_DIR}/jniLibs/${ANDROID_ABI}/libijkffmpeg.so"
            INTERFACE_INCLUDE_DIRECTORIES "${FFMPEG_DIR}/ffmpeginclude"
            INTERFACE_LINK_LIBRARIES ""
            )
    target_link_libraries(${CMAKE_PROJECT_NAME} IJKFFMPEG)
endif()
#    add_library(native-lib
#            SHARED
#            audio/FFMpegExtractor.h
#            audio/FFMpegExtractor.cpp)


#    target_sources( native-lib PRIVATE audio/NDKExtractor.cpp )
#    set (TARGET_LIBS ${TARGET_LIBS} mediandk)








# 日志输出在.cxx/cmake/debug//armeabi-v7a/build_output.txt
message("PROJECT_SOURCE_DIR=${PROJECT_SOURCE_DIR}")
message("ANDROID_ABI=${ANDROID_ABI}")
message("CMAKE_ANDROID_ARCH_ABI=${CMAKE_ANDROID_ARCH_ABI}")
message("CMAKE_CURRENT_SOURCE_DIR=${CMAKE_CURRENT_SOURCE_DIR}")
include_directories(${CMAKE_SOURCE_DIR}/include)


set_property(TARGET myaudio PROPERTY C_STANDARD 17)
set_target_properties(myaudio PROPERTIES
        LINKER_LANGUAGE CXX
        CXX_STANDARD 17
        CXX_EXTENSIONS OFF
        POSITION_INDEPENDENT_CODE ON
        )

## https://github.com/google/oboe/blob/main/docs/GettingStarted.md
find_package (oboe REQUIRED CONFIG)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)

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
        ${CMAKE_PROJECT_NAME}

        android
        OpenSLES
        aaudio
        mediandk
        OpenMAXAL
        oboe::oboe
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib}
        ${jnigraphics-lib})

message("log= ${log-lib}")
## 输出log= $HOME/Library/Android/sdk/ndk/25.1.8937393/toolchains/llvm/prebuilt/darwin-x86_64/sysroot/usr/lib/aarch64-linux-android/21/liblog.so
