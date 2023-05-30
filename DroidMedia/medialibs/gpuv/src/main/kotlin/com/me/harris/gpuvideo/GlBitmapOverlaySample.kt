package com.me.harris.gpuvideo

import android.graphics.Bitmap
import android.graphics.Canvas
import com.daasuu.gpuv.egl.filter.GlOverlayFilter

class GlBitmapOverlaySample(val bitmap:Bitmap?):GlOverlayFilter() {
    override fun drawCanvas(canvas: Canvas?) {
        bitmap?.takeIf { !it.isRecycled}?.let {
            canvas?.drawBitmap(it,0.0f,0.0f,null)
        }
    }
}