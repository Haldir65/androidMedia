package com.me.harris.mediainfo

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import coil.decode.VideoFrameDecoder
import coil.load
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.mediainfo.databinding.ActivityMediaInfoEntryBinding
import net.mediaarea.mediainfo.Core

class MediaInfoProbeActivity:AppCompatActivity() {

    private lateinit var binding:ActivityMediaInfoEntryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaInfoEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val videoUrl = VideoUtil.setUrl().let { VideoUtil.strVideo }
        binding.image.load(videoUrl){
            decoderFactory { result, options, _ -> VideoFrameDecoder(result.source, options) }
//         videoFramePercent(0.0)
        }
        binding.text.movementMethod = ScrollingMovementMethod()
        binding.button.setOnClickListener {
           Core.creatReport(videoUrl).let {
               Log.w("=A=",it)
               binding.text.text = it
           }
        }
    }
}