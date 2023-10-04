package com.me.harris.avif

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.me.harris.avif.databinding.ActivityAvifEntryBinding
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

class AvIfEntryActivity:AppCompatActivity() {

    private lateinit var binding :ActivityAvifEntryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAvifEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadAVIFImage()
    }

    private fun loadAVIFImage(){
        try {
            val avifImage = findViewById<ImageView>(R.id.avif_img)
            val bytes: ByteArray = inputStreamToBytes(assets.open("image_2010.avif"))!!
//            val bytes: ByteArray = inputStreamToBytes(assets.open("image_017.avif"))!! // 1.3MB -> 407kB
//            val bytes: ByteArray = inputStreamToBytes(assets.open("test.avif"))!!
            Glide.with(this)
                .load(bytes)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(avifImage)
            val avifsImage = findViewById<ImageView>(R.id.avifs_img)
            val bytes2: ByteArray = inputStreamToBytes(assets.open("test.avifs"))!!
            Glide.with(this)
                .load(bytes2).
                 override(SIZE_ORIGINAL)
                .into(avifsImage)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun inputStreamToBytes(`is`: InputStream): ByteArray? {
        val buffer = ByteArrayOutputStream(64 * 1024)
        try {
            var nRead: Int
            val data = ByteArray(16 * 1024)
            while (`is`.read(data).also { nRead = it } != -1) {
                buffer.write(data, 0, nRead)
            }
            buffer.close()
        } catch (e: IOException) {
            return null
        }
        return buffer.toByteArray()
    }
}
