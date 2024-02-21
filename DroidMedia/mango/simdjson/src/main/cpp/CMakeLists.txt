cmake_minimum_required(VERSION 3.6)
project(simd-json)

#
#set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -Wno-register")
#set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -Wno-register -Wno-error=register")
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-register -Wno-error=register")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fvisibility=hidden -Wno-error=declaration-after-statement")


file(GLOB_RECURSE workfile CONFIGURE_DEPENDS
        #        "*.h"
        library/*.cpp
        source/*.cpp
        )

set(SRC_FILES
        JniHandle.cpp
        ${workfile}
        )

#set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wno-error -fvisibility=hidden -Wno-unsafe-buffer-usage")
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fvisibility=hidden -Wno-unsafe-buffer-usage")

set(MY_DEBUG_OPTIONS -Wno-implicit-function-declaration -Wno-error=declaration-after-statement -Wno-unsafe-buffer-usage -Wno-declaration-after-statement -Wno-strict-prototypes -Wno-implicit-function-declaration -Wno-int-conversion)

add_definitions( -DSIMDJSON_IMPLEMENTATION_ARM64=1 )


add_library(${PROJECT_NAME} SHARED
        ${SRC_FILES}
        )

set_target_properties(${PROJECT_NAME} PROPERTIES
        LINKER_LANGUAGE CXX
        CXX_STANDARD 17
        CXX_EXTENSIONS OFF
        POSITION_INDEPENDENT_CODE ON
        )


target_compile_definitions(${PROJECT_NAME} PRIVATE
        SHOW_SCOPR_CRASH=0
        SIMDJSON_IMPLEMENTATION_ICELAKE=0
        SIMDJSON_IMPLEMENTATION_HASWELL=0
        SIMDJSON_IMPLEMENTATION_WESTMERE=0
        SIMDJSON_IMPLEMENTATION_PPC64=0
        )

target_compile_options(${PROJECT_NAME} PRIVATE $<$<COMPILE_LANGUAGE:CXX>:-std=c++17>)
#target_compile_options(${PROJECT_NAME} PRIVATE "$<$<CONFIG:DEBUG>:${MY_DEBUG_OPTIONS}>")
#target_compile_options(${PROJECT_NAME} PRIVATE "$<$<CONFIG:RELEASE>:${MY_DEBUG_OPTIONS}>")

target_compile_options(${PROJECT_NAME} PRIVATE "$<$<CONFIG:DEBUG>:${MY_DEBUG_OPTIONS}>")


target_link_libraries(${PROJECT_NAME} jnigraphics  log)

