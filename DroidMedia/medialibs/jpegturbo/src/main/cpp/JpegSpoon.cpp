

#include "JpegSpoon.h"
#include <chrono>
#include <filesystem>
#include <fstream>
#include <vector>


METHODDEF(void) my_error_exit(j_common_ptr cinfo) {
    my_error_ptr myerr = (my_error_ptr) cinfo->err;
    (*cinfo->err->output_message)(cinfo);
    ALOGE("jpeg_message_table[%d]:%s", myerr->pub.msg_code,
         myerr->pub.jpeg_message_table[myerr->pub.msg_code]);
    longjmp(myerr->setjmp_buffer, 1);
}

void JpegSpoon::callSomeMethod() {

}



int JpegSpoon::write_JPEG_file(BYTE *data, int w, int h, int quality, const char *outFileName, bool optimize) {

    //jpeg的结构体，保存的比如宽、高、位深、图片格式等信息
    struct jpeg_compress_struct cinfo;

    /* Step 1: allocate and initialize JPEG compression object */

    /* We set up the normal JPEG error routines, then override error_exit. */

    struct my_error_mgr jem;

    cinfo.err = jpeg_std_error(&jem.pub);
    jem.pub.error_exit = my_error_exit;
    if (setjmp(jem.setjmp_buffer)){
        /* If we get here, the JPEG code has signaled an error.
      and return.
      */
        return -1;
    }
    jpeg_create_compress(&cinfo);

    /* Step 2: specify data destination (eg, a file) */

    FILE *outfile = fopen(outFileName,"wb");
    if (outfile == nullptr){
        ALOGE("can't open %s",outFileName);
    }
    jpeg_stdio_dest(&cinfo,outfile);
    /* Step 3: set parameters for compression */

    cinfo.image_width = w;      /* image width and height, in pixels */
    cinfo.image_height = h;
    cinfo.input_components = 3;           /* # of color components per pixel */
    cinfo.in_color_space = JCS_RGB;       /* colorspace of input image */

    /*  源码地址：
     [http://androidos.net.cn/androidossearch?query=SkImageDecoder_libjpeg.cpp](http://androidos.net.cn/androidossearch?query=SkImageDecoder_libjpeg.cpp)

     >=android 7.0 后的源码已经设置为true了
     ...省略其它代码
     Tells libjpeg-turbo to compute optimal Huffman coding tables
     for the image.  This improves compression at the cost of
     slower encode performance.
     cinfo.optimize_coding = TRUE;
     jpeg_set_quality(&cinfo, quality, TRUE);
     ...省略其它代码*/


    cinfo.optimize_coding = optimize;
    //哈夫曼编码和算术编码，TRUE=arithmetic coding, FALSE=Huffman
    if (optimize) {
        cinfo.arith_code = false;
    } else {
        cinfo.arith_code = true;
    }
    // 其它参数 全部设置默认参数
    jpeg_set_defaults(&cinfo);
    //设置质量
    jpeg_set_quality(&cinfo, quality, TRUE /* limit to baseline-JPEG values */);


    /* Step 4: Start compressor */

    jpeg_start_compress(&cinfo, TRUE);


    /* Step 5: while (scan lines remain to be written) */
    /*           jpeg_write_scanlines(...); */

    JSAMPROW row_pointer[1];
    int row_stride;
    //一行的RGB数量
    row_stride = cinfo.image_width * 3; /* JSAMPLEs per row in image_buffer */
    //一行一行遍历
    while (cinfo.next_scanline < cinfo.image_height) {
        //得到一行的首地址
        row_pointer[0] = &data[cinfo.next_scanline * row_stride];
        //此方法会将jcs.next_scanline加1
        jpeg_write_scanlines(&cinfo, row_pointer, 1);//row_pointer就是一行的首地址，1：写入的行数
    }
    /* Step 6: Finish compression */
    jpeg_finish_compress(&cinfo);
    /* After finish_compress, we can close the output file. */
    fclose(outfile);
    outfile = nullptr;

    /* Step 7: release JPEG compression object */

    /* This is an important step since it will release a good deal of memory. */
    jpeg_destroy_compress(&cinfo);

    /* And we're done! */
    return 0;

    return 0;
}

#define COMPRESS_MODE_TURBO_JPEG  1
#define COMPRESS_MODE_LIBJPEG_FILE  2
#define COMPRESS_MODE_LIBJPEG_IN_MEMORY  3

