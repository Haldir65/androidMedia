package com.me.harris.pnglib

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.decode.ImageSource
import coil.load
import coil.transform.RoundedCornersTransformation
import com.me.harris.awesomelib.BuildConfig
import com.me.harris.pnglib.databinding.ActivityPngEntryBinding
import kotlinx.coroutines.*
import java.io.File
import java.nio.ByteBuffer

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

        binding.originalImage.load("file:///android_asset/${NON_SCALEABLE_BMP_NAME}") {
            transformations(RoundedCornersTransformation(25f))
        }
        binding.btn1.setOnClickListener {
            makeSureSaveDirExists()
            testPngFileIsPngFile()
//            binding.comoressedImage.setImageBitmap(assets.open(NON_SCALEABLE_BMP_NAME).use(BitmapFactory::decodeStream))
        }

        binding.btn2.setOnClickListener {
            makeSureSaveDirExists()
            retrievePngFileWidthAndHeight()
            binding.comoressedImage2.setImageBitmap(assets.open(NON_SCALEABLE_BMP_NAME).use(BitmapFactory::decodeStream))
        }

        binding.btn3.setOnClickListener {
            makeSureSaveDirExists()
            decodePngToByteBuffer()
        }

        binding.btn4.setOnClickListener {
            makeSureSaveDirExists()
            saveBitmapToPngFileUsingLibPng()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            binding.btn5.setOnClickListener {
                var start = System.currentTimeMillis()
                val source = ImageDecoder.createSource(assets,NON_SCALEABLE_BMP_NAME)
                val bmp = ImageDecoder.decodeBitmap(source)
                binding.comoressedImage5.setImageBitmap(bmp)
                Log.w("=A=","using image decoder cost me ${System.currentTimeMillis() - start} milliseconds")
                start = System.currentTimeMillis()
                val bmp2 = assets.open(NON_SCALEABLE_BMP_NAME).use(BitmapFactory::decodeStream)
                binding.comoressedImage5.setImageBitmap(bmp2)
                Log.w("=A=","using BitmapFactory decoder cost me ${System.currentTimeMillis() - start} milliseconds")
                // using image decoder cost me 79 milliseconds
                // using BitmapFactory decoder cost me 25 milliseconds
            }
        }

    }

    private fun makeSureSaveDirExists() {
        val fileStorageDir = File(saveDir)
        fileStorageDir.deleteRecursively()
        fileStorageDir.mkdir()
    }


    private fun decodePngToByteBuffer(){
        val pngPath = generatePngFileOnTheFly()
        val width = viewModel.getPngWidth(pngPath)
        val height = viewModel.getPngHeight(pngPath)
        val hasAlpha = viewModel.spoon.pngHasAlpha(pngPath)
        val size = width * height * 3 // rgb
        val buffer = ByteBuffer.allocateDirect(size)
        var start = System.currentTimeMillis()
        viewModel.decodePngToByteBuffer(pngPath,buffer)
        Log.w("=A=","decodePngToByteBuffer cost me with ${width*height*3} bytes [or ${width*height*3/1000_000}MB] ${System.currentTimeMillis()-start} milliseconds")
        buffer.limit(size) // 读的尽头，读的开始是0
        // PNG_FORMAT_FLAG_ALPHA  在c层
        val rgbaBuffer = ByteBuffer.allocateDirect(width * height * 4)
        // rgb buffer to rgba buffer

        start = System.currentTimeMillis()
        repeat(width*height) {num ->
            repeat(3){ i ->
                rgbaBuffer.put(buffer.get(3*num+i))
            }
//            rgbaBuffer.put(0XFF.toByte())
            rgbaBuffer.put(255.toByte())
        }
        val end = System.currentTimeMillis()
        Log.w("=A=","adding alpha bytes to rgb buffer with size of ${width*height*3} bytes [or ${width*height*3/1000_000}MB ] cost me ${end-start} milliseconds")
        rgbaBuffer.flip() // 读完了，读的开始设置为0，读的结束设置为当前写的结束
        val bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(rgbaBuffer)
        binding.comoressedImage3.setImageBitmap(bitmap)
    }

    /**
     * bitmap pixel -> directBytebuffer -> jni -> pass buffer address -> copy from buffer -> rgba data to lib-png abgr?
     */
    private fun saveBitmapToPngFileUsingLibPng(){
        val pngPath = generatePngFileOnTheFly()
        val width = viewModel.getPngWidth(pngPath)
        val height = viewModel.getPngHeight(pngPath)
        val size = width * height * 4
        val bmp = File(pngPath).inputStream().use(BitmapFactory::decodeStream)
        val buffer = ByteBuffer.allocateDirect(size)
        bmp.copyPixelsToBuffer(buffer)
        val despath = "${saveDir}/opt_${System.currentTimeMillis()}.png"
        viewModel.saveBitmapToPngFile(destfile = despath,buffer = buffer,width = bmp.width,height = bmp.height)
        binding.comoressedImage4.load(despath) {
//            transformations(RoundedCornersTransformation(25f))
        }
    }

    private fun retrievePngFileWidthAndHeight(){
        val pngPath = generatePngFileOnTheFly()
        viewModel.getPngHeight(pngPath)
        viewModel.getPngWidth(pngPath)
    }

    private fun generatePngFileOnTheFly():String{
        val despath = "${saveDir}/opt_${System.currentTimeMillis()}.png"
        val bmp = assets.open(NON_SCALEABLE_BMP_NAME).use(BitmapFactory::decodeStream)
        val fos = File(despath).outputStream()
        bmp.compress(Bitmap.CompressFormat.PNG,100,fos)
        require(File(despath).exists())
        return despath
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
