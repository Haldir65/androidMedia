package com.me.harris.audiolib.audioPlayer

import android.media.AudioFormat
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.me.harris.audiolib.R
import com.me.harris.audiolib.databinding.ActivityMyAudioPlayerBinding
import com.me.harris.audiolib.oboe.OboeAudioPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

// tldr
// 1. openssl可以播放mp3 , aaudio不可以，也就说aaudio不带解码功能
// 2. aaudio 是api 26引入的， 比opensles简单一些
class MyAudioPlayerEntryActivity:AppCompatActivity() {

    private lateinit var binding: ActivityMyAudioPlayerBinding

    private val PCM_FILE_PATH = Environment.getExternalStorageDirectory().path +
            File.separator + Environment.DIRECTORY_MUSIC + File.separator+"output.pcm"

    private val M4A_FILE_PATH = Environment.getExternalStorageDirectory().path +
            File.separator + Environment.DIRECTORY_MUSIC + File.separator+"input.m4a"

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
        binding.aaudioPlayer.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                // AAUDIO API 26
                // https://zhuanlan.zhihu.com/p/603942737
                aaudioPlayAudio()
            }
        }

        binding.oboePlayer.setOnClickListener {
            oboePlayAudio()
        }
    }



    var audioPLayer: MyAudioPlayer? = null

    private fun startPlay(){
        audioPLayer = MyAudioPlayer().also {
            val url = M4A_FILE_PATH
            require(File(url).exists())
//            it.play(VideoUtil.strVideo,60*1000*1000,75*1000*1000)
            it.play(url,0,750*1000*1000)
            lifecycle.addObserver(object :DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    it.stop()
                }
            })
        }
    }

    private fun audioTrackPlayPCM(){
        require(File(PCM_FILE_PATH).exists())
        val player = ADAudioTrackPlayer(c = this,
            path = PCM_FILE_PATH, sampleRate = 44100, format =  AudioFormat.ENCODING_PCM_16BIT,
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
        require(File(PCM_FILE_PATH).exists())
        val player = ADOpenSLES(path = PCM_FILE_PATH, sample_rate =  44100,
            ch_layout = 1, format = 1)
        player.play()
        lifecycle.addObserver(object :DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                player.stop()
            }
        })
    }

    private fun aaudioPlayAudio(){
        require(File(PCM_FILE_PATH).exists())
        val player = AAudioPlayer(filepath = PCM_FILE_PATH, context = this)
        player.play()
        lifecycle.addObserver(object :DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                player.stop()
            }
        })
    }


    private fun oboePlayAudio(){
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                require(File(M4A_FILE_PATH).exists())
                val player =  OboeAudioPlayer()
                player.startPlaying(fileName = M4A_FILE_PATH, sampleRate = 44100)
                withContext(Dispatchers.Main.immediate){
                    lifecycle.addObserver(object :DefaultLifecycleObserver {
                        override fun onDestroy(owner: LifecycleOwner) {
                            super.onDestroy(owner)
                            player.stopPlaying()
                        }
                    })
                }
            }
        }


    }
}