jint JpegSpoon::compressBitmap(JNIEnv *env, jobject thiz, jobject bitmap, jint quality,
                               jstring out_file_path, jboolean optimize,int mode) {


    //获取Bitmap信息
    AndroidBitmapInfo android_bitmap_info;
    AndroidBitmap_getInfo(env, bitmap, &android_bitmap_info);
    //获取bitmap的 宽，高，format
    u_int32_t w = android_bitmap_info.width;
    u_int32_t h = android_bitmap_info.height;

    ALOGW("android_bitmap_info w =%d h = %d ", w,h);


    BYTE *tempData = read_rgb_buffer_from_bitmap(env,thiz,bitmap);

    char *path = (char *) env->GetStringUTFChars(out_file_path, nullptr);
    ALOGI("path=%s", path);
    int resultCode = 0;
    auto startTime = std::chrono::high_resolution_clock::now();
    switch (mode) {
        case COMPRESS_MODE_TURBO_JPEG:
            // Turbojpeg进行压缩，并写入文件
            resultCode =  compress_rgb_to_jpeg(tempData,quality,static_cast<int>(w),static_cast<int>(h),path);
            break;
        case COMPRESS_MODE_LIBJPEG_FILE:
            // Libjpeg进行压缩，并写入文件
            resultCode = write_JPEG_file(tempData, static_cast<int>(w), static_cast<int>(h), quality, path, optimize);
            break;
        case COMPRESS_MODE_LIBJPEG_IN_MEMORY:
            // Libjpeg进行压缩，并写入memory
            resultCode = write_JPEG_file_in_memory(tempData, static_cast<int>(w), static_cast<int>(h), quality, path,optimize);
            break;
        default:
            resultCode = -1;
            break;
    }
    auto endTime = std::chrono::high_resolution_clock::now();
    auto cost = std::chrono::duration_cast<std::chrono::milliseconds>(endTime - startTime).count();
    ALOGW(" turbo = %i compress bitmap with width = %d height %d  \n to file %s \n cost me %s milliseconds ",mode, w, h,path,std::to_string(cost).c_str());
    if (resultCode == -1) {
        return -1;
    }
    env->ReleaseStringUTFChars(out_file_path, path);
    free(tempData);
    tempData = nullptr;
    return 0;
}



int JpegSpoon::compress_rgb_to_jpeg(BYTE *rgbBuffer, int quality, int width, int height, const std::string& jpeg_file_path) {

    tjhandle tj = tjInitCompress();
    unsigned char * jpegBuf = nullptr;
    unsigned long jpegSize = 0;
    const int r = tjCompress2( tj, ( unsigned char * )rgbBuffer,	// TJ isn't const correct...
                               width, width * 3, height, TJPF_RGB, &jpegBuf,
                               &jpegSize, TJSAMP_420 /* TJSAMP_422 */, quality /* jpegQual */, TJFLAG_FASTUPSAMPLE|TJFLAG_FASTDCT/* flags */ );
//    TJSAMP_GRAY 变为黑白
    if ( r != 0 )
    {
        ALOGE( "tjCompress2 returned %s for %s", tjGetErrorStr(), jpeg_file_path.c_str() );
        return false;
    }

    FILE * f = fopen( jpeg_file_path.c_str(), "wb" );
    if ( f != nullptr )
    {
        fwrite( jpegBuf, jpegSize, 1, f );
        fclose( f );
    }
    else
    {
        ALOGE( "WriteJpeg failed to write to %s", jpeg_file_path.c_str() );
        return false;
    }

    tjFree( jpegBuf );

    tjDestroy( tj );

    return true;
}


int JpegSpoon::yuv_2_jpeg_buffer_Turbo(BYTE *yuvBuffer, int yuvSize, int width, int height, int padding, int quality,
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
    return 0;
}

JpegSpoon::~JpegSpoon() {
    this->env = nullptr;
}

JpegSpoon::JpegSpoon(JNIEnv *env, const std::string &storage_dir) {
    this->env = env;
    this->storage_dir = storage_dir;
}

