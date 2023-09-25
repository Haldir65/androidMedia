package com.me.harris.playerLibrary.video.player.subordinates

import android.util.Log
import com.me.harris.playerLibrary.video.player.MediaCodecPlayerContext
import com.me.harris.playerLibrary.video.player.internal.PlayerState

class MediaCodecAvSynchronizer(val context:MediaCodecPlayerContext) {

//    private var _currentPosition:Long = 0
    fun getCurrentPosition():Long = _videoPtsMicroSeconds/1000

     var _audioPtsMicroSeconds:Long = 0

     var _videoPtsMicroSeconds:Long = 0


    var mAudioSeekPositionMs:Long = -1
    var mVideoSeekPositionMs:Long = -1
    fun seekVideoAndAudio(ms:Long){
        mAudioSeekPositionMs = ms
        mVideoSeekPositionMs = ms
    }

    fun seekVideoCompleted(){
        Log.w("=A=","【视频】seek到${mVideoSeekPositionMs}ms seekCompleted  ${Thread.currentThread().name} 当前音频pending position = ${mAudioSeekPositionMs}")
        mVideoSeekPositionMs = -1
        restoreState()
    }

    fun seekAudioCompleted(){
        Log.w("=A=","【音频】seek到${mAudioSeekPositionMs}ms seekCompleted ${Thread.currentThread().name} 当前视频pending position = ${mVideoSeekPositionMs}")
        mAudioSeekPositionMs = -1
        restoreState()
    }

    private fun restoreState(){
        if (mAudioSeekPositionMs ==-1L && mVideoSeekPositionMs==-1L){
            context.updateState(PlayerState.PLAYING)
            Log.w("=A=","【视频和音频都seek完成】seekAudioCompleted and seekVideoCompleted ${Thread.currentThread().name}")
        }
    }


}
