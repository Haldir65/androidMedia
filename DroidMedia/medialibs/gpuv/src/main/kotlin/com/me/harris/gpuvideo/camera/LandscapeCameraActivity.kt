package com.me.harris.gpuvideo.camera

import android.os.Bundle
import com.daasuu.gpuv.R

class LandscapeCameraActivity:BaseCameraActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_landscape)
        onCreateActivity()
        videoWidth = 1280
        videoHeight = 720
        cameraWidth = 1280
        cameraHeight = 720
    }


}