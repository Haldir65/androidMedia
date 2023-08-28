package com.me.harris.extractframe.finale.creator

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.media.Image
import com.me.harris.libyuv.Constants
import com.me.harris.libyuv.ImageToBitmap
import com.me.harris.libyuv.YuvUtils
import java.nio.ByteBuffer

class ExtractBitMapStoreK {

    var bitmap:Bitmap? = null
    var byeArray:ByteArray? = null

    fun getBitmapFromImageUsingLibYUV(image:Image):Bitmap{
        require(image.format == ImageFormat.YUV_420_888)
        var yuvFrame = YuvUtils.convertToI420(image)
//        yuvFrame = YuvUtils.scale(yuvFrame, image.width/4, image.height/4, Constants.FILTER_BOX) //   this is why we keep crash in the past,二分法确定
        yuvFrame = YuvUtils.rotate(yuvFrame, Constants.ROTATE_0)
        val argbFrame = YuvUtils.yuv420ToArgb(yuvFrame)
        val bm = bitmap?:Bitmap.createBitmap(argbFrame.width, argbFrame.height, Bitmap.Config.ARGB_8888).also { this.bitmap = it }
        bm.copyPixelsFromBuffer(ByteBuffer.wrap(argbFrame.asArray())) // for displaying argb
        return bm
    }


}
