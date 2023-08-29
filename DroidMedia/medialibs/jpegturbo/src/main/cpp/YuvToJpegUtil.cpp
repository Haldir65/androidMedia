#include <cstring>
#include <cstdint>
#include "YuvToJpegUtil.h"
#include "turbojpeg.h"

bool YuvToJpegUtil::convertYuvToJpeg(unsigned char *yuvBuffer, int yuvSize, int type, int width, int height, int padding,
                                     int quality, unsigned char **jpgBuffer, int &jpgSize) {

    // yu12 -> jpeg
    if (type == 0) {
        return yu12ToJpeg(yuvBuffer, yuvSize, width, height, padding, quality, jpgBuffer,
                          jpgSize);
    }

    // yv12 -> yu12
    // yu12 -> jpeg
    if (type == 1) {
        yv12ToYu12(yuvBuffer, yuvSize, width, height, padding);
        return yu12ToJpeg(yuvBuffer, yuvSize, width, height, padding, quality, jpgBuffer,
                          jpgSize);
    }

    // nv12 -> yu12
    // yu12 -> jpeg
    if (type == 2) {
        nv12ToYu12(yuvBuffer, yuvSize, width, height, padding);
        return yu12ToJpeg(yuvBuffer, yuvSize, width, height, padding, quality, jpgBuffer,
                          jpgSize);
    }

    // nv21 -> yu12
    // yu12 -> jpeg
    if (type == 3) {
        nv21ToYu12(yuvBuffer, width, height);
        int result = yu12ToJpeg(yuvBuffer, yuvSize, width, height, padding, quality,
                                jpgBuffer,
                                jpgSize);
        return result;
    }

    // yuyv -> yuv422p
    // yu12 -> jpeg
    if (type == 4) {
        yuyvToYuv422P(yuvBuffer, width, height);
        int result = yu12ToJpeg(yuvBuffer, yuvSize, width, height, padding, quality,
                                jpgBuffer,
                                jpgSize, TJSAMP_422);
        return result;
    }
}

/**
 * YUV420P：YV12 -> YU12
 */
bool YuvToJpegUtil::yv12ToYu12(BYTE *yuvBuffer, int yuvSize, int width, int height, int padding) {
    int uvLength = (yuvSize - width * height) / 2;
    BYTE *tempCache = new BYTE[uvLength];

    memcpy(tempCache, yuvBuffer + width * height, uvLength);
    memcpy(yuvBuffer + width * height, yuvBuffer + width * height + uvLength, uvLength);
    memcpy(yuvBuffer + width * height + uvLength, tempCache, uvLength);
    delete[] tempCache;
    tempCache = nullptr;
    return true;
}

/**
 * NV12 -> YU12
 */
bool YuvToJpegUtil::nv12ToYu12(BYTE *yuvBuffer, int yuvSize, int width, int height, int padding) {
    // 将yyyyyyuvuvuv 分离为 yyyuuuvvv
    // u 、v各占 1/6
    // y 独占 2/3
    // 创建存放UV的缓存
    int size = width * height * 3 / 2;
    BYTE *tempVBuffer = new BYTE[size / 6];
    // 第一个V的位置
    int uvIndex = 0;
    int vIndex = 0;
    int yEndPos = size * 2 / 3 + 1;
    for (int index = yEndPos; index < size; ++index) {
        // 偶数为 u
        if (index % 2 == 0) {
            // 当前的U 要替换  的位置
            int newUPos = yEndPos + uvIndex / 2;
            yuvBuffer[newUPos] = yuvBuffer[index];
        } else {
            *(tempVBuffer + vIndex) = yuvBuffer[index];
            vIndex++;
        }
        uvIndex++;
    }
    // 将v拼接到后面
    memcpy(yuvBuffer + size * 5 / 6, tempVBuffer, size / 6);
    delete[](tempVBuffer);
    return true;
}

/**
 * NV21 -> YU12
 */
int YuvToJpegUtil::nv21ToYu12(BYTE *in, int width, int height) {
    // 将yyyyyyvuvuvuvuvu 分离为 yyyuuuvvv
    // u 、v各占 1/6
    // y 独占 2/3
    // 创建存放UV的缓存
    int size = width * height * 3 / 2;
    BYTE *tempVBuffer = new BYTE[size / 6];
    // 第一个V的位置
    int uvIndex = 0;
    int vIndex = 0;
    int yEndPos = size * 2 / 3 + 1;
    for (int index = yEndPos; index < size; ++index) {
        // 偶数为 v
        if (index % 2 != 0) {
            // 当前的U 要替换  的位置
            int newUPos = yEndPos + uvIndex / 2;
            in[newUPos] = in[index];
        } else {
            *(tempVBuffer + vIndex) = in[index];
            vIndex++;
        }
        uvIndex++;
    }
    // 将v拼接到后面
    memcpy(in + size * 5 / 6, tempVBuffer, size / 6);
    delete[](tempVBuffer);
    return 0;
}

/**
 * YU12 -> Jpeg
 */
int YuvToJpegUtil::yu12ToJpeg(BYTE *yuvBuffer, int yuvSize, int width, int height, int padding,
                              int quality,
                              BYTE **jpgBuffer, int &jpgSize, TJSAMP TJSAMP_TYPE) {

    int subsample = TJSAMP_TYPE;

    tjhandle handle = NULL;
    int flags = 0;
    int need_size = 0;
    int ret = 0;

    handle = tjInitCompress();

    flags |= 0;

    need_size = tjBufSizeYUV2(width, padding, height, subsample);
    if (need_size != yuvSize) {
        return -1;
    }
    unsigned long retSize = 0;
    ret = tjCompressFromYUV(handle, yuvBuffer, width, padding, height, subsample,
                            jpgBuffer, &retSize, quality, flags);
    jpgSize = retSize;
    if (ret < 0) {
//        Log::info("压缩jpeg失败，错误信息:%s", tjGetErrorStr());
    }
    tjDestroy(handle);

    return ret;
}

/**
 * YUYV -> Yuv422P
 */
int YuvToJpegUtil::yuyvToYuv422P(BYTE *in, int width, int height) {
    // 将yuyv 分离为 yyyuuuvvv
    // 创建存放UV的缓存
    int size = width * height * 2;
    int halfOfSize = size / 2;
    BYTE *tempUVBuffer = new BYTE[halfOfSize];
    // 第一个U的位置
    BYTE *UBuffer = tempUVBuffer;
    // 第一个V的位置
    BYTE *VBuffer = tempUVBuffer + halfOfSize / 2;
    bool isU = true;
    int uIndex = 0;
    int vIndex = 0;
    for (int index = 0; index < size; ++index) {
        // 偶数为 Y
        if (index % 2 == 0) {
            // 当前的Y 要替换 index/2 的位置
            in[index / 2] = in[index];
        } else {
            // U、V交替
            if (isU) {
                isU = false;
                *(UBuffer + uIndex) = in[index];
                uIndex++;
            } else {
                *(VBuffer + vIndex) = in[index];
                isU = true;
                vIndex++;
            }
        }
    }
    // 将uv拼接到后面
    memcpy(in + halfOfSize, tempUVBuffer, halfOfSize);
    delete[](tempUVBuffer);
    return 0;
}

