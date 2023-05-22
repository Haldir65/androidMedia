package com.me.harris.extractframe.yuvrelated

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.media.Image
import android.media.ImageReader
import android.util.Log
import com.me.harris.libyuv.ImageToBitmap
import com.me.harris.libyuv.YuvUtils
import java.nio.ByteBuffer

  class MyOnImageAvailableListener(
    val callback: (bmp: Bitmap) -> Unit,
    val scale: Int,
    val rotation: Int
) : ImageReader.OnImageAvailableListener {


    override fun onImageAvailable(reader: ImageReader?) {

        var img: Image? = null
        try {
            img = reader?.acquireLatestImage() ?: return
            img.planes?.get(0)?.buffer ?: return
            require(img.format== ImageFormat.YUV_420_888)

//           val bmp = getBitmapScale(img, scale, rotation) // 色彩不对
//           val bmp = getBitmapUsingJava(img) // 顺时针转了90度？ 色彩也不对
//            val bmp = getBitmapUsingNV21(img) // crashy

//            val bmp = ImageToBitmap.getBitMapFromImageUsingYUVImage(img,scale,rotation) // Java YUVImage，一切正常
            val bmp = ImageToBitmap.getBitmapFromImageUsingLibYUV(img)// libyuv , 表现正常
            Log.w(MediaCodecFrameExtractor.TAG,"got one bitmap not null!!")
            callback(bmp)
        } catch (e:Exception){
            Log.w(MediaCodecFrameExtractor.TAG,"having some trouble onImageAvailable ${e.stackTraceToString()} ")

        } finally {
            img?.close() /// crital !!!!  用完就关闭，否则只能回调3次
        }


    }

    private fun getBitmapUsingJava(image: Image): Bitmap {
        val width = image.width
        val height = image.height
        val i420bytes = CameraUtil.getDataFromImage(image, CameraUtil.COLOR_FormatI420)

        val i420RorateBytes = BitmapUtil.rotateYUV420Degree90(i420bytes, width, height)
        val nv21bytes = BitmapUtil.I420Tonv21(i420RorateBytes, height, width)
        //TODO check YUV数据是否正常
        // BitmapUtil.dumpFile("mnt/sdcard/1.yuv", i420bytes);

        //TODO check YUV数据是否正常
        // BitmapUtil.dumpFile("mnt/sdcard/1.yuv", i420bytes);
        val bitmap = BitmapUtil.getBitmapImageFromYUV(nv21bytes, height, width)
        return bitmap

    }

    private fun getBitmapUsingNV21(img: Image): Bitmap {
        val nv21bytes = BitmapUtil.toNv21(img)
        val width = img.width
        val height = img.height
        val yuvSrc = ByteBuffer.allocateDirect(nv21bytes.size)
        yuvSrc.put(nv21bytes)
        val outBuffer = ByteBuffer.allocateDirect((width / scale) * (height / scale) * 4)
        require(img.format == ImageFormat.YUV_420_888)
        val strideY = img.planes[0].pixelStride
        val uvStride = img.planes[1].pixelStride
        YuvUtils.NV21ToRGBA(width,height,yuvSrc,strideY,uvStride,outBuffer)
        //  backtrace:
        //2023-05-22 14:15:42.103 20333-20333 DEBUG                   pid-20333                            A        #00 pc 0000000000369bb0  /apex/com.android.runtime/lib64/libart.so (art::(anonymous namespace)::ScopedCheck::CheckArray(art::ScopedObjectAccess&, _jarray*)+96) (BuildId: 8b52ee5427994d5851b0f85a2c15eedf)
        //2023-05-22 14:15:42.103 20333-20333 DEBUG                   pid-20333                            A        #01 pc 0000000000369080  /apex/com.android.runtime/lib64/libart.so (art::(anonymous namespace)::ScopedCheck::CheckPossibleHeapValue(art::ScopedObjectAccess&, char, art::(anonymous namespace)::JniValueType)+528) (BuildId: 8b52ee5427994d5851b0f85a2c15eedf)
        //2023-05-22 14:15:42.103 20333-20333 DEBUG                   pid-20333                            A        #02 pc 00000000003686d0  /apex/com.android.runtime/lib64/libart.so (art::(anonymous namespace)::ScopedCheck::Check(art::ScopedObjectAccess&, bool, char const*, art::(anonymous namespace)::JniValueType*)+652) (BuildId: 8b52ee5427994d5851b0f85a2c15eedf)
        //2023-05-22 14:15:42.103 20333-20333 DEBUG                   pid-20333                            A        #03 pc 000000000037535c  /apex/com.android.runtime/lib64/libart.so (art::(anonymous namespace)::CheckJNI::GetPrimitiveArrayRegion(char const*, art::Primitive::Type, _JNIEnv*, _jarray*, int, int, void*)+668) (BuildId: 8b52ee5427994d5851b0f85a2c15eedf)
        //2023-05-22 14:15:42.103 20333-20333 DEBUG                   pid-20333                            A        #04 pc 00000000003886b4  /system/framework/arm64/boot-core-libart.oat (art_jni_trampoline+196) (BuildId: 4a9d0ac75a0303354b17f438df9ba5a7ecd0f6d6)
        //2023-05-22 14:15:42.103 20333-20333 DEBUG                   pid-20333                            A        #05 pc 00000000001375b8  /apex/com.android.ru
        val bitmap  =  Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(outBuffer)
        return bitmap
    }



    private fun getBitmapScale(img: Image, scale: Int, rotation: Int): Bitmap {
        val width = img.width / scale
        val height = img.height / scale
        val imageWitdh = img.width
        val imageHeight = img.height
        // Read image data
        // Read image data

        val planes = img.planes

        val argb = ByteArray(imageWitdh / scale * imageHeight / scale * 4)

        //值得注意的是在Java层传入byte[]以RGBA顺序排列时，libyuv是用ABGR来表示这个排列
        //libyuv表示的排列顺序和Bitmap的RGBA表示的顺序是反向的。
        // 所以实际要调用libyuv::ABGRToI420才能得到正确的结果。
//        val outBuffer = ByteBuffer.allocateDirect(width / scale * height / scale * 4)
        YuvUtils.yuvI420ToABGRWithScale(
            argb,
            planes[0].buffer, planes[0].rowStride,
            planes[1].buffer, planes[1].rowStride,
            planes[2].buffer, planes[2].rowStride,
            imageWitdh, imageHeight,
            scale,
            rotation
        )
        val bitmap = if (rotation == 90 || rotation == 270) {
            Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888)
        } else {
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        }
        bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(argb))
        return bitmap
    }
}