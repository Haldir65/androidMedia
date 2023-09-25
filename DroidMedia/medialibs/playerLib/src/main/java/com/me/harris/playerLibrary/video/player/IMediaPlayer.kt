package com.me.harris.playerLibrary.video.player

import android.view.Surface
import java.io.IOException

interface IMediaPlayer {

    @Throws(
        IOException::class,
        IllegalArgumentException::class,
        SecurityException::class,
        IllegalStateException::class
    )
    fun setDataSource(path: String)
//    fun setDisplay(sh: SurfaceHolder)
    fun setSurface(sf: Surface)

    @Throws(java.lang.IllegalStateException::class)
    fun prepareAsync()

    @Throws(java.lang.IllegalStateException::class)
    fun start()

    @Throws(java.lang.IllegalStateException::class)
    fun stop()

    @Throws(java.lang.IllegalStateException::class)
    fun pause()


    fun resume()

    fun prepare()


    fun setScreenOnWhilePlaying(screenOn: Boolean)

    fun getVideoWidth(): Int

    fun getVideoHeight(): Int

    fun isPlaying(): Boolean

    @Throws(java.lang.IllegalStateException::class)
    fun seekTo(msec: Long)

    /**
     *
     */
    fun getCurrentPosition(): Long

    fun reset()


    fun release()



    /**
     *
     */
    fun setMute(mute: Boolean)

    /**
     * ms
     */
    fun getDuration(): Long

//    fun isPlayComplete(): Boolean
}
