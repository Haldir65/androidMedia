

#ifndef DROIDMEDIA_JPEGSPOON_H
#define DROIDMEDIA_JPEGSPOON_H

#include <iostream>
#include <setjmp.h>
#include "AndroidLog.h"
#include <jni.h>
#include <string>
#include <android/bitmap.h>

#ifdef __cplusplus
extern "C" {
#endif

#include "include/jerror.h"
#include "include/jconfig.h"
#include "include/jmorecfg.h"
#include "include/jpeglib.h"
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
public:
    int age;
    std::string name;

    void callSomeMethod();

    int write_JPEG_file(BYTE *data, int w, int h, int quality, const char *outFileName, bool optimize);

    jint compressBitmap(JNIEnv *env, jobject thiz, jobject bitmap,
                        jint quality, jstring out_file_path,
                        jboolean optimize);
};


#endif //DROIDMEDIA_JPEGSPOON_H
