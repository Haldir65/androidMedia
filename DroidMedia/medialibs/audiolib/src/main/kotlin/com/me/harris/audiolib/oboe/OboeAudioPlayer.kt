package com.me.harris.audiolib.oboe

import android.content.res.AssetManager

class OboeAudioPlayer {
    companion object {
        init {
            System.loadLibrary("myaudio")
        }
    }


    external fun startPlaying( fileName:String,assetmanager:AssetManager,sampleRate:Int);
    external fun stopPlaying();
}
