package com.me.harris.cameralib

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.contextaware.withContextAvailable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.me.harris.cameralib.CameraV1GLSurfaceView.CameraV1GLSurfaceViewActivity
import com.me.harris.cameralib.CameraV1TextureView.CameraV1TextureViewActivity
import com.me.harris.cameralib.CameraV2GLSurfaceView.CameraV2GLSurfaceViewActivity
import kotlinx.coroutines.launch

class CameraEntryActivity :AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
        findViewById<ViewGroup>(R.id.buytton1).setOnClickListener {
            startActivity(Intent(this, CameraV1GLSurfaceViewActivity::class.java))
        }
        findViewById<ViewGroup>(R.id.buytton2).setOnClickListener {
            startActivity(Intent(this, CameraV1TextureViewActivity::class.java))
        }

        findViewById<ViewGroup>(R.id.buytton3).setOnClickListener {
            startActivity(Intent(this, CameraV2GLSurfaceViewActivity::class.java))
        }
        lifecycleScope.launch {
            withContextAvailable {
                Utils.checkCameraPermission(this@CameraEntryActivity)
            }
        }
    }

}