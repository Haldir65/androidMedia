package com.me.harris.filterlibrary

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cgfay.filterlibrary.ui.NativeRenderMain2Activity
import com.cgfay.filterlibrary.ui.NativeRenderMain3Activity
import com.cgfay.filterlibrary.ui.NativeRenderMainActivity
import com.me.harris.awesomelib.viewBinding
import com.me.harris.filterlibrary.baisc.FilterBasicActivity
import com.me.harris.filterlibrary.imagefilter.ImageFilterActivity
import com.me.harris.filterlibrary.databinding.ActivityFilterEntryBinding

class FilterEntryActivity:AppCompatActivity(R.layout.activity_filter_entry) {

    private val binding by viewBinding(ActivityFilterEntryBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.card1.setOnClickListener {
            startActivity(Intent(this,FilterBasicActivity::class.java))
        }
        binding.card2.setOnClickListener {
            startActivity(Intent(this, ImageFilterActivity::class.java))
        }
        binding.card3.setOnClickListener {
            startActivity(Intent(this, NativeRenderMainActivity::class.java))
        }
        binding.card4.setOnClickListener {
            startActivity(Intent(this, NativeRenderMain2Activity::class.java))
        }
        binding.card5.setOnClickListener {
            startActivity(Intent(this, NativeRenderMain3Activity::class.java))
        }

    }


}