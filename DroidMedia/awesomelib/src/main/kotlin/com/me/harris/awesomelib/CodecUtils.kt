package com.me.harris.awesomelib

import android.media.MediaCodecInfo
import android.util.Log
import java.nio.ByteBuffer

object CodecUtils
{

    const val TAG = "CodecUtils"

    @JvmStatic
     fun logColorFormat( format:Int){
        when(format){
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV411Planar ->{
                Log.d(TAG,"color format COLOR_FormatYUV411Planar ${format}")
            }
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV411PackedPlanar ->{
                Log.d(TAG,"color format COLOR_FormatYUV411PackedPlanar ${format}")
            }
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar ->{
                Log.d(TAG,"color format COLOR_FormatYUV420PackedPlanar ${format}")
            }
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar ->{
                Log.d(TAG,"color format COLOR_FormatYUV420SemiPlanar ${format}")
            }
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar ->{
                Log.d(TAG,"color format COLOR_FormatYUV420PackedSemiPlanar ${format}")
            }
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar ->{
                Log.d(TAG,"color format COLOR_FormatYUV420Planar ${format}")
            }
            0x7FA30C04 -> {
                Log.d(TAG,"color format OMX_QCOM_COLOR_FormatYUV420PackedSemiPlanar32m  ${format}")
            }
            else ->{
                Log.d(TAG,"color format unkonwn ${format}")
            }
        }
    }

    // MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:  yuv420sp 转 Yuv420P 的方法：
    @JvmStatic
    fun yuv420spToYuv420P(yuv420spData:ByteArray , width:Int, height:Int):ByteArray{
        val yuv420pData = ByteArray(width * height *3 /2)
        val ySize = width * height
        System.arraycopy(yuv420spData,0,yuv420pData,0,ySize) // 拷贝y分量
        var  i = 0
        for (j in 0 until ySize/2 step 2){
            yuv420pData[ySize+i] = yuv420spData[ySize+j]  //u分量
            yuv420pData[ySize*5/4 + i] = yuv420spData[ySize+j + 1] //v分量
            i++
        }
        return yuv420pData
    }


    @JvmStatic
     fun I420ToNv21(i420bytes: ByteArray, width: Int, height: Int): ByteArray? {
        val nv21bytes = ByteArray(i420bytes.size)
        val y_len = width * height
        val uv_len = y_len / 4
        System.arraycopy(i420bytes, 0, nv21bytes, 0, y_len)
        for (i in 0 until uv_len) {
            val u = i420bytes[y_len + i]
            val v = i420bytes[y_len + uv_len + i]
            nv21bytes[y_len + i * 2] = v
            nv21bytes[y_len + i * 2 + 1] = u
        }
        return nv21bytes
    }


    @JvmStatic
   fun nv21ToI420(data:ByteArray ,width:Int, height: Int):ByteArray{
       val ret = ByteArray(data.size)
       val total = width * height
       val bufferY = ByteBuffer.wrap(ret,0,total)
       val bufferU = ByteBuffer.wrap(ret,total,total/4)
       val bufferV = ByteBuffer.wrap(ret,total + total/4 ,total/4)

       bufferY.put(data,0,total)
       for (i in total until data.size step 2){
           bufferV.put(data[i])
           bufferU.put(data[i+1])
       }
       return ret
   }

}