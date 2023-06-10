package com.me.harris.filterlibrary.imagefilter

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cgfay.filterlibrary.ndkfilter.ImageFilter
import com.me.harris.awesomelib.viewBinding
import com.me.harris.filterlibrary.R
import com.me.harris.filterlibrary.databinding.ActivityImageFilterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

class ImageFilterActivity :AppCompatActivity(R.layout.activity_image_filter){

    private val binding by viewBinding(ActivityImageFilterBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.button.setOnClickListener {
            lifecycleScope.launch {
                val bmp = withContext(Dispatchers.Main.immediate){
                    val bmp = BitmapFactory.decodeResource(resources,R.drawable.image_017)
                    measureTimeMillis {
                        ImageFilter.getInstance().filterStackBlur(bmp,100)// slow!!!!!
                    }.let { time ->
                        Log.w("=A=","time cost is $time")
                    }
                    bmp
                }
                binding.image2.setImageBitmap(bmp)

            }
//            ImageFilter.getInstance().filterBlackWhite(bmp)
        }

    }
}