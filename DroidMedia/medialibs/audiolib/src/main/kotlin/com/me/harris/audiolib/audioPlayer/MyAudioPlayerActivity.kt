package com.me.harris.audiolib.audioPlayer

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.audiolib.R
import com.me.harris.awesomelib.utils.VideoUtil

class MyAudioPlayerActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_audio_player)
        findViewById<Button>(R.id.buttonStart)?.setOnClickListener {
                startPlay()
        }

        findViewById<Button>(R.id.buttonStop)?.setOnClickListener {
            audioPLayer?.stop()
        }
    }

    var audioPLayer: MyAudioPlayer? = null

    private fun startPlay(){
        audioPLayer = MyAudioPlayer().also {
            it.play(VideoUtil.strVideo,60*1000*1000,75*1000*1000)
        }
    }
}
