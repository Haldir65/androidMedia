package com.me.harris.droidmedia.opengl

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.droidmedia.R
import com.me.harris.droidmedia.opengl.egl.TextureViewRenderViaEglActivity

class OpenGlEntryActivity:AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opengl_entry)
        findViewById<Button>(R.id.btn1)?.setOnClickListener {
            startActivity(Intent(this,TextureViewRenderViaEglActivity::class.java))
        }

    }


}