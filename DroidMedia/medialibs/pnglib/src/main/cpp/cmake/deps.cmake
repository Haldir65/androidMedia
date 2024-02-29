set(LIB_PNG_VENDER_PARENT_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../../vendor")

IF(EXISTS "${LIB_PNG_VENDER_DIR}" AND IS_DIRECTORY "${LIB_PNG_VENDER_DIR}")
    # 如果目录存在且为目录，则执行相应的操作
    # 你可以在这里添加你需要的逻辑
    message("${LIB_PNG_VENDER_DIR} already exist exist , skip now")
ELSE()
    # 检查build目录是否存在，如果不存在则创建
    message("${LIB_PNG_VENDER_DIR} not exist , create now")
    #    file(MAKE_DIRECTORY ${GML_SOURCE_DIR})
    # 解压a.tar.gz到build目录下
    file(ARCHIVE_EXTRACT INPUT ${LIB_PNG_VENDER_PARENT_DIR}/libpng-1.6.37.tar.gz DESTINATION ${LIB_PNG_VENDER_DIR}/..)
#    include(FetchContent)
#    FetchContent_Declare(
#            glm
#            GIT_REPOSITORY https://github.com/g-truc/glm.git
#            GIT_TAG        7882684a2cd69005fb57001c17a332899621e2be #
#            SOURCE_DIR "${GML_SOURCE_DIR}"
#    )

#    FetchContent_MakeAvailable(glm)

    #    FetchContent_Declare(fmt SOURCE_DIR "${CMAKE_CURRENT_LIST_DIR}/../thirdparty/fmt/")
    #    FetchContent_MakeAvailable(fmt)
    message("${LIB_PNG_VENDER_DIR} not exist , created")
ENDIF()
