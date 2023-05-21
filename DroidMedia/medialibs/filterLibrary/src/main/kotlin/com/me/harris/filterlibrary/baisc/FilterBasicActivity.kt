package com.me.harris.filterlibrary.baisc

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.awesomelib.viewBinding
import com.me.harris.awesomelib.whenProgressChanged
import com.me.harris.filterlibrary.R
import com.me.harris.filterlibrary.databinding.ActivityFilterBasicBinding

class FilterBasicActivity:AppCompatActivity(R.layout.activity_filter_basic) {

    private val binding by viewBinding(ActivityFilterBasicBinding::bind)
    private lateinit var mediaPath:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaPath = VideoUtil.strVideo
        binding.playerViewMp.setDataSource(mediaPath)
        binding.playerViewMp.start()

        binding.seekbar.whenProgressChanged(::seekWhenStopTracking)
        binding.btnPause.setOnClickListener { binding.playerViewMp.pausePlay() }
        binding.btnResume.setOnClickListener { binding.playerViewMp.resumePlay() }

    }

    override fun onResume() {
        super.onResume()
        binding.playerViewMp.resumePlay()
    }

    override fun onStop() {
        super.onStop()
        binding.playerViewMp.pausePlay()
    }

    private fun seekWhenStopTracking(seekBar: SeekBar){
        val player = binding.playerViewMp.mMediaPlayer
        player?.run {
            val duration = this.duration
            val targetDuration = duration * (seekBar.progress*1.0f/seekBar.max)
            seekTo(targetDuration.toInt())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.playerViewMp.release()
    }

}