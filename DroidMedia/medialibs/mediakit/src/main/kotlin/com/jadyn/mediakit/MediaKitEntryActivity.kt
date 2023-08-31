package com.jadyn.mediakit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jadyn.mediakit.databinding.ActivityMediakitEntryBinding
import com.jadyn.mediakit.framer.MediaKitDecodeFrameActivity
import com.jadyn.mediakit.nativecodec.MediaKitNativeCodecActivity
import com.me.harris.awesomelib.viewBinding

class MediaKitEntryActivity:AppCompatActivity(R.layout.activity_mediakit_entry) {


    private val binding by viewBinding(ActivityMediakitEntryBinding::bind)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.card1.setOnClickListener {
            startActivity(Intent(this, MediaKitDecodeFrameActivity::class.java))
        }

        binding.card2.setOnClickListener {
            startActivity(Intent(this, MediaKitNativeCodecActivity::class.java))
        }

    }
}
