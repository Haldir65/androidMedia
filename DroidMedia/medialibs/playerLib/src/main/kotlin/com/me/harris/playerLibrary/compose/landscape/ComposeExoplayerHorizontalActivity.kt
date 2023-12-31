package com.me.harris.playerLibrary.compose.landscape

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.me.harris.awesomelib.utils.VideoUtil

class ComposeExoplayerHorizontalActivity : AppCompatActivity() {

    @SuppressLint("UnsafeOptInUsageError")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = this
        val exoPlayer = ExoPlayer.Builder(context)
            .build()
            .apply {
                val defaultDataSourcFactpry = DefaultDataSource.Factory(context)
                val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(context, defaultDataSourcFactpry)
                val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(VideoUtil.strVideo))
                setMediaSource(source)
//                    setMediaItem(fromUri(videoURL))
                playWhenReady = true
                repeatMode = Player.REPEAT_MODE_ONE
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
                setTurnScreenOn(true)
                prepare()
            }
        val playerWrapper = PlayerWrapper(
            exoPlayer = exoPlayer
        )
        setContent {
            PlayerView(playerWrapper = playerWrapper, isFullScreen = false, onFullScreenToggle = ::toggleOrientation, onTrailerChange = {}, navigateBack = {

            }
            )
        }
    }

    private fun toggleOrientation(isFullScreen:Boolean){
        if (isFullScreen){
//            setPortrait()
        }else {
//            setLandscape()
        }
    }
}
