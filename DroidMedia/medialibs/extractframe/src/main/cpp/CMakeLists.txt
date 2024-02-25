# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS  OFF)

## Q：相比与 Android.mk 方式，CMake 方式编译出的动态库体积很大，怎么办？
#A：编译选项末尾加上 -s，形如： set(CMAKE_C_FLAGS_DEBUG “${cmake_c_flag_debug} -s”)，可去除冗余的二进制数据。
#set(CMAKE_C_FLAGS_DEBUG “${cmake_c_flag_debug} -s”)
# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.


#set(CMAKE_CXX_STANDARD 17)

add_library( # Sets the name of the library.
        yuv-decoder

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        yuv-decoder.c)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

target_compile_options(yuv-decoder PUBLIC $<$<COMPILE_LANGUAGE:CXX>:-std=c++17>)


find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        yuv-decoder

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib}
        GLESv2
        jnigraphics)
