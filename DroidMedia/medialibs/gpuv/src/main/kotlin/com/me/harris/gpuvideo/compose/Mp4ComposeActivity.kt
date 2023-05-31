package com.me.harris.gpuvideo.compose

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.daasuu.gpuv.R
import com.daasuu.gpuv.composer.GPUMp4Composer
import com.daasuu.gpuv.egl.filter.GlFilter
import com.daasuu.gpuv.egl.filter.GlFilterGroup
import com.daasuu.gpuv.egl.filter.GlMonochromeFilter
import com.daasuu.gpuv.egl.filter.GlVignetteFilter
import com.daasuu.gpuv.gpuvideoandroid.FilterType
import com.me.harris.gpuv.compose.VideoItem
import com.me.harris.gpuv.compose.VideoLoadListener
import com.me.harris.gpuv.compose.VideoLoader
import java.lang.Exception

class Mp4ComposeActivity:AppCompatActivity(R.layout.activity_mp4compose) {

    companion object {
        const val TAG = "mp4Compose"
        const val PERMISSION_REQUEST_CODE = 8888
    }

    private var videoLoader:VideoLoader? = null
    private var videoItem:VideoItem? = null
    private var GPUMp4Composer:GPUMp4Composer? = null
    private lateinit var muteCheckBox: CheckBox
    private lateinit var flipVerticalCheckBox:CheckBox
    private lateinit var flipHorizontalCheckBox:CheckBox
    private var videoPath:String = ""

    private var filterDialog:AlertDialog? = null
    private var glFilter:GlFilter = GlFilterGroup(GlMonochromeFilter(),GlVignetteFilter())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        muteCheckBox = findViewById(R.id.mute_check_box)
        flipVerticalCheckBox = findViewById(R.id.flip_vertical_check_box)
        flipHorizontalCheckBox = findViewById(R.id.flip_horizontal_check_box)
        findViewById<Button>(R.id.start_codec_button).setOnClickListener { v ->
             v.isEnabled =   true

        }
        findViewById<Button>(R.id.cancel_button).setOnClickListener {
            GPUMp4Composer?.cancel()
        }

        findViewById<Button>(R.id.start_play_movie).setOnClickListener {
            val uri = Uri.parse(videoPath)
            val intent = Intent(Intent.ACTION_VIEW,uri)
            intent.setDataAndType(uri,"video/mp4")
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_filter).setOnClickListener { it ->
            if (filterDialog==null){
                val builder = AlertDialog.Builder(it.context)
                builder.setTitle("Choose a filter")
                builder.setOnDismissListener {
                    filterDialog = null
                }
                val filters = FilterType.values()
                val charList = filters.map {a -> a.name }.toTypedArray()
                builder.setItems(charList) { d ,i ->
                    changeFilter(filters[i])
                }
                filterDialog = builder.show()
            }else {
                filterDialog?.dismiss()
            }
        }
    }

    private fun changeFilter(filter:FilterType){
        glFilter = FilterType.createGlFilter(filter,this)
        findViewById<Button>(R.id.btn_filter).text = "Filter ${filter.name}"
    }

    override fun onResume() {
        super.onResume()
        if (checkPermission()){
            videoLoader = VideoLoader(applicationContext).apply {
                loadDeviceVideos(object :VideoLoadListener{
                    override fun onVideoLoaded(videoItems: MutableList<VideoItem>?) {
                        val lv = findViewById<ListView>()
                    }

                    override fun onFailed(e: Exception?) {
                        TODO("Not yet implemented")
                    }
                })

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE){
            if (grantResults.getOrNull(0) == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "permission has been granted ", Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(this, "permission is not granted ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermission():Boolean{
        return if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            false
        } else true
    }
}