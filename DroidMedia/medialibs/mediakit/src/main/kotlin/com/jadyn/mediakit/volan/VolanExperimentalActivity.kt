package com.jadyn.mediakit.volan

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import androidx.lifecycle.lifecycleScope
import com.jadyn.mediakit.R
import com.jadyn.mediakit.databinding.ActivityVolanExterimentalBinding
import kotlinx.coroutines.*

class VolanExperimentalActivity:AppCompatActivity() {

    private lateinit var binding:ActivityVolanExterimentalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolanExterimentalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.image.setImageResource(R.drawable.image_3008)
        binding.button.setOnClickListener {
            lifecycleScope.launch {
                val bmp = binding.image.drawToBitmap()
                val rotated = Volan.rotateBitmapCcw90(bmp)
                delay(1000)
                binding.image.setImageBitmap(rotated)
            }

        }
    }
}
