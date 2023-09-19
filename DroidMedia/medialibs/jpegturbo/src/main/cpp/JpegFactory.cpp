
#include "JpegFactory.h"

jobject JpegFactory::create_bitmap(JNIEnv *env, int width, int height) {
    // 找到 Bitmap.class 和 该类中的 createBitmap 方法
    jclass clz_bitmap = env->FindClass("android/graphics/Bitmap");
    jmethodID mtd_bitmap = env->GetStaticMethodID(
            clz_bitmap, "createBitmap",
            "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");

    // 配置 Bitmap
    jstring str_config = env->NewStringUTF("ARGB_8888");
    jclass clz_config = env->FindClass("android/graphics/Bitmap$Config");
    jmethodID mtd_config = env->GetStaticMethodID(
            clz_config, "valueOf", "(Ljava/lang/String;)Landroid/graphics/Bitmap$Config;");
    jobject obj_config = env->CallStaticObjectMethod(clz_config, mtd_config, str_config);

    // 创建 Bitmap 对象
    jobject bitmap = env->CallStaticObjectMethod(
            clz_bitmap, mtd_bitmap, width, height, obj_config);
    return bitmap;
}
