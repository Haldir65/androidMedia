package cameraX

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.cameralib.databinding.ActivityCameraxSampleBinding

class CameraXSampleActivity:AppCompatActivity() {

    private lateinit var binding:ActivityCameraxSampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraxSampleBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}
