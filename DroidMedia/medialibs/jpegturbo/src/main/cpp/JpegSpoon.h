

#ifndef DROIDMEDIA_JPEGSPOON_H
#define DROIDMEDIA_JPEGSPOON_H

#include <iostream>
#include <setjmp.h>
#include "AndroidLog.h"
#include <jni.h>
#include <string>
#include <android/bitmap.h>
#include <chrono>

#ifdef __cplusplus
extern "C" {
#endif

#include "include/jerror.h"
#include "include/jconfig.h"
#include "include/jmorecfg.h"
#include "include/jpeglib.h"
#include "include/turbojpeg.h"
#ifdef __cplusplus
};
#endif

typedef uint8_t BYTE;

struct my_error_mgr {
    struct jpeg_error_mgr pub;
    jmp_buf setjmp_buffer; /* for return to caller */
};

typedef struct my_error_mgr *my_error_ptr;

class JpegSpoon {
private:
    BYTE* read_rgb_buffer_from_bitmap(JNIEnv *env, jobject thiz, jobject bitmap);
public:
    JNIEnv *env;
    std::string storage_dir;
    int processed_num;

    explicit JpegSpoon(JNIEnv* env,const std::string& storage_dir);

    virtual ~JpegSpoon();


    void callSomeMethod();

    int write_JPEG_file(BYTE *data, int w, int h, int quality, const char *outFileName, bool optimize);
    int write_JPEG_file_in_memory(BYTE *data, int w, int h, int quality, const char *outFileName, bool optimize);

    int yuv_2_jpeg_buffer_Turbo(BYTE *yuvBuffer, int yuvSize, int width, int height, int padding,
                                int quality,
                                BYTE **jpgBuffer, int &jpgSize, TJSAMP TJSAMP_TYPE);
    int compress_rgb_to_jpeg(BYTE *rgbBuffer, int quality, int width , int height, const std::string& jpeg_file_path);

    jint compressBitmap(JNIEnv *env, jobject thiz, jobject bitmap,
                        jint quality, jstring out_file_path,
                        jboolean optimize,int mode);

     long decompressjpegToRgbBuffer(JNIEnv *env, jobject thiz,std::string jpeg_path,jobject dstBuffer);

};


#endif //DROIDMEDIA_JPEGSPOON_H
