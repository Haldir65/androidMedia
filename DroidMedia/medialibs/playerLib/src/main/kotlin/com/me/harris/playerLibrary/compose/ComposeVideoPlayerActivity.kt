package com.me.harris.playerLibrary.compose

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.*
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.C
import androidx.media3.common.MediaItem.fromUri
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.me.harris.awesomelib.utils.VideoUtil

class ComposeVideoPlayerActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        hideSystemUI()
//        ViewCompat.setOnApplyWindowInsetsListener(view) { view, windowInsets ->
//            val insets = windowInsets.getInsets(
//                WindowInsetsCompat.Type.systemGestures()
//            )
//            view.updatePadding(
//                insets.left,
//                insets.top,
//                insets.right,
//                insets.bottom
//            )
//            WindowInsetsCompat.CONSUMED
//        }
        setContent {
            ExoPlayerComp()
        }

    }

    fun hideSystemUI() {

        //Hides the ugly action bar at the top
        actionBar?.hide()

        //Hide the status bars

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.insetsController?.apply {
                hide(WindowInsets.Type.statusBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }
   @OptIn(UnstableApi::class)
   @Composable
   @Preview
    fun ExoPlayerComp() {
       requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
//            val videoURL       = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
            val videoURL       = VideoUtil.strVideo
            val context        = LocalContext.current
            val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

            val exoPlayer = ExoPlayer.Builder(context)
                .build()
                .apply {
                    val defaultDataSourcFactpry = DefaultDataSource.Factory(context)
                    val dataSourceFactory :DataSource.Factory = DefaultDataSource.Factory(context,defaultDataSourcFactpry)
                    val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(fromUri(videoURL))
                    setMediaSource(source)
//                    setMediaItem(fromUri(videoURL))
                    playWhenReady    = true
                    repeatMode = Player.REPEAT_MODE_ONE
                    videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
                    setTurnScreenOn(true)
                    prepare()
                }

            DisposableEffect(
                key1 = AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = {
                        PlayerView(context).apply {
//                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                            player = exoPlayer
//                            useController = false
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                        }
                    }),
                effect = {
                    val observer = LifecycleEventObserver { _, event ->
                        when (event) {
                            Lifecycle.Event.ON_RESUME -> {
                                Log.e("LIFECYCLE", "resumed")
                                exoPlayer.play()
                            }
                            Lifecycle.Event.ON_PAUSE  -> {
                                Log.e("LIFECYCLE", "paused")
                                exoPlayer.stop()
                            }
                            else ->{

                            }
                        }
                    }

                    val lifecycle = lifecycleOwner.value.lifecycle
                    lifecycle.addObserver(observer)

                    onDispose {
                        exoPlayer.release()
                        lifecycle.removeObserver(observer)
                    }
                }
            )
        }
    }
    @Composable
    fun clickableButton1() {
        var state by remember { mutableIntStateOf(1) }
        Button(colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.LightGray,
            contentColor = Color.Blue
        ), modifier = Modifier.background(Color.Green), onClick = {
            state++
        }
        ) {
            val display = """

                count as click  $state
                and

            """.trimIndent()
            Text(text = display, Modifier.padding(16.dp), color = Color.Blue)
            Divider(modifier = Modifier
                .size(20.dp)
                .align(Alignment.Vertical { size, _ ->
                    size
                }), color = Color.Yellow)
        }
    }
}
