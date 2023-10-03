package com.me.harris.audiolib.audioPlayer

import android.media.AudioFormat
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.me.harris.audiolib.R
import com.me.harris.audiolib.databinding.ActivityMyAudioPlayerBinding
import java.io.File

class MyAudioPlayerActivity:AppCompatActivity() {

    private lateinit var binding: ActivityMyAudioPlayerBinding

    private val url = Environment.getExternalStorageDirectory().path +
            File.separator + Environment.DIRECTORY_DOWNLOADS + File.separator+"output.pcm"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        findViewById<Button>(R.id.buttonStart)?.setOnClickListener {
                startPlay()
        }

        findViewById<Button>(R.id.buttonStop)?.setOnClickListener {
            audioPLayer?.stop()
        }
        //   constructor( c: Context?,
        //                 path: String,
        //                 sampleRate: Int,
        //                 format: Int,
        //                 ch_layout: Int,
        //                 isBig: Boolean):this() {
        binding.audioTrackPlayer.setOnClickListener {
          audioTrackPlayPCM()
        }
        binding.openSlesPlayer.setOnClickListener {
            opensslelPlayAudio()
        }
    }

    var audioPLayer: MyAudioPlayer? = null

    private fun startPlay(){
        audioPLayer = MyAudioPlayer().also {
            val url = Environment.getExternalStorageDirectory().path +
                    File.separator + Environment.DIRECTORY_DOWNLOADS + File.separator+"input.m4a"
            require(File(url).exists())
//            it.play(VideoUtil.strVideo,60*1000*1000,75*1000*1000)
            it.play(url,0,750*1000*1000)
        }
    }

    private fun audioTrackPlayPCM(){
        require(File(url).exists())
        val player = ADAudioTrackPlayer(c = MyAudioPlayerActivity@this,
            path = url, sampleRate = 44100, format =  AudioFormat.ENCODING_PCM_16BIT,
            ch_layout = AudioFormat.CHANNEL_IN_STEREO, isBig = false
        )
        player.play()
        lifecycle.addObserver(object :DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                player.stop()
            }
        })
    }

    private fun opensslelPlayAudio(){
        require(File(url).exists())
        val player = ADOpenSLES(path = url, sample_rate =  44100,
            ch_layout = 1, format = 1)
        player.play()
        lifecycle.addObserver(object :DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                player.stop()
            }
        })
    }
}
