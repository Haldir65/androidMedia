package com.me.harris.playerLibrary.video.player

import android.util.Log
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
        if (this.context.getSurface()!=null) {
            this.context.threadManner?.pause()
            this.context.updateState(PlayerState.PAUSED)
        }
    }

    override fun resume() {
        if (this.context.getSurface()!=null){
            this.context.updateState(PlayerState.PLAYING)
            this.context.threadManner?.resume()
        }
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

    fun isSeeking():Boolean{
        return state() == PlayerState.SEEKING
    }

    fun isPaused():Boolean {
        return state() == PlayerState.PAUSED
    }

    override fun seekTo(msec: Long) {
        if (state()!= PlayerState.SEEKING){
            this.context.updateState(PlayerState.SEEKING)
            this.context.avSynchronizer!!.seekVideoAndAudio(msec)
            Log.e("=A=","【Seeking】 now seek to $msec ")
        }else {
            Log.e("=A=","【Seeking】 skip seek to $msec since we are in the process of seeking")
        }
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
