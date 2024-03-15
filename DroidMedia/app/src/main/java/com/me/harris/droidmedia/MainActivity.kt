package com.me.harris.droidmedia

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.daasuu.epf.VideoClipEntryActivity
import com.jadyn.mediakit.MediaKitEntryActivity
import com.me.harris.AwesomePickVideoActivity
import com.me.harris.audiolib.AudioLibEntryActivity
import com.me.harris.avif.AvIfEntryActivity
import com.me.harris.awesomelib.utils.StoragePermissSucks
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.awesomelib.viewBinding
import com.me.harris.cameralib.CameraEntryActivity
import com.me.harris.droidmedia.databinding.ActivityMainBinding
import com.me.harris.droidmedia.entity.JUserInfo
import com.me.harris.droidmedia.entity.SubUserInfo
import com.me.harris.droidmedia.entity.UserInfo
import com.me.harris.extractframe.ExtractFrameEntryActivity
import com.me.harris.filterlibrary.FilterEntryActivity
import com.me.harris.filterlibrary.opengl.enrty.OpenGlEntryActivity
import com.me.harris.gpuvideo.GPUVideoEntryActivity
import com.me.harris.libjpeg.ui.LibJpegEntryActivity
import com.me.harris.mediainfo.MediaInfoProbeActivity
import com.me.harris.playerLibrary.VideoPlayExtryActivity
import com.me.harris.pnglib.PngLibEntryActivity
import com.me.harris.simdjson.SimdJsonEntryActivity

@Deprecated("replace with compose")
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding by viewBinding(ActivityMainBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.btn1.setOnClickListener {
            startActivity(Intent(this, VideoPlayExtryActivity::class.java))
        }
        binding.btn2.setOnClickListener {
            startActivity(Intent(this, CameraEntryActivity::class.java))
        }
        binding.btn3.setOnClickListener {
            startActivity(Intent(this, MediaKitEntryActivity::class.java))
        }
        binding.btn4.setOnClickListener {
            startActivity(Intent(this, FilterEntryActivity::class.java))
        }
        binding.btn5.setOnClickListener {
            VideoUtil.setUrl()
            startActivity(Intent(this, OpenGlEntryActivity::class.java))
        }

        binding.btn6.setOnClickListener {
            VideoUtil.setUrl()
            startActivity(Intent(this, VideoClipEntryActivity::class.java))
        }
        binding.btn7.setOnClickListener {
            VideoUtil.setUrl()
            val user1 = UserInfo("john", 100L, arrayListOf("user1", "user2"))
            val user2 = SubUserInfo("john2", 101L, arrayListOf("subaffs"), arrayOf("SubJohn"))
            val user3 = JUserInfo("john3", 101L, arrayListOf("aff1", "aff2"))
            startActivity(Intent(this, ExtractFrameEntryActivity::class.java).apply {
                putExtra("user3", user3)
                putExtra("user1", user1)
                putExtra("user2", user2)
            })
        }
        binding.btn8.setOnClickListener {
            VideoUtil.setUrl()
            startActivity(Intent(this,GPUVideoEntryActivity::class.java))
        }
        binding.btn9.setOnClickListener {
            VideoUtil.setUrl()
            startActivity(Intent(this,PngLibEntryActivity::class.java))
        }
        binding.btn10.setOnClickListener {
            startActivity(Intent(this, LibJpegEntryActivity::class.java))
        }

        binding.btn11.setOnClickListener {
            startActivity(Intent(this, AudioLibEntryActivity::class.java))
        }
        binding.btn12.setOnClickListener {
            startActivity(Intent(this, AwesomePickVideoActivity::class.java))

//         ServiceHelper.getService(DisplayB::class.java)?.name.orEmpty().let { Log.w("=A=","service loader on Android works ${it}") }
//                .name.orEmpty().let { Log.w("=A=","service loader on Android works") }
//        ServiceHelper.getService(PlayerLibService::class.java)?.startPlayVideoActivity(activity = this, bundle = bundleOf(
//            KEY_VIDEO_URL to VideoUtil.strVideo))
        }

        binding.btn13.setOnClickListener {
            startActivity(Intent(this, MediaInfoProbeActivity::class.java))
        }

        binding.btn14.setOnClickListener {
            startActivity(Intent(this, AvIfEntryActivity::class.java))
        }
        binding.btn15.setOnClickListener {
            startActivity(Intent(this, SimdJsonEntryActivity::class.java))
        }
        binding.btn16.setOnClickListener {
            startActivity(Intent(this, com.me.harris.composeworkmanager.BlurActivity::class.java))
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                StoragePermissSucks.grantManageExternalStoragePermission(this)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1098 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.e("=A=", "we have full permission")
        }
    }
}
