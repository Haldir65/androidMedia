#!/bin/bash

# Display all commands before executing them.
set -o errexit
set -o errtrace

echo "installing depths... skip"

# git clone https://github.com/libjpeg-turbo/libjpeg-turbo.git medialibs/jpegturbo/libjpeg-turbo
# git -C medialibs/jpegturbo/libjpeg-turbo reset --hard "abeca1f0cc638a6492d81f4c3b956c2dec817c3e"

# ls -al medialibs/jpegturbo/libjpeg-turbo

# echo "successfully clone libjepg-turbo..."


## skip , managed by cmake
#git clone https://github.com/g-truc/glm.git medialibs/filterLibrary/src/main/cpp/glm
#git -C medialibs/filterLibrary/src/main/cpp/glm reset --hard "7882684a2cd69005fb57001c17a332899621e2be"
#ls -al medialibs/filterLibrary/src/main/cpp/glm
#echo "successfully clone glm lib .."


# mkdir -p medialibs/pnglib/vendor
#wget https://sourceforge.net/projects/libpng/files/libpng16/1.6.37/libpng-1.6.37.tar.gz -P medialibs/pnglib/vendor
#echo "successfully download libpng lib .. now unzip it..."
# tar -xzvf medialibs/pnglib/vendor/libpng-1.6.37.tar.gz -C medialibs/pnglib/vendor
# echo "show all available  download libpng lib .."
# ls -al medialibs/pnglib/vendor/libpng-1.6.37
