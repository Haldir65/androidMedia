package com.me.harris.gpuvideo.camera

import android.os.Bundle
import com.daasuu.gpuv.R

class SquareCameraActivity:BaseCameraActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_square)
        onCreateActivity()
    }
}