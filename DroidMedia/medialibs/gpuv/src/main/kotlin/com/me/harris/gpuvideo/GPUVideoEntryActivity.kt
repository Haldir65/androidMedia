package com.me.harris.gpuvideo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.daasuu.gpuv.R
import com.daasuu.gpuv.databinding.ActivityGpuvideoEntryBinding
import com.me.harris.awesomelib.viewBinding
import com.me.harris.gpuvideo.camera.LandscapeCameraActivity
import com.me.harris.gpuvideo.camera.PortraitCameraActivity
import com.me.harris.gpuvideo.camera.SquareCameraActivity
import com.me.harris.gpuvideo.compose.Mp4ComposeActivity
import com.me.harris.gpuvideo.preview.GPUVideoPreviewVideoActivity

class GPUVideoEntryActivity:AppCompatActivity(R.layout.activity_gpuvideo_entry) {


    private val binding by viewBinding(ActivityGpuvideoEntryBinding::bind)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.button1.setOnClickListener { startActivity(Intent(this,
            GPUVideoPreviewVideoActivity::class.java)) }
        binding.button2.setOnClickListener { startActivity(Intent(this,
            Mp4ComposeActivity::class.java)) }
        binding.button3.setOnClickListener { startActivity(Intent(this,
            SquareCameraActivity::class.java)) }
        binding.button4.setOnClickListener { startActivity(Intent(this,
            LandscapeCameraActivity::class.java)) }
        binding.button5.setOnClickListener { startActivity(Intent(this,
            PortraitCameraActivity::class.java)) }

    }



}