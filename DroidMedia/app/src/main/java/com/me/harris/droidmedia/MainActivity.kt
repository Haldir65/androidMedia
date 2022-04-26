package com.me.harris.droidmedia

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.liubing.filtertestbed.CameraEntryActivity
import com.me.harris.droidmedia.decode.DecodeActivity
import com.me.harris.droidmedia.decode.DecodeFrameActivity
import com.me.harris.droidmedia.encode.MediaCodecEncodeActivity
import com.me.harris.droidmedia.entity.JUserInfo
import com.me.harris.droidmedia.entity.SubUserInfo
import com.me.harris.droidmedia.entity.UserInfo
import com.me.harris.droidmedia.extractFrame.ExtractFrameAndSaveKeyFrameToFileActivity
import com.me.harris.droidmedia.filter.VideoPlayFilterActivity
import com.me.harris.droidmedia.opengl.OpenGlEntryActivity
import com.me.harris.droidmedia.utils.VideoUtil
import com.me.harris.droidmedia.video.VideoPlayExtryActivity
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
        btn8?.setOnClickListener {
            VideoUtil.setUrl()
            startActivity(Intent(this, OpenGlEntryActivity::class.java))
        }
        btn9?.setOnClickListener {
            VideoUtil.setUrl()
            val user1 = UserInfo("john",100L, arrayListOf("user1","user2"))
            val user2 = SubUserInfo("john2",101L, arrayListOf("subaffs"), arrayOf("SubJohn"))
            val user3 = JUserInfo("john3",101L, arrayListOf("aff1","aff2"))
            startActivity(Intent(this, ExtractFrameAndSaveKeyFrameToFileActivity::class.java).apply {
                putExtra("user3",user3)
                putExtra("user1",user1)
                putExtra("user2",user2)
            })
//            startActivity(Intent(this,MyAudioPlayerActivity::class.java))
        }
    }


    override fun onResume() {
        super.onResume()
        checkPermissions()
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission_group.STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission_group.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission_group.STORAGE,
                    Manifest.permission_group.CAMERA
                ),
                1098
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1098 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.e("=A=","we have full permission")
        }
    }
}
