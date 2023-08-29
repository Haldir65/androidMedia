
#ifndef DROIDMEDIA_YUVTOJPEGUTIL_H
#define DROIDMEDIA_YUVTOJPEGUTIL_H

#ifdef __cplusplus
extern "C" {
#endif
#include "AndroidLog.h"
#include "include/turbojpeg.h"
#ifdef __cplusplus
}
#endif

typedef unsigned char BYTE;

class YuvToJpegUtil {
public:
    bool convertYuvToJpeg(unsigned char *yuvBuffer,
                          int yuvSize,
                          int type,
                          int width,
                          int height,
                          int padding,
                          int quality,
                          unsigned char **jpgBuffer,
                          int &jpgSize);

private:

    /*
       * YUV420P：YV12 -> YU12，because libjpeg only surport YU12
       */
    bool yv12ToYu12(BYTE *yuv_buffer, int yuv_size, int width, int height, int padding);

    /*
     * NV12 -> YU12
     */
    bool nv12ToYu12(BYTE *yuv_buffer, int yuv_size, int width, int height, int padding);

    /**
     * NV21 -> YU12
     */
    int nv21ToYu12(BYTE *in, int width, int height);

    /**
     * YUYV -> YUV422P
     */
    int yuyvToYuv422P(BYTE *in, int width, int height);

    /**
     * YU12 -> Jpeg
     * @param yuv_buffer：yuv数据区
     * @param yuv_size：yuv大小
     * @param width：yuv宽度
     * @param height：yuv高度
     * @param quality：jpg压缩质量 (1-100)
     * @out jpg_buffer:输出的jpg数据
     * @out jpg_size :输出的jpg大小
     * @return :0为成功
     */
    int yu12ToJpeg(BYTE *yuv_buffer, int yuv_size, int width, int height, int padding, int quality,
                   BYTE **jpg_buffer, int &jpg_size, TJSAMP = TJSAMP_420);
};


#endif //DROIDMEDIA_YUVTOJPEGUTIL_H
