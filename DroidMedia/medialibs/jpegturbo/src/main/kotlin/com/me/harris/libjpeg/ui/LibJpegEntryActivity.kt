package com.me.harris.libjpeg.ui

import android.graphics.Bitmap
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
import com.me.harris.libjpeg.JpegSpoon
import kotlinx.coroutines.*
import java.io.File

class LibJpegEntryActivity : AppCompatActivity(R.layout.activity_jpeg_entry) {

    private val viewModel by viewModels<LibJpegEntryViewModel>()
    private val binding by viewBinding(ActivityJpegEntryBinding::bind)

    private val saveDir by lazy {
        "${application.filesDir}${File.separator}photos_compress_to_jpeg"
    }

    // BitmapFactory.decodeResource会缩放图片
    private val NON_SCALEABLE_BMP_NAME = "image_2010.jpg"

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
            viewModel.callJpegCompressBitmap(
                bitmap = assets.open(NON_SCALEABLE_BMP_NAME).use(BitmapFactory::decodeStream),
                quality = 30, storage_dir = saveDir, outFilePath = despath, optimize = true, mode = JpegSpoon.COMPRESS_MODE_LIBJPEG_FILE
            )
        }
        binding.btn3.setOnClickListener {
            makeSureSaveDirExists()
            val despath = "${saveDir}/opt_${System.currentTimeMillis()}.jpeg"
            viewModel.callJpegCompressBitmap(
                bitmap = assets.open(NON_SCALEABLE_BMP_NAME).use(BitmapFactory::decodeStream),
                quality = 30, storage_dir = saveDir, outFilePath = despath, optimize = true,mode = JpegSpoon.COMPRESS_MODE_TURBO_JPEG
            )
        }
        binding.btn4.setOnClickListener {
            makeSureSaveDirExists()
            val despath = "${saveDir}/opt_${System.currentTimeMillis()}.jpeg"
            val manager = assets
            val fd = manager.openFd(NON_SCALEABLE_BMP_NAME)
            viewModel.callJpegCompressBitmap(
                bitmap = assets.open(NON_SCALEABLE_BMP_NAME).use(BitmapFactory::decodeStream),
                quality = 30, storage_dir = saveDir, outFilePath = despath, optimize = true,mode = JpegSpoon.COMPRESS_MODE_LIBJPEG_IN_MEMORY
            )
        }

        binding.btn5.setOnClickListener {
            makeSureSaveDirExists()
            val despath = "${saveDir}/opt_${System.currentTimeMillis()}.jpeg"
//            val bmp = BitmapFactory.decodeResource(resources,R.drawable.image_1009)
            val bmp = assets.open("image_1009.jpg").use(BitmapFactory::decodeStream)
            Log.w("=A=","decodeStream bmp width = ${bmp.width} height = ${bmp.height}")
            // https://stackoverflow.com/a/23047893
            val fos = File(despath).outputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG,100,fos)
            require(File(despath).exists())
            viewModel.decomressJpegToBitmap(jpegFilePath = despath)
        }

        binding.btn6.setOnClickListener {
            makeSureSaveDirExists()
            val despath = "${saveDir}/opt_${System.currentTimeMillis()}.jpeg"
            val bmp = assets.open("image_1009.jpg").use(BitmapFactory::decodeStream)
            val fos = File(despath).outputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG,100,fos)
            require(File(despath).exists())
            viewModel.decomressJpegToBitmapTurbo(jpegFilePath = despath)
        }

        observeCompress()
    }

    private fun makeSureSaveDirExists() {
        val fileStorageDir = File(saveDir)
        fileStorageDir.deleteRecursively()
        fileStorageDir.mkdir()
    }

    private fun observeCompress() {
        lifecycleScope.launch {
            viewModel.compressEvents.collect() { ev ->
                when (ev) {
                    is CompressEvents.CompressCompleted -> {
                        binding.comoressedImage2.load(ev.path) {
                            transformations(RoundedCornersTransformation(25f))
                        }
                    }
                    is CompressEvents.TurboCompressCompleted -> {
                        binding.comoressedImage3.load(ev.path) {
                            transformations(RoundedCornersTransformation(25f))
                        }
                    }
                    is CompressEvents.LibJpegCompressInMemoryCompleted -> {
                        binding.comoressedImage4.load(ev.path) {
                            transformations(RoundedCornersTransformation(25f))
                        }
                    }
                    else -> {
                        Log.w("=A=", "")
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.decompressEvents.collect(){ ev ->
                when(ev) {
                    is DecompressEvents.JpegDecompressFinished -> {
                        binding.comoressedImage5.load(ev.path) {
                            transformations(RoundedCornersTransformation(25f))
                        }
                    }

                    is DecompressEvents.JpegDecompressFinishedTurbo -> {
                        binding.comoressedImage6.load(ev.path) {
                            transformations(RoundedCornersTransformation(25f))
                        }
                    }
                }

            }
        }
    }
}
