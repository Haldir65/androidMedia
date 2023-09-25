package com.me.harris.playerLibrary.video.player.subordinates

import com.me.harris.playerLibrary.video.player.decode.AudioDecodeThread
import com.me.harris.playerLibrary.video.player.video.VideoDecodeThread
import com.me.harris.playerLibrary.video.player.MediaCodecPlayerContext

/**
 * 管理线程吧
 */
class MediaCodecThreadManner(val context: MediaCodecPlayerContext) {

    private var videoDecodeThread: VideoDecodeThread? = null

    private var audioDecodeThread: AudioDecodeThread? = null

    fun start() {
        stop()
        videoDecodeLoop()
        audioDecodeLoop()
    }

    fun stop() {
        videoDecodeThread?.stop = true
        audioDecodeThread?.stop = true
        videoDecodeThread?.interrupt()
        audioDecodeThread?.interrupt()
        videoDecodeThread = null
        audioDecodeThread = null
    }


    fun resume(){

    }

    fun release(){
        stop()
    }

    private fun videoDecodeLoop() {
        val path = requireNotNull(context.getDataSource())
        val surface = requireNotNull(context.getSurface())
        VideoDecodeThread(surface = surface, path = path,context = context).apply {
            videoDecodeThread = this
            start()
        }
    }

    private fun audioDecodeLoop() {
        val path = requireNotNull(context.getDataSource())
        AudioDecodeThread(path = path,context = context).apply {
            audioDecodeThread = this
            start()
        }
    }

    fun setMute(mute:Boolean){
        requireNotNull(audioDecodeThread).mute = mute
    }
}
