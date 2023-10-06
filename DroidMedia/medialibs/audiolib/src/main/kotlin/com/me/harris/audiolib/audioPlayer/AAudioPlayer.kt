package com.me.harris.audiolib.audioPlayer

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.me.harris.audiolib.audioPlayer.interfaces.AudioPlayInterface


// https://github.com/MRYangY/AAudioDemo
// https://zhuanlan.zhihu.com/p/603942737
//
@RequiresApi(value = Build.VERSION_CODES.O)
class AAudioPlayer(val filepath:String,val context:Context,val sampleRate:Int = AUDIO_SAMPLERATE,val channel_count:Int = AUDIO_CHANNELS) : AudioPlayInterface {

    companion object {
        init {
            System.loadLibrary("myaudio")
        }
        private val INVALID_PTR = 0L
        private const val AUDIO_SAMPLERATE = 44100
        private const val AUDIO_CHANNELS = 2
        private const val AUDIO_FORMAT = 2

    }

    private var mEngineHandle = INVALID_PTR.toLong()
    override fun play() {
        if (mEngineHandle == INVALID_PTR) {
            mEngineHandle = nativeCreateAAudioEngine(filepath, sampleRate, channel_count, AUDIO_FORMAT);
            nativeAAudioEnginePlay(mEngineHandle)
        }
    }

    override fun stop() {
        if (mEngineHandle!= INVALID_PTR){
            nativeAAudioEngineStop(mEngineHandle)
        }
    }

    fun destroy(){
        if (mEngineHandle!= INVALID_PTR){
            nativeDestroyAAudioEngine(mEngineHandle)
            mEngineHandle = INVALID_PTR
        }
    }


    external fun nativeCreateAAudioEngine(
        filePath: String?,
        sampleRate: Int,
        audioChannel: Int,
        audioFormat: Int
    ): Long

    external fun nativeDestroyAAudioEngine(engineHandle: Long)

    external fun nativeAAudioEnginePlay(engineHandle: Long)

    external fun nativeAAudioEnginePause(engineHandle: Long)

    external fun nativeAAudioEngineStop(engineHandle: Long)

}
