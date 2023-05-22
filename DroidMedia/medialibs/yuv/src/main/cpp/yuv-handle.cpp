#include <jni.h>
#include <string>
#include <iostream>
#include <android/log.h>
//引入头文件
extern "C"
{
#include "libyuv.h"
}
//定义日志宏变量
#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,"=A=",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"=A=",FORMAT,##__VA_ARGS__);



extern "C"
JNIEXPORT void JNICALL
Java_com_me_harris_libyuv_YuvUtils_yuvI420ToNV21(JNIEnv *env, jobject clazz, jbyteArray i420_src,
                                                 jbyteArray nv21_src, jint width, jint height) {

    jbyte *src_i420_data = env->GetByteArrayElements(i420_src, NULL);
    jbyte *src_nv21_data = env->GetByteArrayElements(nv21_src, NULL);

    jint src_y_size = width * height;
    jint src_u_size = (width >> 1) * (height >> 1);

    jbyte *src_i420_y_data = src_i420_data;
    jbyte *src_i420_u_data = src_i420_data + src_y_size;
    jbyte *src_i420_v_data = src_i420_data + src_y_size + src_u_size;

    jbyte *src_nv21_y_data = src_nv21_data;
    jbyte *src_nv21_vu_data = src_nv21_data + src_y_size;


    libyuv::I420ToNV21(
    (uint8_t *) src_i420_y_data, width,
    (uint8_t *) src_i420_u_data, width >> 1,
    (uint8_t *) src_i420_v_data, width >> 1,
    (uint8_t *) src_nv21_y_data, width,
    (uint8_t *) src_nv21_vu_data, width,
    width, height);

    env->ReleaseByteArrayElements(i420_src, src_i420_data, 0);
    env->ReleaseByteArrayElements(nv21_src, src_nv21_data, 0);
}



