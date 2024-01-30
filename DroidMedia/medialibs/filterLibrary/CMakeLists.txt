
cmake_minimum_required(VERSION 3.21.0)

set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_STANDARD 17)


set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -Wno-register")
set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -Wno-register -Wno-error=register")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-register -Wno-error=register")

## https://www.youtube.com/watch?v=GIGHalVqSBE&t=337s
LIST(APPEND CMAKE_MODULE_PATH ${CMAKE_CURRENT_SOURCE_DIR}/cmake)
#set (CMAKE_MODULE_PATH "${CMAKE_MODULE_PATH};${CMAKE_CURRENT_SOURCE_DIR}/cmake")
include(fmt) # defines a macro:


## add all file with certain extension to a collectio,
# then call add_library will be easier
## https://stackoverflow.com/a/3201211
file(GLOB cain_filters CONFIGURE_DEPENDS
#        "*.h"
        src/main/cpp/cainfilter/*.cpp
        src/main/cpp/cainfilter/adjust/*.cpp
        src/main/cpp/cainfilter/input/*.cpp
        src/main/cpp/cainfilter/*.h
        src/main/cpp/caingles/*.cpp
        src/main/cpp/caingles/*.h
        src/main/cpp/cainrender/*.cpp
        src/main/cpp/cainrender/*.h
        #        "*.h"
#        "*.cpp"
        )


file(GLOB gl_filters CONFIGURE_DEPENDS
        #        "*.h"
        src/main/cpp/gl/*.cpp
        src/main/cpp/gl/*.h
        #        "*.h"
        #        "*.cpp"
        )


include_directories(${CMAKE_SOURCE_DIR}/glm)
include_directories(${CMAKE_SOURCE_DIR}/glm/glm)


message("include_directories1= ${CMAKE_SOURCE_DIR}/src/main/cpp/freetypes/${ANDROID_ABI}/include/freetype2")
message("include_directories2= ${CMAKE_SOURCE_DIR}/src/main/cpp/freetypes/${ANDROID_ABI}/include/freetype2/freetype")


add_library(
            nativefilter

             SHARED

             # common
             src/main/cpp/common/Point.cpp
             src/main/cpp/common/Looper.cpp
             src/main/cpp/common/MyLooper.cpp
             src/main/cpp/common/native_log.h
             src/main/cpp/common/Point.cpp
             # utils
             src/main/cpp/nativefilter/FilterUtils.cpp
             src/main/cpp/nativefilter/BlendFilter.cpp
             # ndk filter
             src/main/cpp/nativefilter/BlackWhiteFilter.cpp
             src/main/cpp/nativefilter/BrightContrastFilter.cpp
             src/main/cpp/nativefilter/ColorQuantizeFilter.cpp
             src/main/cpp/nativefilter/GaussianBlurFilter.cpp
             src/main/cpp/nativefilter/HistogramEqualFilter.cpp
             src/main/cpp/nativefilter/ImageFilter.cpp
             src/main/cpp/nativefilter/InvertFilter.cpp
             src/main/cpp/nativefilter/LookupTableFilter.cpp
             src/main/cpp/nativefilter/MosaicFilter.cpp
             src/main/cpp/nativefilter/ShiftFilter.cpp
             src/main/cpp/nativefilter/StackBlurFilter.cpp
             src/main/cpp/nativefilter/VignetteFilter.cpp


             ${cain_filters}
             ${gl_filters}
             # jni interface
             src/main/cpp/ImageFilterController.cpp
             src/main/cpp/freetypes/TextRenderSample.h
             src/main/cpp/freetypes/TextRenderSample.cpp
             src/main/cpp/NativeEglController.cpp)

find_library( EGL-lib EGL )
find_library( GLESv2-lib GLESv2 )
find_library( GLESv3-lib GLESv3 )
find_library( log-lib log )


add_library( freetype SHARED IMPORTED )
set_target_properties( freetype PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/src/main/cpp/freetypes/${ANDROID_ABI}/lib/libfreetype.so )
target_include_directories(nativefilter PRIVATE
        ${CMAKE_SOURCE_DIR}/src/main/cpp/freetypes/${ANDROID_ABI}/include/freetype2
        ${CMAKE_SOURCE_DIR}/src/main/cpp/freetypes/${ANDROID_ABI}/include/freetype2/freetype
        )


target_link_libraries(
        nativefilter
        freetype
       -ljnigraphics
        android

        ${log-lib}
        ${EGL-lib}
        ${GLESv3-lib}
        ${GLESv2-lib}
                      )
