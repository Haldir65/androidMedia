

#ifndef DROIDMEDIA_MEDIAKITBLUE_H
#define DROIDMEDIA_MEDIAKITBLUE_H

#include <iostream>
#include <vector>
#include <stdlib.h>
#include <unordered_map>
#include <sstream>
#include <media/NdkMediaCodec.h>
#include <media/NdkMediaFormat.h>
#include <media/NdkMediaExtractor.h>
#include <android/native_window_jni.h> // ANativeWindow

#include "media/MediaInsighter.h"
#include <filesystem>
#include "filemap/file_fast_map.h"


class MediaKitBlue{

public:
    std::string_view probeBasicInfo();

};


#endif //DROIDMEDIA_MEDIAKITBLUE_H