BYTE* JpegSpoon::read_rgb_buffer_from_bitmap(JNIEnv *env, jobject thiz, jobject bitmap) {


    //获取Bitmap信息
    AndroidBitmapInfo android_bitmap_info;
    int rcc = AndroidBitmap_getInfo(env, bitmap, &android_bitmap_info);
    if (rcc!=ANDROID_BITMAP_RESULT_SUCCESS){
        throw "getBitmap info failure";
    }
    if (android_bitmap_info.format != ANDROID_BITMAP_FORMAT_RGBA_8888){
        throw "only argb888 format supported";
    }
    //获取bitmap的 宽，高，format
    int w = android_bitmap_info.width;
    int h = android_bitmap_info.height;

    ALOGW("android_bitmap_info w =%d h = %d ", w,h);


    //读取Bitmap所有像素信息
    BYTE *pixelsColor;
     rcc = AndroidBitmap_lockPixels(env, bitmap, (void **) &pixelsColor);
    if (rcc != ANDROID_BITMAP_RESULT_SUCCESS){
        throw "lockPixel failure";
    }

    int i = 0, j = 0;
    BYTE r, g, b,alpha;
    //存储RGB所有像素点
    BYTE *data = (BYTE *) malloc(w * h * 3);
    // 临时保存指向像素内存的首地址
    BYTE *tempData = data;

    uint32_t color;
    for (i = 0; i < h; i++) {
        for (j = 0; j < w; j++) {
            // 取出一个像素  去调了alpha，然后保存到data中，对应指针++
            color = *((uint32_t *) pixelsColor);

            // 在jni层中，Bitmap像素点的值是ABGR，而不是ARGB，也就是说，高端到低端：A，B，G，R
            alpha =  color & 0xff000000;
            b = ((color & 0x00FF0000) >> 16);
            g = ((color & 0x0000FF00) >> 8);
            r = ((color & 0x000000FF));
            // jpeg压缩需要的是rgb
            //  for example, R,G,B,R,G,B,R,G,B,... for 24-bit RGB color.
            // Pixel Bytes Order has RGBA in LITTLE ENDIAN,
            *data = r;
            *(data + 1) = g;
            *(data + 2) = b;
            data += 3;
            pixelsColor += 4;
        }
    }
    AndroidBitmap_unlockPixels(env, bitmap);
    return tempData;
}

int JpegSpoon::write_JPEG_file_in_memory(BYTE *data, int w, int h, int quality, const char *outFileName,
                                         bool optimize) {
    struct jpeg_compress_struct jpegCompressStruct;


    struct my_error_mgr jem;

    jpegCompressStruct.err = jpeg_std_error(&jem.pub);
    jem.pub.error_exit = my_error_exit;
    if (setjmp(jem.setjmp_buffer)){
        /* If we get here, the JPEG code has signaled an error.
      and return.
      */
        return -1;
    }

    // 创建代表压缩的结构体
    jpeg_create_compress(&jpegCompressStruct);

    // 文件方式输出 还有一种是内存方式
//    jpeg_stdio_dest(&jpegCompressStruct, fp);
    unsigned char *outbuffer = new unsigned char [w*h*3];
    unsigned long outSize = 0;
    jpeg_mem_dest(&jpegCompressStruct,(static_cast<unsigned char **>(&outbuffer)), &outSize);

    // 设置压缩的相关参数信息
    jpegCompressStruct.image_width = w;
    jpegCompressStruct.image_height = h;
    jpegCompressStruct.arith_code = false;
    jpegCompressStruct.input_components = 3;
    // 设置解压的颜色
    jpegCompressStruct.in_color_space = JCS_RGB;
    jpeg_set_defaults(&jpegCompressStruct);
    // 压缩的质量
    jpegCompressStruct.optimize_coding = quality;
    jpeg_set_quality(&jpegCompressStruct, quality, TRUE);

    auto startTime = std::chrono::high_resolution_clock::now();
    // 开始压缩
    jpeg_start_compress(&jpegCompressStruct, TRUE);
    // JSAMPROW 代表每行的数据
    JSAMPROW row_pointer[1];
    int row_stride = jpegCompressStruct.image_width * 3;

    while (jpegCompressStruct.next_scanline < jpegCompressStruct.image_height) {
        startTime = std::chrono::high_resolution_clock::now();

        // data 参数就是要压缩的数据源
        // 逐行读取像素内容
        row_pointer[0] = &data[jpegCompressStruct.next_scanline * row_stride];
        // 写入数据
        jpeg_write_scanlines(&jpegCompressStruct, row_pointer, 1);

        auto endTime = std::chrono::high_resolution_clock::now();
        auto cost = std::chrono::duration_cast<std::chrono::microseconds>(endTime - startTime).count();
        ALOGI("write each line at line num %d cost me %s microseconds ",jpegCompressStruct.next_scanline,std::to_string(cost).c_str());

    }


    // 完成压缩
    jpeg_finish_compress(&jpegCompressStruct);
    // 释放相应的结构体
    jpeg_destroy_compress(&jpegCompressStruct);


    // write file
    startTime = std::chrono::high_resolution_clock::now();
    std::string_view filepath { outFileName };
    std::fstream fos { filepath ,std::ios::out | std::ios::binary };
    if (fos){
        fos.write(reinterpret_cast<const char *>(outbuffer), (std::streamsize)outSize);
    } else {
        ALOGI("fatal , failed to write jpeg buff to file %s \n", outFileName);
    }
    auto endTime = std::chrono::high_resolution_clock::now();
     auto cost = std::chrono::duration_cast<std::chrono::microseconds>(endTime - startTime).count();
    ALOGW(" writing jpeg buffer with size %lu bytes to jpeg file %s using fstream \n  cost me %s microseconds \n the original rgb buffer size is %d bytes ", outSize,outFileName,std::to_string(cost).c_str(),(w*h*3));

    delete[] outbuffer;

    ALOGW("out size = %ld , write to %s",outSize,outFileName);

    return 0;
}


