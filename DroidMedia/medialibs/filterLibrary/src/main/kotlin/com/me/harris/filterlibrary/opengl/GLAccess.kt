package com.me.harris.filterlibrary.opengl

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.view.Surface

object GLAccess {

    init {
        System.loadLibrary("nativefilter");
    }

    // https://github.com/yishuinanfeng/opengl-es-study-demo/commit/9cd65a745df1b26e5f7efe0a587527b8aefa4781
    external fun drawTexture(bitmap:Bitmap,bitmap1:Bitmap,surface:Surface)

    external fun loadYuv(surface:Surface,assetmanager:AssetManager)

}