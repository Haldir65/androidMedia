#!/bin/sh

. $(dirname "$0")/functions.sh

# Display all commands before executing them.
set -o errexit
set -o errtrace


function _prepare_for_macos(){
  export CORES=$((`sysctl -n hw.logicalcpu`+1))
  export HOST_TAG=darwin-x86_64 # Same tag for Apple Silicon. Other OS values here: https://developer.android.com/ndk/guides/other_build_systems#overview
  _green "macos , num of cores =  $CORES \n"
}


function _prepare_for_linux(){
  export CORES=$((`nproc`+1))
  export HOST_TAG=linux-x86_64 # Same tag for Apple Silicon. Other OS values here: https://developer.android.com/ndk/guides/other_build_systems#overview
  _green "linux , num of cores =  $CORES \n"
}


function _prepare_for_windows(){
  export CORES=$NUMBER_OF_PROCESSORS
  export HOST_TAG=windows-x86_64 # Same tag for Apple Silicon. Other OS values here: https://developer.android.com/ndk/guides/other_build_systems#overview
  _green "windows , num of cores =  $CORES \n"
}


function _prepare(){

  case "$OSTYPE" in
  solaris*) echo "SOLARIS" ;;
  darwin*)
    _prepare_for_macos
    ;;
  linux*)
    _prepare_for_linux
    ;;
  bsd*) 
      echo "BSD" ;;
  msys*)  
    _prepare_for_windows
    echo "WINDOWS msys" 
    ;;
  cygwin*) 
    echo "WINDOWS cygwin" ;;
  *)        
  echo "unknown: $OSTYPE" ;;
esac


  # export ANDROID_NDK_HOME=$ANDROID_HOME/ndk/26.3.11579264 # Point into your NDK.
  export MIN_SDK_VERSION=23
  export TARGET_HOST=aarch64-linux-android
  export ANDROID_ARCH=arm64-v8a



  if [ -d "$ANDROID_NDK_HOME" ]; then
      _green "ndk directory $ANDROID_NDK_HOME exists \n"
  else
      _red "ndk directory $ANDROID_NDK_HOME not  exists ,abort  \n"
      exit 1 
  fi

  export ANDROID_NDK_ROOT=$ANDROID_NDK_HOME
  export NDK=$ANDROID_NDK_HOME

  export TOOLCHAIN=$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/$HOST_TAG

  if [ -d "$TOOLCHAIN" ]; then
    _green "ndk toolchain exists \n"
  else
      _red "ndk toolchain not  exists ,abort  \n"
      exit 1 
  fi

  export AS=$TOOLCHAIN/bin/llvm-as
  export CC=$TOOLCHAIN/bin/aarch64-linux-android${MIN_SDK_VERSION}-clang
  export CXX=$TOOLCHAIN/bin/aarch64-linux-android${MIN_SDK_VERSION}-clang++
  export LD=$TOOLCHAIN/bin/ld
  export AR=$TOOLCHAIN/bin/llvm-ar
  export RANLIB=$TOOLCHAIN/bin/llvm-ranlib
  export READELF=$TOOLCHAIN/bin/llvm-readelf
  export STRIP=$TOOLCHAIN/bin/llvm-strip


  ##export NDK=your_android_ndk_root_here # e.g. $HOME/Library/Android/sdk/ndk/23.0.7599858
  #export HOST_TAG=see_this_table_for_info # e.g. darwin-x86_64, see https://developer.android.com/ndk/guides/other_build_systems#overview
  #export MIN_SDK_VERSION=23 # or any version you want

  PATH=$TOOLCHAIN/bin:$PATH



  export build_dir=$PWD/build
  export install_dir=$PWD/libs_prebuilt
  export SSL_DIR=$install_dir/openssl/$ANDROID_ARCH
  export CURL_DIR=$install_dir/curl/$ANDROID_ARCH

  mkdir -p ${build_dir} 
  mkdir -p ${install_dir} 
  mkdir -p ${SSL_DIR} 
  mkdir -p ${CURL_DIR} 

}

