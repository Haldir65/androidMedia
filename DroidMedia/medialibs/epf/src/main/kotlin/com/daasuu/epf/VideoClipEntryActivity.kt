package com.daasuu.epf

import android.content.Intent
import android.os.Bundle
import androidx.activity.contextaware.withContextAvailable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.daasuu.epf.databinding.ActivityVideoClipEntryBinding
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.awesomelib.viewBinding
import kotlinx.coroutines.launch

class VideoClipEntryActivity:AppCompatActivity(R.layout.activity_video_clip_entry) {

    private val binding by viewBinding(ActivityVideoClipEntryBinding::bind)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        VideoUtil.setUrl()
        binding.card1.setOnClickListener { startActivity(Intent(this,VideoClipActivity::class.java)) }
        binding.card2.setOnClickListener { startActivity(Intent(this,VideoEditActivity::class.java)) }
//        binding.card3.setOnClickListener { startActivity(Intent(this,VideoClipActivity::class.java)) }

    }
}