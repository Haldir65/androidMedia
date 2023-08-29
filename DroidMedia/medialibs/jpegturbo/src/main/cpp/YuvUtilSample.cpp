#include "YuvToJpegUtil.h"
#include <iostream>

void testYuvToJpeg(int quality){

    YuvToJpegUtil *jpgUtil = new YuvToJpegUtil();
    uint8_t *jpgBuffer = new uint8_t[1024 * 1024];
    int jpgSize = 1024 * 1024;
    int yuvSize = 1920 * 1080 * 3 / 2;
    unsigned char *yuvBuffer = new unsigned char[yuvSize];
    FILE *fp_in = fopen(
            "/sdcard/1614138720028814.yuv",
            "rb+");
    if (fp_in != NULL) {
        fread(yuvBuffer, yuvSize, 1, fp_in);
    } else {
        std::cout<<"找不到yuv文件"<<std::endl;
        return;
    }
    fclose(fp_in);
    jpgUtil->convertYuvToJpeg(yuvBuffer, yuvSize, 0, 1920, 1080, 4, quality, &jpgBuffer, jpgSize);
    std::string path = "/sdcard/1614138720028814_";
    path = path+ std::to_string(quality)+".jpg";
    FILE *fp_out = NULL;
    if ((fp_out = fopen(
            path.c_str(),
            "wb+")) == NULL) {
        std::cout<<"文件创建失败"<<std::endl;
    } else {
        fwrite(jpgBuffer, jpgSize, 1, fp_out);
        fclose(fp_out);
        std::cout<<"文件创建成功"<<std::endl;
    }

    delete[] jpgBuffer;
    delete[] yuvBuffer;
}

int main() {
    testYuvToJpeg(80);
    testYuvToJpeg(100);
    return 0;
}
//