extern "C"
JNIEXPORT void JNICALL
Java_com_me_harris_libyuv_YuvUtils_yuvI420ToNV212(JNIEnv *env, jobject type, jbyteArray nv21_,
jobject y_buffer, jint y_rowStride, jobject u_buffer,
        jint u_rowStride, jobject v_buffer, jint v_rowStride,
jint width, jint height) {
    jbyte *nv21 = env->GetByteArrayElements(nv21_, NULL);
    uint8_t *srcYPtr = reinterpret_cast<uint8_t *>(env->GetDirectBufferAddress(y_buffer));
    uint8_t *srcUPtr = reinterpret_cast<uint8_t *>(env->GetDirectBufferAddress(u_buffer));
    uint8_t *srcVPtr = reinterpret_cast<uint8_t *>(env->GetDirectBufferAddress(v_buffer));


    jint src_y_size = width * height;
    jbyte *src_nv21_y_data = nv21;
    jbyte *src_nv21_vu_data = nv21 + src_y_size;
    libyuv::I420ToNV21(
            srcYPtr, y_rowStride,
            srcUPtr, u_rowStride,
            srcVPtr, v_rowStride,
    (uint8_t *) src_nv21_y_data, width,
    (uint8_t *) src_nv21_vu_data, width,
    width, height
    );

    env->ReleaseByteArrayElements(nv21_, nv21, 0);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_me_harris_libyuv_YuvUtils_yuvI420ToABGR(JNIEnv *env, jobject type, jbyteArray argb_,
    jobject y_buffer, jint y_rowStride,
    jobject u_buffer, jint u_rowStride,
    jobject v_buffer, jint v_rowStride,
    jint width, jint height) {
    jbyte *argb = env->GetByteArrayElements(argb_, NULL);


    uint8_t *srcYPtr = reinterpret_cast<uint8_t *>(env->GetDirectBufferAddress(y_buffer));
    uint8_t *srcUPtr = reinterpret_cast<uint8_t *>(env->GetDirectBufferAddress(u_buffer));
    uint8_t *srcVPtr = reinterpret_cast<uint8_t *>(env->GetDirectBufferAddress(v_buffer));

    jbyte *temp_y = new jbyte[width * height * 3 / 2];
    jbyte *temp_u = temp_y + width * height;
    jbyte *temp_v = temp_y + width * height + width * height / 4;

    libyuv::I420Rotate(
            srcYPtr, y_rowStride,
            srcUPtr, u_rowStride,
            srcVPtr, v_rowStride,

    (uint8_t *) temp_y, height,
    (uint8_t *) temp_u, height >> 1,
    (uint8_t *) temp_v, height >> 1,

    width, height,
    libyuv::kRotate0
    );

    libyuv::I420ToABGR(
    (uint8_t *) temp_y, height,
    (uint8_t *) temp_u, height >> 1,
    (uint8_t *) temp_v, height >> 1,

    (uint8_t *) argb, height * 4,
    height, width
    );


    env->ReleaseByteArrayElements(argb_, argb, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_me_harris_libyuv_YuvUtils_yuvI420ToABGRWithScale(JNIEnv *env, jobject type, jbyteArray argb_,
    jobject y_buffer, jint y_rowStride,
    jobject u_buffer, jint u_rowStride,
    jobject v_buffer, jint v_rowStride,
    jint width, jint height,
    jint scale, jint rotation) {
    jbyte *argb = env->GetByteArrayElements(argb_, NULL);

    uint8_t *srcYPtr = reinterpret_cast<uint8_t *>(env->GetDirectBufferAddress(y_buffer));
    uint8_t *srcUPtr = reinterpret_cast<uint8_t *>(env->GetDirectBufferAddress(u_buffer));
    uint8_t *srcVPtr = reinterpret_cast<uint8_t *>(env->GetDirectBufferAddress(v_buffer));

    int scaleW = width / scale;
    int scaleH = height / scale;
    int scaleSize = scaleW * scaleH;
    jbyte *temp_y_scale = new jbyte[scaleSize * 3 / 2];
    jbyte *temp_u_scale = temp_y_scale + scaleSize;
    jbyte *temp_v_scale = temp_y_scale + scaleSize + scaleSize / 4;

    libyuv::I420Scale(
            srcYPtr, y_rowStride,
            srcUPtr, u_rowStride,
            srcVPtr, v_rowStride,
            width, height,
    (uint8_t *) temp_y_scale, scaleW,
    (uint8_t *) temp_u_scale, scaleW >> 1,
    (uint8_t *) temp_v_scale, scaleW >> 1,
    scaleW, scaleH,
    libyuv::kFilterNone
    );

    //    width = scaleW;
    //    height = scaleH;
    jbyte *temp_y = new jbyte[scaleSize * 3 / 2];
    jbyte *temp_u = temp_y + scaleSize;
    jbyte *temp_v = temp_y + scaleSize + scaleSize / 4;

    LOGE("width = %d", width);
    LOGE("scaleW = %d", scaleW);
    LOGE("rotation = %d", rotation);

    if (rotation == 90) {
    libyuv::I420Rotate(
    (uint8_t *) temp_y_scale, scaleW,
    (uint8_t *) temp_u_scale, scaleW >> 1,
    (uint8_t *) temp_v_scale, scaleW >> 1,
    //
    (uint8_t *) temp_y, scaleH,
    (uint8_t *) temp_u, scaleH >> 1,
    (uint8_t *) temp_v, scaleH >> 1,

    scaleW, scaleH,
    libyuv::kRotate90
    );

    libyuv::I420ToABGR(
    (uint8_t *) temp_y, scaleH,
    (uint8_t *) temp_u, scaleH >> 1,
    (uint8_t *) temp_v, scaleH >> 1,

    (uint8_t *) argb, scaleH * 4,
    scaleH, scaleW
    );
    } else if (rotation == 270) {
    libyuv::I420Rotate(
    (uint8_t *) temp_y_scale, scaleW,
    (uint8_t *) temp_u_scale, scaleW >> 1,
    (uint8_t *) temp_v_scale, scaleW >> 1,
    //
    (uint8_t *) temp_y, scaleH,
    (uint8_t *) temp_u, scaleH >> 1,
    (uint8_t *) temp_v, scaleH >> 1,

    scaleW, scaleH,
    libyuv::kRotate270
    );

    libyuv::I420ToABGR(
    (uint8_t *) temp_y, scaleH,
    (uint8_t *) temp_u, scaleH >> 1,
    (uint8_t *) temp_v, scaleH >> 1,

    (uint8_t *) argb, scaleH * 4,
    scaleH, scaleW
    );
    } else if (rotation == 180) {
    libyuv::I420Rotate(
    (uint8_t *) temp_y_scale, scaleW,
    (uint8_t *) temp_u_scale, scaleW >> 1,
    (uint8_t *) temp_v_scale, scaleW >> 1,
    //
    (uint8_t *) temp_y, scaleW,
    (uint8_t *) temp_u, scaleW >> 1,
    (uint8_t *) temp_v, scaleW >> 1,

    scaleW, scaleH,
    libyuv::kRotate180
    );

    libyuv::I420ToABGR(
    (uint8_t *) temp_y, scaleW,
    (uint8_t *) temp_u, scaleW >> 1,
    (uint8_t *) temp_v, scaleW >> 1,

    (uint8_t *) argb, scaleW * 4,
    scaleW, scaleH
    );
    } else {
    libyuv::I420Rotate(
    (uint8_t *) temp_y_scale, scaleW,
    (uint8_t *) temp_u_scale, scaleW >> 1,
    (uint8_t *) temp_v_scale, scaleW >> 1,
    //
    (uint8_t *) temp_y, scaleW,
    (uint8_t *) temp_u, scaleW >> 1,
    (uint8_t *) temp_v, scaleW >> 1,

    scaleW, scaleH,
    libyuv::kRotate0
    );

    libyuv::I420ToABGR(
    (uint8_t *) temp_y, scaleW,
    (uint8_t *) temp_u, scaleW >> 1,
    (uint8_t *) temp_v, scaleW >> 1,

    (uint8_t *) argb, scaleW * 4,
    scaleW, scaleH
    );
    }

    env->ReleaseByteArrayElements(argb_, argb, 0);
}




extern "C"
JNIEXPORT jint JNICALL
Java_com_me_harris_libyuv_YuvUtils_NV21ToRGBA(JNIEnv *env, jobject clazz, jint width, jint height,
                                              jobject yuv, jint stride_y, jint stride_uv,
                                              jobject out) {
    auto src_y = (uint8_t *) env->GetDirectBufferAddress(yuv);
    auto src_uv = src_y + height * stride_y;

    auto dst_rgba = (uint8_t *) env->GetDirectBufferAddress(out);

    return libyuv::NV12ToABGR(src_y, stride_y, src_uv, stride_uv, dst_rgba, width * 4, width,
                              height);

}

extern "C"
JNIEXPORT jint JNICALL
Java_com_me_harris_libyuv_YuvUtils_yuv420ToArgb(JNIEnv *env, jobject clazz, jobject y, jobject u,
                                                jobject v, jint y_stride, jint u_stride,
                                                jint v_stride, jobject out, jint out_stride,
                                                jint width, jint height) {


    uint8_t *yNative = (uint8_t *) env->GetDirectBufferAddress(y);
    uint8_t *uNative = (uint8_t *) env->GetDirectBufferAddress(u);
    uint8_t *vNative = (uint8_t *) env->GetDirectBufferAddress(v);

    uint8_t *outNative = (uint8_t *) env->GetDirectBufferAddress(out);

   return libyuv::I420ToARGB(yNative, y_stride,
                       vNative, v_stride, // exactly this order "YVU" and not "YUV", otherwise the colors are inverted
                       uNative, u_stride,
                       outNative, out_stride,
                       width, height);
}



extern "C"
JNIEXPORT void JNICALL
Java_com_me_harris_libyuv_YuvUtils_convertToI420(JNIEnv *env, jobject thiz, jobject y, jobject u,
                                                 jobject v, jint y_stride, jint u_stride,
                                                 jint v_stride, jint src_pixel_stride_uv,
                                                 jobject y_out, jobject u_out, jobject v_out,
                                                 jint y_out_stride, jint u_out_stride,
                                                 jint v_out_stride, jint width, jint height) {
    uint8_t *yNative = (uint8_t *) env->GetDirectBufferAddress(y);
    uint8_t *uNative = (uint8_t *) env->GetDirectBufferAddress(u);
    uint8_t *vNative = (uint8_t *) env->GetDirectBufferAddress(v);

    uint8_t *yOutNative = (uint8_t *) env->GetDirectBufferAddress(y_out);
    uint8_t *uOutNative = (uint8_t *) env->GetDirectBufferAddress(u_out);
    uint8_t *vOutNative = (uint8_t *) env->GetDirectBufferAddress(v_out);

    libyuv::Android420ToI420(yNative, y_stride,
                             uNative, u_stride,
                             vNative, v_stride,
                             src_pixel_stride_uv,
                             yOutNative, y_out_stride,
                             uOutNative, u_out_stride,
                             vOutNative, v_out_stride,
                             width, height);
}