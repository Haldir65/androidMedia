package com.me.harris.gpuvideo.compose

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.daasuu.gpuv.R
import com.daasuu.gpuv.composer.FillMode
import com.daasuu.gpuv.composer.GPUMp4Composer
import com.daasuu.gpuv.egl.filter.GlFilter
import com.daasuu.gpuv.egl.filter.GlFilterGroup
import com.daasuu.gpuv.egl.filter.GlMonochromeFilter
import com.daasuu.gpuv.egl.filter.GlVignetteFilter
import com.daasuu.gpuv.gpuvideoandroid.FilterType
import com.me.harris.gpuv.compose.VideoItem
import com.me.harris.gpuv.compose.VideoListAdapter
import com.me.harris.gpuv.compose.VideoLoadListener
import com.me.harris.gpuv.compose.VideoLoader
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

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
            startCodec()
        }
        findViewById<Button>(R.id.cancel_button).setOnClickListener {
            GPUMp4Composer?.cancel()
            File(videoPath).takeIf { it.exists() }?.delete()
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
                        val lv = findViewById<ListView>(R.id.video_list)
                        val adapter = VideoListAdapter(applicationContext,R.layout.row_video_list,videoItems)
                        lv.adapter = adapter
                        lv.setOnItemClickListener { parent,view,position,id ->
                            this@Mp4ComposeActivity.videoItem = videoItems?.get(position)
                            findViewById<Button>(R.id.start_codec_button).isEnabled = true
                        }
                    }

                    override fun onFailed(e: Exception?) {
                        e?.printStackTrace()
                    }
                })
            }
        }
    }



    private fun getVideoFilePath():String {
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath +  File.separator+ "Mp4Composer"
        if (!File(dir).exists()){
            File(dir).mkdirs()
        }
        return dir+File.separator + SimpleDateFormat("yyyyMM_dd-HHmmss").format(
            Date()
        )+"filter_apply.mp4"
    }

    private fun startCodec(){
        if (videoItem?.path.isNullOrEmpty()) {
            Toast.makeText(this, "选中视频为空", Toast.LENGTH_SHORT).show()
            return
        }
        videoPath = getVideoFilePath()
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.max = 100
        findViewById<Button>(R.id.start_play_movie).isEnabled = false
        GPUMp4Composer = GPUMp4Composer(requireNotNull(videoItem!!.path),videoPath)
            .fillMode(FillMode.PRESERVE_ASPECT_CROP)
            .filter(glFilter)
            .mute(muteCheckBox.isChecked)
            .flipHorizontal(flipHorizontalCheckBox.isChecked)
            .flipVertical(flipVerticalCheckBox.isChecked)
            .listener(object :GPUMp4Composer.Listener{
                override fun onProgress(progress: Double) {
                    Log.d(TAG, "onProgress = $progress")
                    runOnUiThread { progressBar.progress = (progress*100).toInt() }
                }

                override fun onCompleted() {
                    Log.d(TAG, "onCompleted  $videoPath")
                    if (File(videoPath).exists()){
                        exportMp4ToGallery(applicationContext,videoPath)
                        runOnUiThread {
                            Toast.makeText(this@Mp4ComposeActivity, "导出成功", Toast.LENGTH_SHORT).show()
                            progressBar.progress = 100
                            findViewById<Button>(R.id.start_codec_button).isEnabled = true
                            findViewById<Button>(R.id.start_play_movie).isEnabled = true
                        }
                    }
                }

                override fun onCanceled() {

                }

                override fun onFailed(exception: java.lang.Exception?) {
                }

            }).start()
    }

    private fun exportMp4ToGallery(context:Context,filePath:String){
        val values = ContentValues(2)
        values.put(MediaStore.Video.Media.MIME_TYPE,"video/mp4")
        values.put(MediaStore.Video.Media.DATA,filePath)
        context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,values)
        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.parse("file://"+filePath)))
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