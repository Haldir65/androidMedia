#!/bin/bash

API=21

NDK=$ANDROID_HOME/ndk/21.4.7075529
TOOLCHAIN=$NDK/toolchains/llvm/prebuilt/darwin-x86_64
SYSROOT=$TOOLCHAIN/sysroot

export CC=$TOOLCHAIN/bin/aarch64-linux-android${API}-clang
export CFLAGS="-g -DANDROID -fdata-sections -ffunction-sections -funwind-tables -fstack-protector-strong -no-canonical-prefixes -D_FORTIFY_SOURCE=2 -Wformat -Werror=format-security  -O2 -DNDEBUG  -fPIC  --target=aarch64-none-linux-android21 --gcc-toolchain=$TOOLCHAIN "

CPU=arm64-v8a
PREFIX=$(pwd)/android/$CPU

function build()
{
./configure \
--host=aarch64-linux-android \
--prefix=$PREFIX \
--enable-shared \
--enable-static \
--with-zlib=no \
--with-bzip2=no \
--with-png=no \
--with-harfbuzz=no \
--with-sysroot=$SYSROOT \

make -j8
make install
}


FREETYPE_DIR="freetype-2.11.1"

if [ ! -d ${FREETYPE_DIR} ]; then
  echo "${FREETYPE_DIR} is an non existing dir"
  tar -xzvf freetype-2.11.1.tar.gz
fi


cd freetype-2.11.1
build
cd -

## https://download.savannah.gnu.org/releases/freetype/
## https://www.cnblogs.com/freedreamnight/p/14930341.html
