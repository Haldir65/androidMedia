package com.me.harris.libjpeg.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.RoundedCornersTransformation
import com.me.harris.awesomelib.viewBinding
import com.me.harris.jpegturbo.R
import com.me.harris.jpegturbo.databinding.ActivityJpegEntryBinding
import com.me.harris.jpegturbo.databinding.ActivityJpegEntryBinding.bind
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File

class LibJpegEntryActivity:AppCompatActivity(R.layout.activity_jpeg_entry) {


    private val viewModel by viewModels<LibJpegEntryViewModel>()
    private val binding by viewBinding(ActivityJpegEntryBinding::bind)

    private val saveDir by lazy {
        "${application.filesDir}${File.separator}photos_compress_to_jpeg"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.originalImage.load(R.drawable.image_2010) {
            transformations(RoundedCornersTransformation(25f))
        }
        binding.btn1.setOnClickListener {
            viewModel.callJpegEntryMethod()
        }
        binding.btn2.setOnClickListener {
            makeSureSaveDirExists()
            val despath = "${saveDir}/opt_${System.currentTimeMillis()}.jpeg"
            viewModel.callJpegCompressBitmap(bitmap = BitmapFactory.decodeResource(resources,R.drawable.image_2010)!!,
               quality = 30, outFilePath = despath,optimize = true )
        }
        observeCompress()
    }

    private fun makeSureSaveDirExists(){
        val fileStorageDir = File(saveDir)
        fileStorageDir.deleteRecursively()
        fileStorageDir.mkdir()
    }

    private fun observeCompress(){
        lifecycleScope.launch {
            viewModel.events.collect() { ev ->
                when(ev){
                    is CompressEvents.CompressCompleted ->{
                        binding.comoressedImage.load(ev.path){
                            transformations(RoundedCornersTransformation(25f))
                        }
                    }
                    else ->{

                        Log.w("=A=","")
                    }
                }

            }
        }
    }

}
