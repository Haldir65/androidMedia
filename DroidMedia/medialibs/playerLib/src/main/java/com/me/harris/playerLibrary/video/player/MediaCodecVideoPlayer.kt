package com.me.harris.playerLibrary.video.player

import android.view.Surface
import com.me.harris.playerLibrary.video.player.internal.PlayerState

class MediaCodecVideoPlayer :IMediaPlayer{

    val context = MediaCodecPlayerContext()

    private fun state():PlayerState = context.state

    override fun setDataSource(path: String) {
       context.setDataSource(path)
    }

    override fun setSurface(sf: Surface) {
        context.setSurface(sf)
    }

    override fun prepareAsync() {
        error("unimplemented")
    }

    override fun start() {
        requireNotNull(context.threadManner).start()
    }

    override fun stop() {
        requireNotNull(context.threadManner).stop()
    }

    override fun pause() {
        this.context.updateState(PlayerState.PAUSED)
    }

    override fun resume() {
        this.context.updateState(PlayerState.PLAYING)
    }

    override fun prepare() {
        context.prepare()
    }

    override fun setScreenOnWhilePlaying(screenOn: Boolean) {
        error("call surfaceView.holder.setScreenOnWhilePlaying yourself , don't rely on me ! ")
    }

    override fun getVideoWidth(): Int {
        return context.getWidth()
    }

    override fun getVideoHeight(): Int {
        return context.getHeight()
    }

    override fun isPlaying(): Boolean {
       return state() == PlayerState.PLAYING
    }

    override fun seekTo(msec: Long) {
        this.context.updateState(PlayerState.SEEKING)
    }

    override fun getCurrentPosition(): Long {
       return context.getCurrentPosition()
    }

    override fun reset() {

    }

    override fun release() {
        requireNotNull(context.threadManner).release()
    }

    override fun setMute(mute: Boolean) {
        requireNotNull(context.threadManner).setMute(mute)
    }

    override fun getDuration(): Long {
        return context.getDuration()
    }
}
