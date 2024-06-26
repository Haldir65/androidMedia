package com.spx.library.player

import android.content.Context
import android.view.View
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

fun initPlayer(context: Context, videoUrl:String, playerView: PlayerView): ExoPlayer {
    var TAG ="initPlayer"

    var player = ExoPlayer.Builder(context).build()

    playerView.visibility = View.VISIBLE
    playerView.player = player
//    player!!.addListener(listener)

    player!!.repeatMode = Player.REPEAT_MODE_ALL
    player!!.playWhenReady = true


    val mediaItem: MediaItem = MediaItem.fromUri(videoUrl)
// Set the media item to be played.
// Set the media item to be played.
    player.setMediaItem(mediaItem)
// Prepare the player.
// Prepare the player.
    player.prepare()
// Start the playback.
// Start the playback.
//    player.play()
//    var mVideoSource = ExtractorMediaSource.Factory(DefaultDataSourceFactory(this, "spx")).createMediaSource(Uri.parse(videoUrl)!!)
//    player.prepare(mVideoSource)


    return player
}

//fun initPlayer(context: Context, videoUrl: String): SimpleExoPlayer {
//    val defaultSourceFactory = DefaultDataSourceFactory(context, "luedong")
//
//    var player = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector())
//
////    player!!.addListener(listener)
//
//    player!!.repeatMode = Player.REPEAT_MODE_ALL
//    player!!.playWhenReady = true
//
//    var mVideoSource = ExtractorMediaSource.Factory(defaultSourceFactory).createMediaSource(Uri.parse(videoUrl)!!)
//
//    if (player != null) {
//        player!!.prepare(mVideoSource)
//    }
//
//    return player
//}