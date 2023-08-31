#pragma once

#ifndef DROIDMEDIA_MEDIAINSIGHTER_H
#define DROIDMEDIA_MEDIAINSIGHTER_H

#include <media/NdkMediaCodec.h>
#include <media/NdkMediaExtractor.h>
#include <media/NdkMediaFormat.h>
#include <iostream>

#ifdef __cplusplus
#include "../header/AndroidLog.h"
#endif


#ifndef MEDIA_INSIGHT_LOG
#define MEDIA_INSIGHT_LOG

#define AVLOGE(format, ...)  ALOGE(format, ##__VA_ARGS__);
#define AVLOGW(format, ...)  ALOGW(format, ##__VA_ARGS__);

#endif


class MediaInsighter{

public:
    std::string probeBasicInfo(std::string filepath);

};


#endif //DROIDMEDIA_MEDIAINSIGHTER_H
