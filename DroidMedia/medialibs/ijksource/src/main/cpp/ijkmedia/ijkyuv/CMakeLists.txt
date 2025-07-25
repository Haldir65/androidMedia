cmake_minimum_required(VERSION 3.21.0)
project(ijkyuv)
# CMakeLists for libyuv
# Originally created for "roxlu build system" to compile libyuv on windows
# Run with -DTEST=ON to build unit tests
option(TEST "Built unit tests" OFF)

set(ly_base_dir ${CMAKE_CURRENT_LIST_DIR})
set(ly_src_dir ${ly_base_dir}/source/)
set(ly_inc_dir ${ly_base_dir}/include)
set(ly_lib_name "yuv")

set(ly_source_files
  ${ly_src_dir}/compare.cc
  ${ly_src_dir}/compare_common.cc
  ${ly_src_dir}/compare_neon.cc
  ${ly_src_dir}/compare_posix.cc
  ${ly_src_dir}/compare_win.cc
  ${ly_src_dir}/convert.cc
  ${ly_src_dir}/convert_argb.cc
  ${ly_src_dir}/convert_from.cc
  ${ly_src_dir}/convert_from_argb.cc
  ${ly_src_dir}/convert_jpeg.cc
  ${ly_src_dir}/convert_to_argb.cc
  ${ly_src_dir}/convert_to_i420.cc
  ${ly_src_dir}/cpu_id.cc
  ${ly_src_dir}/format_conversion.cc
  ${ly_src_dir}/mjpeg_decoder.cc
  ${ly_src_dir}/mjpeg_validate.cc
  ${ly_src_dir}/planar_functions.cc
  ${ly_src_dir}/rotate.cc
  ${ly_src_dir}/rotate_argb.cc
  ${ly_src_dir}/rotate_mips.cc
  ${ly_src_dir}/rotate_neon.cc
  ${ly_src_dir}/row_any.cc
  ${ly_src_dir}/row_common.cc
  ${ly_src_dir}/row_mips.cc
  ${ly_src_dir}/row_neon.cc
  ${ly_src_dir}/row_posix.cc
  ${ly_src_dir}/row_win.cc
  ${ly_src_dir}/scale.cc
  ${ly_src_dir}/scale_argb.cc
  ${ly_src_dir}/scale_common.cc
  ${ly_src_dir}/scale_mips.cc
  ${ly_src_dir}/scale_neon.cc
  ${ly_src_dir}/scale_posix.cc
  ${ly_src_dir}/scale_win.cc
  ${ly_src_dir}/video_common.cc
)

set(ly_unittest_sources
  ${ly_base_dir}/unit_test/basictypes_test.cc
  ${ly_base_dir}/unit_test/compare_test.cc
  ${ly_base_dir}/unit_test/convert_test.cc
  ${ly_base_dir}/unit_test/cpu_test.cc
  ${ly_base_dir}/unit_test/math_test.cc
  ${ly_base_dir}/unit_test/planar_test.cc
  ${ly_base_dir}/unit_test/rotate_argb_test.cc
  ${ly_base_dir}/unit_test/rotate_test.cc
  ${ly_base_dir}/unit_test/scale_argb_test.cc
  ${ly_base_dir}/unit_test/scale_test.cc
  ${ly_base_dir}/unit_test/unit_test.cc
  ${ly_base_dir}/unit_test/video_common_test.cc
  ${ly_base_dir}/unit_test/version_test.cc
)

set(ly_header_files
  ${ly_inc_dir}/libyuv/basic_types.h
  ${ly_inc_dir}/libyuv/compare.h
  ${ly_inc_dir}/libyuv/convert.h
  ${ly_inc_dir}/libyuv/convert_argb.h
  ${ly_inc_dir}/libyuv/convert_from.h
  ${ly_inc_dir}/libyuv/convert_from_argb.h
  ${ly_inc_dir}/libyuv/cpu_id.h
  ${ly_inc_dir}/libyuv/format_conversion.h
  ${ly_inc_dir}/libyuv/planar_functions.h
  ${ly_inc_dir}/libyuv/rotate.h
  ${ly_inc_dir}/libyuv/rotate_argb.h
  ${ly_inc_dir}/libyuv/row.h
  ${ly_inc_dir}/libyuv/scale.h
  ${ly_inc_dir}/libyuv/scale_argb.h
  ${ly_inc_dir}/libyuv/scale_row.h
  ${ly_inc_dir}/libyuv/version.h
  ${ly_inc_dir}/libyuv/video_common.h
  ${ly_inc_dir}/libyuv/mjpeg_decoder.h
)

include_directories(${ly_inc_dir})

add_library(${ly_lib_name} STATIC ${ly_source_files})

add_executable(convert ${ly_base_dir}/util/convert.cc)
target_link_libraries(convert ${ly_lib_name})

include(FindJPEG)
if (JPEG_FOUND)
  include_directories(${JPEG_INCLUDE_DIR})
  target_link_libraries(convert ${JPEG_LIBRARY})
  add_definitions(-DHAVE_JPEG)
endif()

if(TEST)
  find_library(GTEST_LIBRARY gtest)
  if(GTEST_LIBRARY STREQUAL "GTEST_LIBRARY-NOTFOUND")
    set(GTEST_SRC_DIR /usr/src/gtest)
    if(EXISTS ${GTEST_SRC_DIR}/src/gtest-all.cc)
      message(STATUS "building gtest from sources in ${GTEST_SRC_DIR}")
      set(gtest_sources ${GTEST_SRC_DIR}/src/gtest-all.cc)
      add_library(gtest STATIC ${gtest_sources})
      include_directories(${GTEST_SRC_DIR})
      set(GTEST_LIBRARY gtest)
    else()
      message(FATAL_ERROR "TEST is set but unable to find gtest library")
    endif()
  endif()

  add_executable(libyuv_unittest ${ly_unittest_sources})
  target_link_libraries(libyuv_unittest ${ly_lib_name} ${GTEST_LIBRARY} pthread)
  if (JPEG_FOUND)
    target_link_libraries(libyuv_unittest ${JPEG_LIBRARY})
  endif()
endif()

if(NACL AND NACL_LIBC STREQUAL "newlib")
  target_link_libraries(libyuv_unittest glibc-compat)
endif()

install(TARGETS ${ly_lib_name} DESTINATION lib)
install(FILES ${ly_header_files} DESTINATION include/libyuv)
install(FILES ${ly_inc_dir}/libyuv.h DESTINATION include/)
