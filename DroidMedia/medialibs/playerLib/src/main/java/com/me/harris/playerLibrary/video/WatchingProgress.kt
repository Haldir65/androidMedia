@file:JvmName("MediaCodeView")
package com.me.harris.playerLibrary.video

import android.widget.SeekBar
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.me.harris.playerLibrary.VideoPlayView
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


fun watchProgress(activity: FragmentActivity,seekBar: SeekBar,player: VideoPlayView){
    activity.lifecycleScope.launch {
        delay(1000)
        while (currentCoroutineContext().isActive){
                val total = player.duration
                val current = player.currentPosition
                val thismax = seekBar.max
                val percentage = current.toFloat()/total.toFloat()
                seekBar.setProgress((thismax*percentage).toInt(),false)
                delay(100)
        }
    }

}