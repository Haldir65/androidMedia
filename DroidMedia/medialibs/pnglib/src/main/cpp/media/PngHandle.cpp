

#include "PngHandle.h"
#include <filesystem>

int PngHandle::probePngFileInfo(std::string filepath) {
    // 传 nullptr 的参数是用来自定义错误处理的，这里不需要
    const std::string_view jpegfile = std::string_view{filepath};
    if (!std::filesystem::exists(filepath)) {
        ALOGE("file %s not exists !",filepath.c_str());
        return -1;
    }
    png_structp png = png_create_read_struct(PNG_LIBPNG_VER_STRING, nullptr, nullptr, nullptr);

    FILE* fp = fopen(filepath.c_str(),"rb");
    #define PNG_BYTES_TO_CHECK 4
    char buf[PNG_BYTES_TO_CHECK];
    // 读取 buffer
    if (fread(buf, 1, PNG_BYTES_TO_CHECK, fp) != PNG_BYTES_TO_CHECK) {
        ALOGE("file %s is not an png file !",filepath.c_str());
        return -1;
    }
    // 判断
    if (!png_sig_cmp(reinterpret_cast<png_const_bytep>(buf), 0, PNG_BYTES_TO_CHECK)) {
        // 返回值不等于 0 则是 png 文件格式
        ALOGE("png_sig_cmp success! file %s is an png file !",filepath.c_str());
        return 0;
    }
    ALOGE("png_sig_cmp failed ! file %s is  an png file !",filepath.c_str());
    return -1;
}






bool PngHandle::jpeg_header_tester(std::string filepath) {
    int width, height;
    FILE *image;
    size_t size, i = 0;
    unsigned char *data;
    image = fopen(filepath.c_str(),"rb");  // open JPEG image file
    if(!image){
        printf("Unable to open image \n");
        return false;
    }
    fseek(image,  0,  SEEK_END);
    size = ftell(image);
    fseek(image,  0,  SEEK_SET);
    data = (unsigned char *)malloc(size);
    fread(data, 1, size, image);
/* verify valid JPEG header */
    if(data[i] == 0xFF && data[i + 1] == 0xD8 && data[i + 2] == 0xFF && data[i + 3] == 0xE0) {
        i += 4;
        /* Check for null terminated JFIF */
        if(data[i + 2] == 'J' && data[i + 3] == 'F' && data[i + 4] == 'I' && data[i + 5] == 'F' && data[i + 6] == 0x00) {
            while(i < size) {
                i++;
                if(data[i] == 0xFF){
                    if(data[i+1] == 0xC0) {
                        height = data[i + 5]*256 + data[i + 6];
                        width = data[i + 7]*256 + data[i + 8];
                        break;
                    }
                }
            }
        }
    }
    fclose(image);
    // https://stackoverflow.com/a/17848968
    return false;
}

bool PngHandle::saveRgbaBufferToPngFile(std::string &filepath, unsigned char *buffer, int width,int height) {
    if (std::filesystem::exists(filepath)){
       bool deleteResult =  std::filesystem::remove(filepath);
        if (!deleteResult){
            ALOGE("FAILED TO delete %s ",filepath.c_str())
        } else {
            ALOGW("Successfully  delete %s ",filepath.c_str())
        }
    }

    int y;
    FILE *fp = fopen(filepath.c_str(), "wb");
    if (!fp) abort();
    png_structp png = png_create_write_struct(PNG_LIBPNG_VER_STRING, NULL, NULL, NULL);
    if (!png) abort();

    png_infop info = png_create_info_struct(png);
    if (!info) abort();

    if (setjmp(png_jmpbuf(png))){
        // 做一些清理工作
        ALOGD("error happen");
        abort();
    }

    png_init_io(png, fp);

    // Output is 8bit depth, RGBA format.
    png_set_IHDR(
            png,
            info,
            width, height,
            8,
            PNG_COLOR_TYPE_RGBA,
            PNG_INTERLACE_NONE,
            PNG_COMPRESSION_TYPE_DEFAULT,
            PNG_FILTER_TYPE_DEFAULT
    );
    png_write_info(png, info);

    // To remove the alpha channel for PNG_COLOR_TYPE_RGB format,
    // Use png_set_filler().
    //png_set_filler(png, 0, PNG_FILLER_AFTER);
    png_bytep *row_pointers = (png_bytep *) malloc(sizeof(png_bytep) * height);
    size_t row_byte_counts = png_get_rowbytes(png, info);
    for (int y = 0; y < height; y++) {
        row_pointers[y] = (png_byte *) malloc(row_byte_counts);
        memcpy(row_pointers[y],buffer+y*row_byte_counts,row_byte_counts);
    }

    png_write_image(png, static_cast<png_bytepp>(row_pointers));
    png_write_end(png, nullptr);

    for (int y = 0; y < height; y++) {
        free(row_pointers[y]);
    }
    free(row_pointers);

    fclose(fp);



    return false;
}
