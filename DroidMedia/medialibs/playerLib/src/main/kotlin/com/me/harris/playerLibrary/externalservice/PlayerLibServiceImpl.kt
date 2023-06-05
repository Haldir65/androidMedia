package com.me.harris.playerLibrary.externalservice

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.playerLibrary.VideoPlayExtryActivity
import com.me.harris.playerLibrary.openglplayvideo.MediaPlayerSurfaceStubActivity
import com.me.harris.serviceapi.KEY_VIDEO_URL
import com.me.harris.serviceapi.PlayerLibService
import kotlinx.coroutines.delay

class PlayerLibServiceImpl:PlayerLibService {
    override fun startPlayVideoActivity(activity: AppCompatActivity, bundle: Bundle) {
        val intent = Intent(activity, MediaPlayerSurfaceStubActivity::class.java)
        intent.putExtra(MediaPlayerSurfaceStubActivity.KEY_VIDEO_PATH,bundle.getString(KEY_VIDEO_URL).orEmpty())
        activity.startActivity(intent)
    }

    override suspend fun callSuspendFucnction() {
        delay(1000)
        Log.w("=A=","call SuspendFunction via serviceLoader is possible")
    }
}