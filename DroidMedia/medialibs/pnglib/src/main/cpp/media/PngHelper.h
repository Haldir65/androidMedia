//
// Created by harris on 2023/9/20.
//

#ifndef DROIDMEDIA_PNGHELPER_H
#define DROIDMEDIA_PNGHELPER_H
#include <string>
#include <iostream>

#ifdef __cplusplus
extern "C" {
#endif
#include <png.h>
#include <pngconf.h>
#include <pnglibconf.h>
#include <pngpriv.h>
#include <android/log.h>
//#include "png.h"
//#include "pnglibconf.h"
//#include "pngpriv.h"
#include "../AndroidLog.h"
#ifdef __cplusplus
}
#endif




using namespace std;

class PngHelper {

public:
    png_size_t sizeOfPixels;

private:
        const string mFileName;
        unsigned char *mPixelData;
        png_uint_32 mWidth, mHeight;
        int mBitDepth, mColorType, mInterlaceType;
        int mCompressionType, mFilterType;
        png_bytep *row_pointers;

        bool check_if_png(FILE *fp);
        public:

        PngHelper() = default;

        PngHelper(const string &file_name);

        ~PngHelper();

        unsigned int getWidth();

        unsigned int getHeight();

        bool has_alpha();

        unsigned char *getPixelData();

        void read_png_file(char *filename);

        void write_png_file(char *filename);

        void process_png_file();
};


#endif //DROIDMEDIA_PNGHELPER_H
