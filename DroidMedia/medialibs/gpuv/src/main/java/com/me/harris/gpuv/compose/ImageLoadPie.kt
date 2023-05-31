@file:JvmName("ImageLoader")
package com.me.harris.gpuv.compose

import android.content.Context
import android.widget.ImageView
import coil.load


fun loadImage(view:ImageView,data:String,context:Context){
    view.load(data)
}

