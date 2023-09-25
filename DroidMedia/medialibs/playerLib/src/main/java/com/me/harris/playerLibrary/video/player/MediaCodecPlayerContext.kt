package com.me.harris.playerLibrary.video.player

import android.view.Surface
import com.me.harris.playerLibrary.video.player.internal.PlayerState
import com.me.harris.playerLibrary.video.player.subordinates.MediaCodecAvSynchronizer
import com.me.harris.playerLibrary.video.player.subordinates.MediaCodecInMutableState
import com.me.harris.playerLibrary.video.player.subordinates.MediaCodecThreadManner

class MediaCodecPlayerContext {

    // 1. class
    // 音画同步？
    // current position
     var avSynchronizer:MediaCodecAvSynchronizer? = null


    // 2. decoder
    //
     var threadManner:MediaCodecThreadManner? = null


    // 3. 不大会变化的东西
    // data source
    // video duration
    // video width
    // video height
    // video rotation
    private var immutableStates: MediaCodecInMutableState? = null


    // 4. user interactive state ， 交互引发的一些状态变更
     var state: PlayerState = PlayerState.IDLE
        private set

    fun updateState(state: PlayerState){
        if (state!=this.state){
            this.state = state
        }
    }

    fun setDataSource(path: String) {
        this.immutableStates = MediaCodecInMutableState(filepath = path)
    }

    fun setSurface(sf: Surface) {
        this.immutableStates?.surface = sf
    }


    fun prepare(){
        threadManner = MediaCodecThreadManner(this)
        avSynchronizer = MediaCodecAvSynchronizer(this)
    }


    fun getWidth(): Int {
        immutableStates?.let { return it.width }
        return -1
    }

    fun getHeight(): Int {
        immutableStates?.let { return it.height }
        return -1
    }

    fun getDuration(): Long {
        immutableStates?.let { return it.videoDurationMicroSeconds/1000 }
        return 0
    }

    fun getCurrentPosition():Long {
        return avSynchronizer?.getCurrentPosition()?:0
    }

    fun getDataSource():String = immutableStates?.filepath.orEmpty()

    fun getSurface():Surface? = immutableStates?.surface



}

