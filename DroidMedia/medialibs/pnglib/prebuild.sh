#编译参考https://www.jianshu.com/p/20902ca448ae?utm_source=oschina-app
# lib-name
MY_LIBS_NAME="libpng"
LIB_VERSION="1.6.37"
VENDOR_DIR="vendor"
LIB_FULL_NAME="${MY_LIBS_NAME}-${LIB_VERSION}"
MY_SOURCE_DIR=$(pwd)/libjpeg-turbo
MY_BUILD_DIR=binary
CMAKE_PATH=${ANDROID_HOME}/cmake/3.10.2.4988404
NDK_PATH=${ANDROID_HOME}/ndk/21.4.7075529


export PATH=${CMAKE_PATH}/bin:$PATH


UNAME=$(uname)

if [ "$UNAME" == "Linux" ] ; then
	echo "Linux"
elif [ "$UNAME" == "Darwin" ] ; then
	echo "Darwin"
elif [[ "$UNAME" == CYGWIN* || "$UNAME" == MINGW* ]] ; then
	echo "Windows"
fi

if [ ! -d  ${VENDOR_DIR} ];then
  echo "${VENDOR_DIR} not exists , mkdir now"
  mkdir -p ${VENDOR_DIR}
fi


if [ ! -d  ${VENDOR_DIR} ];then
  exit -1
fi

if [ ! -f ${VENDOR_DIR}/${LIB_FULL_NAME}];then
    echo "download https://sourceforge.net/projects/libpng/files/libpng16/1.6.37/libpng-1.6.37.tar.gz/download "
    wget https://sourceforge.net/projects/libpng/files/libpng16/1.6.37/libpng-1.6.37.tar.gz/download -o libpng-1.6.37.tar.gz
fi

if [ ! -f ${VENDOR_DIR}/${LIB_FULL_NAME}];then
  echo "failed to download ${LIB_FULL_NAME} ? "
  exit -1
else
  cd $VENDOR_DIR
  tar -xzvf libpng-1.6.37.tar.gz

fi





