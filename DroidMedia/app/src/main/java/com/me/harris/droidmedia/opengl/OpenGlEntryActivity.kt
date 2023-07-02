package com.me.harris.droidmedia.opengl

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.droidmedia.R
import com.me.harris.droidmedia.databinding.ActivityOpenglEntryBinding
import com.me.harris.droidmedia.opengl.egl.TextureViewRenderViaEglActivity
import com.me.harris.filterlibrary.opengl.GLGaussianBlurActivity
import com.me.harris.filterlibrary.opengl.GLLoadYUVActivity
import com.me.harris.filterlibrary.opengl.GLMixTwoPictureActivity
import com.me.harris.filterlibrary.opengl.GLTransformActivity

class OpenGlEntryActivity:AppCompatActivity()
{

    private lateinit var binding:ActivityOpenglEntryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenglEntryBinding.inflate(layoutInflater).also { setContentView(it.root) }
        binding.btn1.setOnClickListener {
            startActivity(Intent(this,TextureViewRenderViaEglActivity::class.java))
        }
        binding.btn2.setOnClickListener {
            startActivity(Intent(this,GLMixTwoPictureActivity::class.java))
        }
        binding.btn3.setOnClickListener {
            startActivity(Intent(this,GLLoadYUVActivity::class.java))
        }
        binding.btn4.setOnClickListener {
            startActivity(Intent(this,GLTransformActivity::class.java))
        }
        binding.btn5.setOnClickListener {
            startActivity(Intent(this,GLGaussianBlurActivity::class.java))
        }

    }


}