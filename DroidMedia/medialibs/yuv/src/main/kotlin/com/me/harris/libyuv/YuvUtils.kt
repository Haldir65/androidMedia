package com.me.harris.libyuv

import android.media.Image
import com.me.harris.libyuv.entities.ArgbFrame
import com.me.harris.libyuv.entities.YuvFrame
import java.nio.ByteBuffer

object YuvUtils {


        init {
            System.loadLibrary("yuvconvert-lib")
        }


    /**
     * 将I420转化为NV21
     *
     * @param i420Src 原始I420数据
     * @param nv21Src 转化后的NV21数据
     * @param width   输出的宽
     * @param width   输出的高
     */
    external fun yuvI420ToNV21(i420Src: ByteArray?, nv21Src: ByteArray?, width: Int, height: Int)
    external fun yuvI420ToNV212(
        nv21: ByteArray?,
        y_buffer: ByteBuffer?,
        rowStride: Int,
        buffer1: ByteBuffer?,
        rowStride1: Int,
        buffer2: ByteBuffer?,
        rowStride2: Int,
        width: Int,
        height: Int
    )

    external fun yuvI420ToABGR(
        argb: ByteArray?,
        y_buffer: ByteBuffer?,
        rowStride: Int,
        buffer1: ByteBuffer?,
        rowStride1: Int,
        buffer2: ByteBuffer?,
        rowStride2: Int,
        width: Int,
        height: Int
    )

    external fun yuvI420ToABGRWithScale(
        argb: ByteArray?,
        y_buffer: ByteBuffer?,
        rowStride: Int,
        buffer1: ByteBuffer?,
        rowStride1: Int,
        buffer2: ByteBuffer?,
        rowStride2: Int,
        width: Int,
        height: Int,
        scale: Int,
        rotation: Int
    )

    external fun NV21ToRGBA(
        width: Int,
        height: Int,
        yuv: ByteBuffer?,
        strideY: Int,
        strideUV: Int,
        out: ByteBuffer?
    ): Int

    fun yuv420ToArgb(yuvFrame: YuvFrame): ArgbFrame {
        val outFrame = FramesFactory.instanceArgb(yuvFrame.width, yuvFrame.height)
        yuv420ToArgb(
            yuvFrame.y,
            yuvFrame.u,
            yuvFrame.v,
            yuvFrame.yStride,
            yuvFrame.uStride,
            yuvFrame.vStride,
            outFrame.data,
            outFrame.dataStride,
            yuvFrame.width,
            yuvFrame.height
        )
        return outFrame
    }

    external fun yuv420ToArgb(
        y: ByteBuffer?, u: ByteBuffer?, v: ByteBuffer?, yStride: Int, uStride: Int,
        vStride: Int, out: ByteBuffer?, outStride: Int, width: Int, height: Int
    ): Int


    fun convertToI420(image: Image): YuvFrame {
        val outFrame = FramesFactory.instanceYuv(image.width, image.height)
        convertToI420(
            image.planes[0].buffer,
            image.planes[1].buffer,
            image.planes[2].buffer,
            image.planes[0].rowStride,
            image.planes[1].rowStride,
            image.planes[2].rowStride,
            image.planes[2].pixelStride,
            outFrame.y,
            outFrame.u,
            outFrame.v,
            outFrame.yStride,
            outFrame.uStride,
            outFrame.vStride,
            image.width,
            image.height
        )
        return outFrame
    }

     external fun convertToI420(
        y: ByteBuffer,
        u: ByteBuffer,
        v: ByteBuffer,
        yStride: Int,
        uStride: Int,
        vStride: Int,
        srcPixelStrideUv: Int,
        yOut: ByteBuffer,
        uOut: ByteBuffer,
        vOut: ByteBuffer,
        yOutStride: Int,
        uOutStride: Int,
        vOutStride: Int,
        width: Int,
        height: Int
    )
}