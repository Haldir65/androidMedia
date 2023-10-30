cmake_minimum_required(VERSION 3.6)
project(avif-jni)

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

#set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wno-error -fvisibility=hidden -Wno-unsafe-buffer-usage")
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fvisibility=hidden -Wno-unsafe-buffer-usage")

set(MY_DEBUG_OPTIONS -Wno-implicit-function-declaration -Wno-error=declaration-after-statement -Wno-unsafe-buffer-usage -Wno-declaration-after-statement -Wno-strict-prototypes -Wno-implicit-function-declaration -Wno-int-conversion)


add_library(${PROJECT_NAME} SHARED ${SRC_FILES})

target_compile_options(${PROJECT_NAME} PRIVATE $<$<COMPILE_LANGUAGE:CXX>:-std=c++17>)
#target_compile_options(${PROJECT_NAME} PRIVATE "$<$<CONFIG:DEBUG>:${MY_DEBUG_OPTIONS}>")
#target_compile_options(${PROJECT_NAME} PRIVATE "$<$<CONFIG:RELEASE>:${MY_DEBUG_OPTIONS}>")

target_compile_options(avif PRIVATE "$<$<CONFIG:DEBUG>:${MY_DEBUG_OPTIONS}>")


target_link_libraries(${PROJECT_NAME} jnigraphics avif log)

