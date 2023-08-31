package com.me.harris.audiolib

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.audiolib.audioPlayer.MyAudioPlayerActivity
import com.me.harris.audiolib.audiorecord.MediaCodecForAACActivity
import com.me.harris.audiolib.databinding.ActivityAudioLibEntryBinding
import com.me.harris.awesomelib.viewBinding

class AudioLibEntryActivity:AppCompatActivity(R.layout.activity_audio_lib_entry) {

    private val binding by viewBinding(ActivityAudioLibEntryBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.card1.setOnClickListener {
            startActivity(Intent(this,MyAudioPlayerActivity::class.java))
        }

        binding.card2.setOnClickListener {
            startActivity(Intent(this,MediaCodecForAACActivity::class.java))
        }
    }

}
