package com.me.harris.droidmedia.textureview

import android.media.MediaCodec
import android.media.MediaExtractor
import android.util.Log

interface TypicalDecoder {

    fun decoderName():String

    fun isStopped():Boolean
    fun start(url:String)
    fun stop()

    fun extractor():MediaExtractor?

     fun sleepRender(audioBufferInfo: MediaCodec.BufferInfo, startMs: Long) {
        // 这里的时间是 毫秒  presentationTimeUs 的时间是累加的 以微秒进行一帧一帧的累加
        // audioBufferInfo 是改变的
        while (!isStopped()&&audioBufferInfo.presentationTimeUs / 1000 > System.currentTimeMillis() - startMs) {
            try {
                // 10 毫秒
                Log.d("=Render="," ${decoderName()}sleep 10 ms")
                Thread.sleep(10)
            } catch (e: InterruptedException) {
                e.printStackTrace()
                break
            }
        }
    }
}