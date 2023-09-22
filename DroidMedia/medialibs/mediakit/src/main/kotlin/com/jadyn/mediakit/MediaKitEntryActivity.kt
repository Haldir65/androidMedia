package com.jadyn.mediakit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.jadyn.mediakit.databinding.ActivityMediakitEntryBinding
import com.jadyn.mediakit.framer.MediaKitDecodeFrameActivity
import com.jadyn.mediakit.native.MediaKitJNI
import com.jadyn.mediakit.nativecodec.MediaKitNativeCodecActivity
import com.me.harris.awesomelib.viewBinding
import java.io.File
import java.nio.charset.Charset

class MediaKitEntryActivity:AppCompatActivity(R.layout.activity_mediakit_entry) {


    private val binding by viewBinding(ActivityMediakitEntryBinding::bind)

    private val viewModel by viewModels<MediaKitEntryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.card1.setOnClickListener {
            startActivity(Intent(this, MediaKitDecodeFrameActivity::class.java))
        }

        binding.card2.setOnClickListener {
            startActivity(Intent(this, MediaKitNativeCodecActivity::class.java))
        }

        binding.card3.setOnClickListener {
            viewModel.readTextWithMMAP()
        }
    }
}
