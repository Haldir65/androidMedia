package com.me.harris

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.core.app.ActivityCompat
import com.me.harris.awesomelib.utils.StoragePermissSucks
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.ui.screen.MediaMainScreen
import com.me.harris.ui.theme.ABluromaticTheme

class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ABluromaticTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MediaMainScreen()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermissions()
    }

    private fun checkPermissions() {
        val storagePermission = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val cameraPermission =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val audioPermission = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (!storagePermission || !cameraPermission || !audioPermission) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                ),
                1098
            )
        } else {
            VideoUtil.setUrl()
//            startActivity(Intent(this, ExtractFrameAndSaveKeyFrameToFileActivity::class.java))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Build.VERSION.SDK_INT!=Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            if (!Environment.isExternalStorageManager()) {
                StoragePermissSucks.grantManageExternalStoragePermission(this)
            }
        }
    }


}
