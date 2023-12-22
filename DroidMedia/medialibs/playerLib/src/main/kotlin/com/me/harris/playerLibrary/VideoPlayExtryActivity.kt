package com.me.harris.playerLibrary

import android.content.Intent
import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.media.MediaFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.contextaware.withContextAvailable
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.me.harris.IJKPlayerSourcingActivity
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.playerLibrary.compose.ComposeVideoPlayerActivity
import com.me.harris.playerLibrary.exoplayer.ExoplayerSampleActivity
import com.me.harris.playerLibrary.ffmediaplayer.FFMediaPlayerActivity
import com.me.harris.playerLibrary.openglplayvideo.MediaPlayerSurfaceStubActivity
import com.me.harris.playerLibrary.process.ui.SendSurfaceToAnotherProcessSenderActivity
import com.me.harris.playerLibrary.textureview.TextureViewMediaCodecVideoPlayerActivity
import com.me.harris.playerLibrary.video.MediaCodecMain2Activity
import com.me.harris.playerLibrary.video.sharedSurface.SharedSurfaceTextureListActivity
import com.me.harris.playerLibrary.viewmodels.VideoPlayEntryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VideoPlayExtryActivity:AppCompatActivity() {

    private val viewModel by viewModels<VideoPlayEntryViewModel>()
    init {
        lifecycleScope.launch {
            withContextAvailable {
                launch(Dispatchers.IO) {
                    VideoUtil.setUrl()
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    probeMediaCodecInfoDetails()
                }
            }
        }
    }

    // https://developer.android.com/guide/topics/media/media-codecs
    @RequiresApi(Build.VERSION_CODES.Q)
    fun probeMediaCodecInfoDetails(){
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.Q){
            val format = MediaFormat.MIMETYPE_VIDEO_VP9
            val codecList = MediaCodecList(MediaCodecList.ALL_CODECS)//74种
            val infos = arrayListOf<MediaCodecInfo>()
            codecList.codecInfos.filter { c -> c.supportedTypes.any { a -> a.contains(format) } }.distinct().map { codec ->
                kotlin.runCatching {
                    val cap =  codec.getCapabilitiesForType(format)
                    cap?.videoCapabilities?.supportedPerformancePoints
                        ?.onEach { p ->
                            Log.w("=A=","${codec.name} covers ${p.covers(MediaCodecInfo.VideoCapabilities.PerformancePoint.UHD_60)}")
                        }
                }.onFailure {
                    it.printStackTrace()
                }

                """
                    =============================
                    code.name = ${codec.name}
                    codec.isSoftwareOnly = ${codec.isSoftwareOnly}
                    codec.isHardwareAccelerated = ${codec.isHardwareAccelerated}
                    codec.isVendor = ${codec.isVendor}
                    codec.isAlias = ${codec.isAlias}
                    codec.canonicalName = ${codec.canonicalName}
                    ===============================
                """.trimIndent()
            }.onEach {
                Log.i("=A=",it)
            }


        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_entry)
        findViewById<Button>(R.id.btn1)?.setOnClickListener {
            startActivity(Intent(this, MediaCodecMain2Activity::class.java))
        }
        findViewById<Button>(R.id.btn2)?.setOnClickListener {
            startActivity(Intent(this, TextureViewMediaCodecVideoPlayerActivity::class.java))
        }
        findViewById<Button>(R.id.btn3)?.setOnClickListener {
            startActivity(Intent(this, MediaPlayerSurfaceStubActivity::class.java))
        }
        findViewById<Button>(R.id.btn4)?.setOnClickListener {
            startActivity(Intent(this, SharedSurfaceTextureListActivity::class.java))
        }
        findViewById<Button>(R.id.btn5)?.setOnClickListener {
            startActivity(Intent(this, ExoplayerSampleActivity::class.java))
        }
        findViewById<Button>(R.id.btn6)?.setOnClickListener {
            startActivity(Intent(this, FFMediaPlayerActivity::class.java))
        }
        findViewById<Button>(R.id.btn7)?.setOnClickListener {
            startActivity(Intent(this, IJKPlayerSourcingActivity::class.java))
        }
        findViewById<Button>(R.id.btn8)?.setOnClickListener {
            startActivity(Intent(this, SendSurfaceToAnotherProcessSenderActivity::class.java))
        }
        findViewById<Button>(R.id.btn11)?.setOnClickListener {
            startActivity(Intent(this, ComposeVideoPlayerActivity::class.java))
        }
        viewModel.doStuff()
    }
}
