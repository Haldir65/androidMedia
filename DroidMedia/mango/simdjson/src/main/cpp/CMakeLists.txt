cmake_minimum_required(VERSION 3.21.0)
project(simd-json)

#
#set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -Wno-register")
#set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -Wno-register -Wno-error=register")
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-register -Wno-error=register")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fvisibility=hidden -Wno-error=declaration-after-statement")


file(GLOB_RECURSE workfile CONFIGURE_DEPENDS
        #        "*.h"
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

include(cmake/CPM.cmake)
CPMAddPackage(
  NAME     simdjson
  URL      https://github.com/simdjson/simdjson/archive/refs/tags/v4.0.7.tar.gz
  URL_HASH SHA256=d2d15490605858d3dd42e90d25e0fde31c53446b7d3cde9ef334449236927916
)

CPMAddPackage(
        NAME     fmt
        URL      https://github.com/fmtlib/fmt/archive/refs/tags/12.0.0.tar.gz
        URL_HASH SHA256=aa3e8fbb6a0066c03454434add1f1fc23299e85758ceec0d7d2d974431481e40
)

CPMAddPackage(
        NAME BS_thread_pool
        URL https://github.com/bshoshany/thread-pool/archive/refs/tags/v4.1.0.tar.gz
        URL_HASH SHA256=be7abecbc420bb87919eeef729b13ff7c29d5ce547bdae284923296c695415bd
)

add_library(BS_thread_pool INTERFACE)
target_include_directories(BS_thread_pool INTERFACE ${BS_thread_pool_SOURCE_DIR}/include)

target_link_libraries(${PROJECT_NAME} PRIVATE
        jnigraphics
        log
        )

if(BS_thread_pool_ADDED)
    target_link_libraries(${PROJECT_NAME} PRIVATE
            BS_thread_pool
    )
endif ()


if(fmt_ADDED)
    target_link_libraries(${PROJECT_NAME} PRIVATE
            fmt::fmt
    )
endif ()

if(simdjson_ADDED)
        message("simdjson_ADDED = " ${simdjson_ADDED})
        set_target_properties(simdjson PROPERTIES CXX_STANDARD 17)
        target_link_libraries(${PROJECT_NAME} PRIVATE
        simdjson
        )
endif()


