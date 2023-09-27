package com.me.harris.cameralib

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.contextaware.withContextAvailable
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.me.harris.cameralib.cameraX.CameraXSampleActivity
import com.me.harris.cameralib.CameraV1GLSurfaceView.CameraV1GLSurfaceViewActivity
import com.me.harris.cameralib.CameraV1TextureView.CameraV1TextureViewActivity
import com.me.harris.cameralib.CameraV2GLSurfaceView.CameraV2GLSurfaceViewActivity
import com.me.harris.cameralib.cameraX.CameraXTutorialActivity
import com.me.harris.cameralib.camerarecord.CameraToH264Activity
import kotlinx.coroutines.*

class CameraEntryActivity :AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
        findViewById<ViewGroup>(R.id.button1).setOnClickListener {
            startActivity(Intent(this, CameraV1GLSurfaceViewActivity::class.java))
        }
        findViewById<ViewGroup>(R.id.button2).setOnClickListener {
            startActivity(Intent(this, CameraV1TextureViewActivity::class.java))
        }

        findViewById<ViewGroup>(R.id.button3).setOnClickListener {
            startActivity(Intent(this, CameraV2GLSurfaceViewActivity::class.java))
        }
        findViewById<ViewGroup>(R.id.button4).setOnClickListener {
            startActivity(Intent(this, CameraXTutorialActivity::class.java))
        }
        findViewById<CardView>(R.id.button5).setOnClickListener {
            startActivity(Intent(this, CameraToH264Activity::class.java))
        }


        findViewById<CardView>(R.id.button6).setOnClickListener {
            startActivity(Intent(this, CameraXSampleActivity::class.java))
            Toast.makeText(this,"CameraX ,todo ",Toast.LENGTH_SHORT).show()
        }

        lifecycleScope.launch {
            withContextAvailable {
                Utils.checkCameraPermission(this@CameraEntryActivity)
            }
        }
    }

}
