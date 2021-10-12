package com.me.harris.droidmedia

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.liubing.filtertestbed.CameraEntryActivity
import com.me.harris.droidmedia.decode.DecodeActivity
import com.me.harris.droidmedia.decode.DecodeFrameActivity
import com.me.harris.droidmedia.encode.MediaCodecEncodeActivity
import com.me.harris.droidmedia.filter.VideoPlayFilterActivity
import com.me.harris.droidmedia.openglplayvideo.MediaPlayerSurfaceStubActivity
import com.me.harris.droidmedia.utils.VideoUtil
import com.me.harris.droidmedia.video.MediaCodecVideoMainActivity
import com.me.harris.droidmedia.video.VideoPlayExtryActivity
import com.me.harris.droidmedia.video.VideoPlayView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn1?.setOnClickListener {
            VideoUtil.setUrl()
            startActivity(Intent(this, com.me.harris.droidmedia.extractFrame.DecodeFrameActivity::class.java))
        }
        btn2?.setOnClickListener {
            VideoUtil.setUrl()
            startActivity(Intent(this, DecodeFrameActivity::class.java))
        }
        btn3?.setOnClickListener {
            VideoUtil.setUrl()
            startActivity(Intent(this, DecodeActivity::class.java))
        }

        btn4?.setOnClickListener {
            VideoUtil.setUrl()
            startActivity(Intent(this, CameraEntryActivity::class.java))
        }

        btn5?.setOnClickListener {
            VideoUtil.setUrl()
            startActivity(Intent(this, VideoPlayExtryActivity::class.java))
        }

        btn6?.setOnClickListener {
            VideoUtil.setUrl()
            startActivity(Intent(this, VideoPlayFilterActivity::class.java))
        }
        btn7?.setOnClickListener {
            VideoUtil.setUrl()
            startActivity(Intent(this, MediaCodecEncodeActivity::class.java))
        }
    }


    override fun onResume() {
        super.onResume()
        checkPermissions()
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                1098
            )
        }
    }
}
