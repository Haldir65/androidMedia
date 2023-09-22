package com.me.harris.playerLibrary.video.vm

import android.app.Application
import android.media.MediaCodecInfo
import android.media.MediaCodecList
import androidx.lifecycle.AndroidViewModel

class MediaCodeMain2ViewModel( app:Application):AndroidViewModel(app) {


    fun probeVideoInfo(){

        //获取所支持的编码信息的方法

        //获取所支持的编码信息的方法
        val mEncoderInfos = HashMap<String, MediaCodecInfo.CodecCapabilities>()
        for (i in MediaCodecList.getCodecCount() - 1 downTo 0) {
            val codecInfo = MediaCodecList.getCodecInfoAt(i)
            if (codecInfo.isEncoder) {
                for (t in codecInfo.supportedTypes) {
                    try {
                        mEncoderInfos[t] = codecInfo.getCapabilitiesForType(t)
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}