// https://mp.weixin.qq.com/s/x-dTLowXcqysnLxZ0BGA2g
long JpegSpoon::decompressjpegToRgbBuffer(JNIEnv *env, jobject thiz, std::string jpeg_path,
                                          jobject dstBuffer) {
    const std::string_view jpegfile = std::string_view{jpeg_path};
    if (!std::filesystem::exists(jpegfile)) {
        return 0;
    }

//    std::ifstream inputFile { jpegfile, std::ios ::binary | std::ios::in};
//
//    if (!inputFile) {
//        std::cerr << "Failed to open binary file" << std::endl;
//        return -1;
//    }
//
//    // Move the pointer to the end of the file to get the file size
//    inputFile.seekg(0, inputFile.end);
//    size_t fileSize = inputFile.tellg();
//    inputFile.seekg(0, inputFile.beg);
//
//
//    // Read the binary file into a vector buffer
//    std::vector<char> buffer(fileSize);
//    inputFile.read(buffer.data(), fileSize);
//
//    // Close the file
//    inputFile.close();
//
//    // Process the content of the binary file as needed
//    // For this example, we'll just print the size of the file
//    std::cout << "The binary file size is: " << fileSize << " bytes." << std::endl;
//
//
//    char * jpegBuffer = &buffer[0];
   // https://stackoverflow.com/a/2923290  vector to arrray

    struct jpeg_decompress_struct cinfo;


    struct my_error_mgr jem;

    cinfo.err = jpeg_std_error(&jem.pub);
    jem.pub.error_exit = my_error_exit;
    if (setjmp(jem.setjmp_buffer)){
        /* If we get here, the JPEG code has signaled an error.
      and return.
      */
        return -1;
    }


    FILE * fp= fopen(jpeg_path.c_str(),"rb");
    jpeg_create_decompress(&cinfo);
    // 设置数据源数据方式 这里以文件的方式，也可以以内存数据的方式
    jpeg_stdio_src(&cinfo, fp);
    // 读取文件信息，比如宽高之类的
    jpeg_read_header(&cinfo, TRUE);

    // To use it after your jpeg_read_header() call (because this call sets a member on cinfo we need to a default)
    // but before your jpeg_start_decompress() call (because it uses the value of this member), add:

    cinfo.out_color_space = JCS_EXT_RGBX;

    // 设置解压的相关参数：
    jpeg_start_decompress(&cinfo); // 这一行走完，output_width就都有了

    unsigned long width = cinfo.output_width; //1760
    unsigned long height = cinfo.output_height; // 990
    unsigned short depth = cinfo.output_components; //3
     long row_stride = cinfo.output_width * cinfo.output_components; // 5280

    ALOGW("image %s has width = %d , height = %d , output_components = %d ",jpeg_path.c_str(),cinfo.output_width,cinfo.image_height,cinfo.num_components)

    auto startTime = std::chrono::high_resolution_clock::now();

    // 保存每行解压的数据内容
    JSAMPARRAY tmp;
    // 初始化
    tmp = (*cinfo.mem->alloc_sarray)((j_common_ptr) &cinfo, JPOOL_IMAGE, row_stride, 1);

    // 保存图像的所有数据
    unsigned char *src_buff;
    // 初始化并置空
    src_buff = static_cast<unsigned char *>(malloc(width * height * depth));
    memset(src_buff, 0, sizeof(unsigned char) * width * height * depth);


    unsigned char *point = src_buff;

    while (cinfo.output_scanline < height) {
        jpeg_read_scanlines(&cinfo, tmp, 1);
        memcpy(point, *tmp, width * depth);
        point += width * depth;
    }

    jpeg_finish_decompress(&cinfo);
    jpeg_destroy_decompress(&cinfo);


    // todo -> copy rgb buffer to direct buffer
    char *cBuffer = static_cast<char *>(env->GetDirectBufferAddress(dstBuffer));

    memcpy(cBuffer,src_buff,width*height*cinfo.output_components);

    free(src_buff);
    ALOGW("done copy rgb buffer to directBuffer")

    return 0;
}