function _build_openssl(){
  _download_if_not_exists https://github.com/openssl/openssl/releases/download/openssl-3.3.0/openssl-3.3.0.tar.gz openssl-3.3.0.tar.gz
  tar -xzvf openssl-3.3.0.tar.gz -C ${build_dir}
  pushd ${build_dir}/openssl-3.3.0
  # arm64
#  export CC=$TOOLCHAIN/bin/$TARGET_HOST$MIN_SDK_VERSION-clang
#  export AS=$CC
#  export CXX=$TOOLCHAIN/bin/$TARGET_HOST$MIN_SDK_VERSION-clang++
#  export LD=$TOOLCHAIN/bin/ld
#  export RANLIB=$TOOLCHAIN/bin/llvm-ranlib
#  export STRIP=$TOOLCHAIN/bin/llvm-strip

  local _prefix=${build_dir}/openssl
  mkdir -p $_prefix
  ./Configure android-arm64 no-shared \
   -D__ANDROID_API__=$MIN_SDK_VERSION \
   --prefix=$_prefix

  make -j$CORES
  make install_sw
  make clean
  cp -R $_prefix $SSL_DIR
  _orange "build artificate located at $SSL_DIR \n"
  popd
  rm -rf ${build_dir}/openssl-3.3.0
}

function _build_curl() {
  _green "_build_curl begin \n"
  # curl common configuration arguments
  # disable functionalities here to reduce size
  ARGUMENTS=" \
      --with-pic \
      --disable-shared \
      --enable-ipv6 \
      --enable-static \
      --enable-threaded-resolver \
      --disable-dict \
      --disable-gopher \
      --disable-ldap --disable-ldaps \
      --disable-manual \
      --disable-pop3 --disable-smtp --disable-imap \
      --disable-rtsp \
      --disable-smb \
      --disable-telnet \
      --disable-verbose \
      "
  _download_if_not_exists https://github.com/curl/curl/releases/download/curl-8_10_1/curl-8.10.1.tar.gz curl-8.10.1.tar.gz
  tar -xzvf curl-8.10.1.tar.gz -C ${build_dir}
  pushd ${build_dir}/curl-8.10.1

  local _prefix=${build_dir}/curl
  mkdir -p $_prefix

  ./configure --host=$TARGET_HOST \
              --target=$TARGET_HOST \
              --target=$TARGET_HOST \
              --prefix=$_prefix \
              --with-openssl=$SSL_DIR/openssl $ARGUMENTS

  make -j$CORES
  make install
  make clean
  mkdir -p ${CURL_DIR}
  rm -rf  $_prefix/share
  cp -R $_prefix $CURL_DIR
  _green "_build_curl completed \n"
  popd
  rm -rf ${build_dir}/curl-8.10.1

}

# arm
#_compile "armeabi" "arm-linux-androideabi" "-mthumb -D__ANDROID_API__=20" "" "arm"
# armv7
#_compile "armeabi-v7a" "arm-linux-androideabi" "-march=armv7-a -mfloat-abi=softfp -mfpu=vfpv3-d16" "-march=armv7-a -Wl,--fix-cortex-a8" "arm"
# arm64v8, maybe should compile with a lower ndk
#_compile "arm64-v8a" "aarch64-linux-android" "" "" "arm64"
# x86
#_compile "x86" "i686-linux-android" "-march=i686 -m32 -msse3 -mstackrealign -mfpmath=sse -mtune=intel" "" "x86"
# x86_64
#_compile "x86_64" "x86_64-linux-android" "-march=x86-64 -m64 -msse4.2 -mpopcnt  -mtune=intel" "" "x86_64"
# mips
#_compile "mips" "mipsel-linux-android" "" "" "mips"
# mips64
#_compile "mips64" "mips64el-linux-android" "" "" "mips64"


#echo "done"


function _show_optputs(){
    _green "show layouts after build completed \n"
    OS=$(uname)
    if [[ "$OS" == "Linux" || "$OS" == "Darwin" ]]; then
       tree -L 4 $SSL_DIR
       tree -L 4 $CURL_DIR
    elif [[ "$OS" == "Windows" ]]; then
        _green "widnows"
    else
        echo "Unknown system"
    fi
    _purple "examine deps of openssl \n $SSL_DIR/openssl/bin/openssl \n"
    $READELF -a $SSL_DIR/openssl/bin/openssl | grep "NEEDED"

    _blue "examine deps of curl \n"
    $READELF -a $CURL_DIR/curl/bin/curl | grep "NEEDED"
}

function _zip_outputs(){
    _blue "zip outputs \n"
    zip -r curl-prebuilt.zip $install_dir
    _blue "zip outputs done \n"
}

main(){
#  
  _prepare
#  echo "[step 1 ] make stand alone tool chain completed"
  _build_openssl
#  echo "[step 2 ] _build_openssl completed"
  _build_curl
# #  echo "[step 3 ] _build_curl completed"
  _show_optputs

  _zip_outputs

}

main
