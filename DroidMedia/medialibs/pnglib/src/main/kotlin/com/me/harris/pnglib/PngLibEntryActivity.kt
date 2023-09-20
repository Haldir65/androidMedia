package com.me.harris.pnglib

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.transform.RoundedCornersTransformation
import com.me.harris.pnglib.databinding.ActivityPngEntryBinding
import java.io.File

class PngLibEntryActivity:AppCompatActivity() {

    lateinit var binding:ActivityPngEntryBinding
    private val viewModel by viewModels<PngLibEntryViewModel>()
    // https://github.com/glumes/InstantGLSL/blob/b73acefdcb60668da0d6e0bc9ebaaca396a82e59/instantglsl/src/main/cpp/pnghelper/PngHelper.cpp#L232


    private val saveDir by lazy {
        "${application.filesDir}${File.separator}photos_compress_to_png"
    }

    private val NON_SCALEABLE_BMP_NAME = "image_2010.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPngEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btn1.setOnClickListener {
            makeSureSaveDirExists()
            testPngFileIsPngFile()
        }

    }

    private fun makeSureSaveDirExists() {
        val fileStorageDir = File(saveDir)
        fileStorageDir.deleteRecursively()
        fileStorageDir.mkdir()
    }

    private fun testPngFileIsPngFile(){
        val despath = "${saveDir}/opt_${System.currentTimeMillis()}.png"
        val bmp = assets.open(NON_SCALEABLE_BMP_NAME).use(BitmapFactory::decodeStream)
        val fos = File(despath).outputStream()
        bmp.compress(Bitmap.CompressFormat.PNG,100,fos)

        val despath2 = "${saveDir}/opt_${System.currentTimeMillis()}.jpg"
        val fos2 = File(despath2).outputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG,100,fos2)

        require(File(despath2).exists())
        require(File(despath).exists())
        viewModel.checkPngFileIsPngFile(despath)
        viewModel.checkPngFileIsPngFile(despath2)
        binding.comoressedImage.load(despath) {
            transformations(RoundedCornersTransformation(25f))
        }
    }



}
