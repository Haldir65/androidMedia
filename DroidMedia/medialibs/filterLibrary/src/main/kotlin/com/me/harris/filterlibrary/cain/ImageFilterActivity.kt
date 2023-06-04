package com.me.harris.filterlibrary.cain

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.awesomelib.viewBinding
import com.me.harris.filterlibrary.R
import com.me.harris.filterlibrary.databinding.ActivityImageFilterBinding

class ImageFilterActivity :AppCompatActivity(R.layout.activity_image_filter){

    private val binding by viewBinding(ActivityImageFilterBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}