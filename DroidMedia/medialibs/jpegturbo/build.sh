#编译参考https://www.jianshu.com/p/20902ca448ae?utm_source=oschina-app
# lib-name
MY_LIBS_NAME=libjpeg-turbo
MY_SOURCE_DIR=$(pwd)/libjpeg-turbo
MY_BUILD_DIR=binary
CMAKE_PATH=${ANDROID_HOME}/cmake/4.0.2
NDK_PATH=${ANDROID_HOME}/ndk/28.0.13004108


export PATH=${CMAKE_PATH}/bin:$PATH


UNAME=$(uname)

if [ "$UNAME" == "Linux" ] ; then
	echo "Linux"
elif [ "$UNAME" == "Darwin" ] ; then
	echo "Darwin"
elif [[ "$UNAME" == CYGWIN* || "$UNAME" == MINGW* ]] ; then
	echo "Windows"
fi


if [ ! -d ${MY_LIBS_NAME} ]; then
  echo "${MY_LIBS_NAME} is an non existing dir"
  git clone https://github.com/libjpeg-turbo/libjpeg-turbo.git --depth=1
fi



if [ ! -d ${CMAKE_PATH} ]; then
  echo "${CMAKE_PATH} is an non existing dir"
  exit -1
fi


if [ ! -d ${NDK_PATH} ]; then
  echo "${NDK_PATH} is an non existing dir"
  exit -1
fi



#BUILD_PLATFORM=linux-x86_64
#TOOLCHAIN_VERSION=4.9
ANDROID_VERSION=24

#-mcpu=cortex-a8  the clang compiler does not support '-mcpu=cortex-a8'
## argument unused during compilation: '-mfpu=neon' Advanced SIMD (aka NEON) is mandatory for AArch64

ANDROID_ARMV5_CFLAGS="-march=armv5te"
ANDROID_ARMV7_CFLAGS="-march=armv7-a -mfloat-abi=softfp -mfpu=neon"  # -mfpu=vfpv3-d16  -fexceptions -frtti
ANDROID_ARMV8_CFLAGS="-march=armv8-a"                   # -mfloat-abi=softfp -mfpu=neon -fexceptions -frtti
ANDROID_X86_CFLAGS="-march=i386 -mtune=intel -mssse3 -mfpmath=sse -m32"
ANDROID_X86_64_CFLAGS="-march=x86-64 -msse4.2 -mpopcnt -m64 -mtune=intel"


##https://github.com/libjpeg-turbo/libjpeg-turbo/blob/main/BUILDING.md
# params($1:arch,$2:arch_abi,$3:host,$4:compiler,$5:cflags,$6:processor)
build_bin() {

    echo "-------------------start build $1-------------------------"

    ANDROID_ARCH_ABI=$1    # armeabi armeabi-v7a x86 mips
    CFALGS="$2"

    PREFIX=$(pwd)/dist/${MY_LIBS_NAME}/${ANDROID_ARCH_ABI}/
    # build 中间件
    BUILD_DIR=./${MY_BUILD_DIR}/${MY_LIBS_NAME}/${ANDROID_ARCH_ABI}

    echo "path==>$PATH"
    echo "build_dir==>$BUILD_DIR"
    echo "ANDROID_ARCH_ABI==>$ANDROID_ARCH_ABI"
    echo "CFALGS==>$CFALGS"


    mkdir -p ${BUILD_DIR}
    cd ${BUILD_DIR}

    # -DCMAKE_MAKE_PROGRAM=${NDK_PATH}/prebuilt/${BUILD_PLATFORM}/bin/make \
    # -DCMAKE_ASM_COMPILER=${NDK_PATH}/prebuilt/${BUILD_PLATFORM}/bin/yasm \

    cmake -G"Unix Makefiles" \
      -DANDROID_ABI=${ANDROID_ARCH_ABI} \
      -DANDROID_PLATFORM=android-${ANDROID_VERSION} \
      -DCMAKE_BUILD_TYPE=Release \
      -DANDROID_NDK=${NDK_PATH} \
      -DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON \
      -DCMAKE_TOOLCHAIN_FILE=${NDK_PATH}/build/cmake/android.toolchain.cmake \
      -DCMAKE_POSITION_INDEPENDENT_CODE=1 \
      -DCMAKE_ASM_FLAGS="--target=aarch64-linux-android${ANDROID_VERSION}" \
      -DCMAKE_INSTALL_PREFIX=${PREFIX} \
      -DANDROID_ARM_NEON=TRUE \
      -DANDROID_ARM_MODE=arm \
      -DANDROID_TOOLCHAIN=clang \
      -DANDROID_STL=c++_shared \
      -DCMAKE_C_FLAGS="${CFALGS} -Os -Wall -pipe -fPIC" \
      -DCMAKE_CXX_FLAGS="${CFALGS} -Os -Wall -pipe -fPIC" \
      -DANDROID_CPP_FEATURES=rtti exceptions \
      -DWITH_JPEG8=1 \
      ${MY_SOURCE_DIR}

    make clean
    make
    make install

    cd ../../../

    echo "-------------------$1 build end-------------------------"
}

_main(){
  # build armeabi
  build_bin armeabi "$ANDROID_ARMV5_CFLAGS"

  #build armeabi-v7a
  build_bin armeabi-v7a "$ANDROID_ARMV7_CFLAGS"

  #build arm64-v8a
  build_bin arm64-v8a "$ANDROID_ARMV8_CFLAGS"

  #build x86
  build_bin x86 "$ANDROID_X86_CFLAGS"

  #build x86_64
  build_bin x86_64 "$ANDROID_X86_64_CFLAGS"
}

_move_outputs_to_jnilib(){
  ANDROID_ARCH_ABI=arm64-v8a
  local dist_dir=$(pwd)/dist/${MY_LIBS_NAME}/${ANDROID_ARCH_ABI}
  mkdir -p src/main/jniLibs/${ANDROID_ARCH_ABI}
  cp -f $dist_dir/lib/libjpeg.so src/main/jniLibs/${ANDROID_ARCH_ABI}/libjpeg.so
  cp -f $dist_dir/lib/libturbojpeg.so src/main/jniLibs/${ANDROID_ARCH_ABI}/libturbojpeg.so
  cp -rf $dist_dir/include src/main/cpp
  echo " moving file completed "
}

build_bin arm64-v8a "$ANDROID_ARMV8_CFLAGS"
_move_outputs_to_jnilib



