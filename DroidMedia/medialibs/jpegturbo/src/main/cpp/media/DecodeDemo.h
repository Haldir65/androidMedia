

#ifndef DROIDMEDIA_DECODEDEMO_H
#define DROIDMEDIA_DECODEDEMO_H

#include <media/NdkMediaCodec.h>
#include <media/NdkMediaExtractor.h>
#include <media/NdkMediaFormat.h>

#include <cstdio>
#include <string>
#include <errno.h>
#include <unistd.h>

#ifdef __cplusplus
extern "C" {
#endif
#include "android/log.h"
#include <fcntl.h>
#ifdef __cplusplus
}
#endif

class DecodeDemo {


public:
    DecodeDemo() {}

    ~DecodeDemo();

    bool Decode();

    bool Release();

    bool Init();

    bool Seek(int sec = 0);

    bool Play(int sec = 999);

    bool AutoPlay();

private:
//    AMediaFormat *format_[2] = {nullptr, nullptr};
    AMediaFormat *format_ = nullptr;
    AMediaExtractor *extractor_ = nullptr;
    AMediaCodec *codec_ = nullptr;
    int video_track_indx_ = -1;
    bool everplay_ = false;
};


#endif //DROIDMEDIA_DECODEDEMO_H
