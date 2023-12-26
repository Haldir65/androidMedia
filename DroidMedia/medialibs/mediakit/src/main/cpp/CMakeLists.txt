# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_STANDARD 17)

set(VOLAN_SOURCE_DIR volan/CodecANativeWin/jni)

file(GLOB glsource CONFIGURE_DEPENDS
        #        "*.h"
        jni/*.cpp
        jni/*.h
        #        "*.h"
        #        "*.cpp"
        )

file(GLOB volan CONFIGURE_DEPENDS
        #        "*.h"
        ${VOLAN_SOURCE_DIR}/JniBitmap.cpp
        ${VOLAN_SOURCE_DIR}/native_msg.h
        ${VOLAN_SOURCE_DIR}/NativeBuffer.h
        ${VOLAN_SOURCE_DIR}/NativeBuffer.cpp
        ${VOLAN_SOURCE_DIR}/vortex.cpp
        ${VOLAN_SOURCE_DIR}/vortex.h
        ${VOLAN_SOURCE_DIR}/HWH264DecodeAndEncode.cpp
        ${VOLAN_SOURCE_DIR}/HWH264DecodeAndEncode.h
        ${VOLAN_SOURCE_DIR}/tcp_udp/*.c
        ${VOLAN_SOURCE_DIR}/tcp_udp/*.h
        ${VOLAN_SOURCE_DIR}/DeFileAndEnCode.cpp
#        volan/CodecANativeWin/jni/*.cpp
#        volan/CodecANativeWin/jni/*.c
#        volan/CodecANativeWin/jni/*.h
#        volan/CodecANativeWin/jni/tcp_udp/*.h
#        volan/CodecANativeWin/jni/tcp_udp/*.cpp
#        volan/CodecANativeWin/jni/tcp_udp/*.c
#        volan/CodecANativeWin/jni/openGL/*.cpp
#        volan/CodecANativeWin/jni/openGL/*.c
#        volan/CodecANativeWin/jni/openGL/*.h
        #        "*.h"
        #        "*.cpp"
        )

file(GLOB filemap CONFIGURE_DEPENDS
        #        "*.h"
        filemap/*.cpp
        filemap/*.h
        )


file(GLOB media CONFIGURE_DEPENDS
        media/*.cpp
        media/*.h
        #        "*.h"
        #        "*.cpp"
        )

file(GLOB utility CONFIGURE_DEPENDS
        header/*.cpp
        header/*.h
        #        "*.h"
        #        "*.cpp"
        )

add_library(cpufeatures STATIC
        ${ANDROID_NDK}/sources/android/cpufeatures/cpu-features.c)

add_library( # Sets the name of the library.
        gljni

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        MediaKitBlue.cpp
        MediaKitBlue.h

        jni/com_jadyn_mediakit_gl_GLJni.c
        jni/com_jadyn_mediakit_gl_GLJni.h
        ${glsource}
        ${media}
        ${filemap}
        ${utility}
#        ${volan}
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

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        gljni
        cpufeatures
        android
        mediandk
        OpenMAXAL
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib}
        GLESv2
        jnigraphics)
