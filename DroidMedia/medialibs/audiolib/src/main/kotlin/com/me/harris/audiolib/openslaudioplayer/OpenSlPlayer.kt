package com.me.harris.audiolib.openslaudioplayer

class OpenSlPlayer {

  companion object {
      init {
          System.loadLibrary("myaudio")
      }
  }


    private  var  nativePtr:Long  = -1


    external fun createPlayer(channelCount:Int, sampleRate:Int)

}
