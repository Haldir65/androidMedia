package com.me.harris.awesomelib

import android.graphics.SurfaceTexture
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import android.widget.SeekBar
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun SurfaceView.surfaceFlow(): Flow<SurfaceHolder> {
    return callbackFlow {
        val cb = object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                Log.w("=A=","holder obtained")
                trySend(holder)
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }
        }
        holder.addCallback(cb)
        awaitClose {
            holder.removeCallback(cb)
        }
    }
}

 inline fun SurfaceView.withSurfaceAvailable(crossinline action:((holder:SurfaceHolder) -> Unit )){
        Log.w("=A=","withSurfaceAvailable launch")
        holder.addCallback(object :SurfaceHolder.Callback{
            override fun surfaceCreated(holder: SurfaceHolder) {
                Log.w("=A=","withSurfaceAvailable surfaceCreated")
                action(holder)
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                Log.w("=A=","withSurfaceAvailable surfaceChanged")
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                Log.w("=A=","withSurfaceAvailable surfaceChanged")
            }
        })
}

inline fun TextureView.withSurfaceAvailable(crossinline action: (holder: SurfaceTexture) -> Unit) {
    surfaceTextureListener = object :TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            action(surface)
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            return true
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

        }
    }
}

inline fun SeekBar.whenProgressChanged(crossinline action: (seekBar: SeekBar) -> Unit) {
    setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            action(seekBar)
        }
    })
}