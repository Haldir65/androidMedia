package com.me.harris.extractframe

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.awesomelib.viewBinding
import com.me.harris.extractframe.databinding.ActivityExtractFrameEntryBinding
import com.me.harris.extractframe.finale.FrameExtractFinalActivity
import com.me.harris.extractframe.glextractor.ExtractUingOpenglActivity

class ExtractFrameEntryActivity:AppCompatActivity(R.layout.activity_extract_frame_entry) {

    private val binding by viewBinding(ActivityExtractFrameEntryBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.card1.setOnClickListener {
            startActivity(Intent(this,ExtractFrameAndSaveKeyFrameToFileActivity::class.java))
        }
        binding.card2.setOnClickListener {
            startActivity(Intent(this, ExtractFrameUsingParallelExtractorActivity::class.java))
        }

        binding.card3.setOnClickListener {
            startActivity(Intent(this, FrameExtractFinalActivity::class.java))
        }

        binding.card4.setOnClickListener {
            startActivity(Intent(this, ExtractUingOpenglActivity::class.java))
        }

        binding.card5.setOnClickListener {
            startActivity(Intent(this, DecodeFrameBitMapToViewActivity::class.java))
        }


    }

}
