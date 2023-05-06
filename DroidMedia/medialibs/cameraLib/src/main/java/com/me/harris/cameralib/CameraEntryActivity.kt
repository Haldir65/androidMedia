package com.me.harris.cameralib

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.cameralib.CameraV1GLSurfaceView.CameraV1GLSurfaceViewActivity
import com.me.harris.cameralib.CameraV1TextureView.CameraV1TextureViewActivity
import com.me.harris.cameralib.CameraV2GLSurfaceView.CameraV2GLSurfaceViewActivity

class CameraEntryActivity :AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
        findViewById<Button>(R.id.buytton1).setOnClickListener {
            startActivity(Intent(this, CameraV1GLSurfaceViewActivity::class.java))
        }
        findViewById<Button>(R.id.buytton2).setOnClickListener {
            startActivity(Intent(this, CameraV1TextureViewActivity::class.java))
        }

        findViewById<Button>(R.id.buytton3).setOnClickListener {
            startActivity(Intent(this, CameraV2GLSurfaceViewActivity::class.java))
        }
        Utils.checkCameraPermission(this)
    }

}