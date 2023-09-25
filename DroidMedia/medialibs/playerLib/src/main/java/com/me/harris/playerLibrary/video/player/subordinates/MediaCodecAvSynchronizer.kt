package com.me.harris.playerLibrary.video.player.subordinates

import com.me.harris.playerLibrary.video.player.MediaCodecPlayerContext

class MediaCodecAvSynchronizer(val context:MediaCodecPlayerContext) {

//    private var _currentPosition:Long = 0
    fun getCurrentPosition():Long = _videoPtsMicroSeconds/1000

     var _audioPtsMicroSeconds:Long = 0

     var _videoPtsMicroSeconds:Long = 0
}
