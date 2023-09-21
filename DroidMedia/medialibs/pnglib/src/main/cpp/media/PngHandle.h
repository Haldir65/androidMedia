

#ifndef DROIDMEDIA_PNGHANDLE_H
#define DROIDMEDIA_PNGHANDLE_H

#include <string>
#include <iostream>
#include <string_view>
#include <filesystem>

#ifdef __cplusplus
extern "C" {
#endif
#include <png.h>
#include <pngconf.h>
#include <pnglibconf.h>
#include <pngpriv.h>
#include <android/log.h>
#include "../AndroidLog.h"
#ifdef __cplusplus
}
#endif





class PngHandle{
public:
    int probePngFileInfo(std::string filepath);

    bool jpeg_header_tester(std::string filepath);

    bool saveRgbaBufferToPngFile(std::string &filepath, unsigned char * buffer, int width,int height);
};


#endif //DROIDMEDIA_PNGHANDLE_H
