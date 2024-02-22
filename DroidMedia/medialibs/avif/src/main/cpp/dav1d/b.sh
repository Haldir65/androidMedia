
#ndk path
# export ANDROID_NDK_HOME=/opt/android-sdk/ndk/23.1.7779620
echo "ANDROID_NDK_HOME = ${ANDROID_NDK_HOME} "
echo "========= really?=========="
tree -L ${ANDROID_NDK_HOME}

CROSS_FILE="$1"
ABI="$2"
PREFIX="$3"

# Add toolchains bin directory to PATH
export PATH=${ANDROID_NDK_HOME}/toolchains/llvm/prebuilt/linux-x86_64/bin:$PATH

OUT=out/build-$ABI

git clone -b 1.4.0 --depth 1 https://code.videolan.org/videolan/dav1d.git dav1d-src

meson --buildtype release \
    --cross-file $CROSS_FILE \
    --default-library=static  \
    --prefix $PREFIX \
    -Denable_tools=false -Denable_tests=false \
    $OUT  dav1d-src

cd $OUT
ninja install
