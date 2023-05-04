package com.me.harris.playerLibrary.video.sharedSurface

import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import com.me.harris.awesomelib.utils.VideoUtil

object SharedSurfaceManager {

    var mSurfaceTexture:SurfaceTexture? = null

     var mPlayer:MediaPlayer? = null

    var playingUrl = VideoUtil.strVideo



}