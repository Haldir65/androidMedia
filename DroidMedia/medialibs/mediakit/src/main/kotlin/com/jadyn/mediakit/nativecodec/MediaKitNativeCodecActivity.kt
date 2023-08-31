package com.jadyn.mediakit.nativecodec

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.jadyn.mediakit.R
import com.jadyn.mediakit.databinding.ActivityMediakitNativeCodecBinding
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.awesomelib.viewBinding

class MediaKitNativeCodecActivity:AppCompatActivity(R.layout.activity_mediakit_native_codec) {

    private val binding by viewBinding(ActivityMediakitNativeCodecBinding::bind)

    private val viewModel by viewModels<MediaKitNativeCodecViewModel>()

    val filepath = VideoUtil.strVideo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.card1.setOnClickListener {
            viewModel.mediaCodecProbeInfo(filepath)
        }

        binding.card2.setOnClickListener {

        }
    }
}
