cmake_minimum_required(VERSION 3.6)
project(libavif)

#
#set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -Wno-register")
#set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -Wno-register -Wno-error=register")
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-register -Wno-error=register")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fvisibility=hidden -Wno-error=declaration-after-statement")

add_subdirectory(libavif)

set(SRC_FILES
        avif_decoder_jni.c
        avif_image_jni.c
        util.c
        jni_init.c)



add_library(avif-jni SHARED ${SRC_FILES})

target_link_libraries(avif-jni jnigraphics avif log)