jintArray JpegSpoon::probeJpegFileInfo(JNIEnv *env, jobject thiz, std::string jpeg_path) {
    jintArray result;
    int size = 2;
    result = env->NewIntArray(size);
    jint  fill[size];
    for (int i =0;i<size;i++) {
        fill[i] = -1;
    }
    if (!result) return nullptr;
    const std::string_view jpegfile = std::string_view{jpeg_path};
    if (!std::filesystem::exists(jpegfile)) {
        env->SetIntArrayRegion(result,0,size,fill);
        return result;
    }
    struct jpeg_decompress_struct cinfo;



    struct my_error_mgr jem;

    cinfo.err = jpeg_std_error(&jem.pub);
    jem.pub.error_exit = my_error_exit;
    if (setjmp(jem.setjmp_buffer)){
        /* If we get here, the JPEG code has signaled an error.
      and return.
      */
        return nullptr;
    }

    FILE * fp= fopen(jpeg_path.c_str(),"rb");
    jpeg_create_decompress(&cinfo);
    // 设置数据源数据方式 这里以文件的方式，也可以以内存数据的方式
    jpeg_stdio_src(&cinfo, fp);
    // 读取文件信息，比如宽高之类的
    jpeg_read_header(&cinfo, TRUE);
    fill[0] = static_cast<int>(cinfo.image_width);
    fill[1] = static_cast<int>(cinfo.image_height);

//    jpeg_finish_decompress(&cinfo);
    jpeg_destroy_decompress(&cinfo);
    env->SetIntArrayRegion(result,0,size,fill);
    return result;
}

// https://stackoverflow.com/questions/9094691/examples-or-tutorials-of-using-libjpeg-turbos-turbojpeg
long JpegSpoon::decompressjpegToRgbBufferTurbo(JNIEnv *env, jobject thiz, std::string jpeg_path, jobject dstBuffer,jint width,jint height) {
    std::ifstream inputFile { jpeg_path, std::ios ::binary | std::ios::in};

    if (!inputFile) {
        std::cerr << "Failed to open binary file" << std::endl;
        return -1;
    }

    // Move the pointer to the end of the file to get the file size
    inputFile.seekg(0, inputFile.end);
    size_t fileSize = inputFile.tellg();
    inputFile.seekg(0, inputFile.beg);


    // Read the binary file into a vector buffer
    std::vector<char> buffer(fileSize);
    inputFile.read(buffer.data(), fileSize);

    // Close the file
    inputFile.close();

    // Process the content of the binary file as needed
    // For this example, we'll just print the size of the file
    std::cout << "The binary file size is: " << fileSize << " bytes." << std::endl;


    char * jpegBuffer = &buffer[0];

    std::chrono::time_point<std::chrono::steady_clock> start, end;
    start = std::chrono::steady_clock::now();

    long unsigned int _jpegSize = fileSize; //!< _jpegSize from above
    unsigned char* _compressedImage = reinterpret_cast<unsigned char *>(jpegBuffer); //!< _compressedImage from above

    int pixelFormat = TJPF_BGRX;

    int COLOR_COMPONENTS = tjPixelSize[pixelFormat]; // 4

    int jpegSubsamp;

    unsigned char *imgBuf  = (unsigned char *)tjAlloc(width*height*COLOR_COMPONENTS); // //!< will contain the decompressed image
    tjhandle _jpegDecompressor = tjInitDecompress();

    int flags = TJFLAG_FASTDCT;
    tjDecompressHeader2(_jpegDecompressor, _compressedImage, _jpegSize, &width, &height, &jpegSubsamp);

    tjDecompress2(_jpegDecompressor, _compressedImage, _jpegSize, imgBuf, width, 0/*pitch*/, height, TJPF_RGB, flags);
    char *cBuffer = static_cast<char *>(env->GetDirectBufferAddress(dstBuffer));
    memcpy(cBuffer,imgBuf,width*height*COLOR_COMPONENTS);

    tjFree(imgBuf);
    tjDestroy(_jpegDecompressor);

    end = std::chrono::steady_clock::now();
    std::chrono::duration<double, std::milli> elapsed_time = end - start;
    std::cout << "turbo uncompress jpg to bmp: " << elapsed_time.count() << std::endl;
    ALOGW("turbo uncompress jpg to bmp: cost me %f milliseconds ",elapsed_time.count())

    return 0;
}





