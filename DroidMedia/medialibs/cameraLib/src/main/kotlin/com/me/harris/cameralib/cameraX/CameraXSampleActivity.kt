package com.me.harris.cameralib.cameraX

import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import com.me.harris.cameralib.databinding.ActivityCameraxSampleBinding
import com.me.harris.cameralib.misc.getCameraHardwareSupport

class CameraXSampleActivity:AppCompatActivity() {

    private lateinit var binding:ActivityCameraxSampleBinding
    // https://developer.android.com/codelabs/camerax-getting-started#1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraxSampleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val manage:CameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        getCameraHardwareSupport(manage)
    }

}
