package com.me.harris.libyuv

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

object ImageToBitmap {

    // critical !!
     fun getBitmapFromImageUsingLibYUV(image: Image): Bitmap {
        require(image.format == ImageFormat.YUV_420_888)
        val yuvFrame = YuvUtils.convertToI420(image)
        val argbFrame = YuvUtils.yuv420ToArgb(yuvFrame)
        val bm = Bitmap.createBitmap(argbFrame.width, argbFrame.height, Bitmap.Config.ARGB_8888)
        bm.copyPixelsFromBuffer(ByteBuffer.wrap(argbFrame.asArray())) // for displaying argb
        return bm
    }

    /**
     * 色彩正常，速度正常，
     */
     fun getBitMapFromImageUsingYUVImage(image: Image, scale: Int, rotation: Int): Bitmap {
//        if (mYuvBuffer == null){
//            mYuvBuffer = ByteArray(image.width*image.height*4)
//        }
        val mYuvBuffer = ByteArray(image.width*image.height*4)
        val buffer= requireNotNull(mYuvBuffer)
        // copy from VideoDecoder.getDataFromImage
        val crop: Rect = image.cropRect
        val width = image.width
        val height = image.height
        // image.getFormat()  == ImageFormat.YUV_420_888
        val planes: Array<Image.Plane> = image.planes
        val rowData = ByteArray(planes[0].rowStride)
        var channelOffset = 0
        var outputStride = 1
        for (i in 0 until planes.size){
            when(i){
                0 -> {
                    channelOffset = 0
                    outputStride = 1
                }
                1 -> {
                    channelOffset = width * height + 1
                    outputStride = 2
                }
                2 -> {
                    channelOffset = width * height
                    outputStride = 2
                }
                else ->{

                }
            }
            val pBuffer = planes[i].buffer
            val rowStride = planes[i].rowStride
            val pixelStride = planes[i].pixelStride

            val shift = if (i == 0) 0 else 1
            val w = width shr shift
            val h = height shr shift
            pBuffer.position(rowStride * (crop.top shr shift) + pixelStride * (crop.left shr shift))
            for (row in 0 until h) {
                var length: Int
                if (pixelStride == 1 && outputStride == 1) {
                    length = w
                    pBuffer.get(mYuvBuffer, channelOffset, length)
                    channelOffset += length
                } else {
                    length = (w - 1) * pixelStride + 1
                    pBuffer.get(rowData, 0, length)
                    for (col in 0 until w) {
                        mYuvBuffer!![channelOffset] = rowData[col * pixelStride]
                        channelOffset += outputStride
                    }
                }
                if (row < h - 1) {
                    pBuffer.position(pBuffer.position() + rowStride - length)
                }
            }
        }
        return yuvDataToBitMap(buffer,width,height,width,height)
    }

    private fun yuvDataToBitMap(
        data: ByteArray,
        pWidth: Int,
        pHeight: Int,
        bWidth: Int,
        bHeight: Int
    ): Bitmap {
        // pWidth and pHeight define the size of the preview Frame
        val out = ByteArrayOutputStream(data.size)

// Alter the second parameter of this to the actual format you are receiving
        val yuv = YuvImage(data, ImageFormat.NV21, pWidth, pHeight, null)

// bWidth and bHeight define the size of the bitmap you wish the fill with the preview image
        yuv.compressToJpeg(Rect(0, 0, bWidth, bHeight), 100, out)
        val bytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

}