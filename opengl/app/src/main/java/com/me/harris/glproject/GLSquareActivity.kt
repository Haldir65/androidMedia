package com.me.harris.glproject

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.me.harris.glproject.view.FGLView

class GLSquareActivity:AppCompatActivity() {

    val glView by lazy {
        FGLView(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(glView)
    }

    override fun onResume() {
        super.onResume()
        glView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glView.onPause()
    }




}