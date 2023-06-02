package com.me.harris.gpuvideo.camera

import android.os.Bundle
import com.daasuu.gpuv.R

class PortraitCameraActivity:BaseCameraActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_portrate)
        onCreateActivity()
        videoWidth = 720
        videoHeight = 1280
        cameraWidth = 1280
        cameraHeight = 720
    }
}