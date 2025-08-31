#!/bin/sh

. $(dirname "$0")/functions.sh

# Display all commands before executing them.
set -o errexit
set -o errtrace

WORKING_DIR=`pwd`/build

mkdir -p $WORKING_DIR

function _download_repos(){
    pushd $WORKING_DIR
    git clone https://github.com/androidx/media --depth=1
    pushd media
    AV1_MODULE_PATH="$(pwd)/libraries/decoder_av1/src/main"
    cd "${AV1_MODULE_PATH}/jni" && \
    git clone https://github.com/google/cpu_features

    cd "${AV1_MODULE_PATH}/jni" && \
    git clone https://chromium.googlesource.com/codecs/libgav1

    cd "${AV1_MODULE_PATH}/jni/libgav1" && \
    git clone https://github.com/abseil/abseil-cpp.git third_party/abseil-cpp

    popd

}

function main(){
    _download_repos
    pushd $WORKING_DIR/media
    sed -i 's/gradle-[0-9]\+\.[0-9]\+-[^.]\+\.zip/gradle-9.0.0-all.zip/g' gradle/wrapper/gradle-wrapper.properties
    chmod +x gradlew
    ./gradlew :lib-decoder-av1:assembleRelease
    popd
    mkdir -p dist
    find  -type f -name "*.aar" -exec mv -t dist {} +
    tar -czf av1-decoder-release.tar.gz dist
}

main