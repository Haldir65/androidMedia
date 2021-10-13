package com.me.harris.droidmedia.video.sharedSurface

import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import com.me.harris.droidmedia.utils.VideoUtil

object SharedSurfaceManager {

    var mSurfaceTexture:SurfaceTexture? = null

     var mPlayer:MediaPlayer? = null

    var playingUrl = VideoUtil.strVideo



}