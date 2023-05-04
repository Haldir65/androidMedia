package com.me.harris.ipc

import android.app.ActivityManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.os.Process
import android.util.Log
import android.view.Surface
import androidx.core.content.getSystemService
import com.me.harris.droidmedia.IPlayerService
import com.me.harris.droidmedia.model.MessageModel
import com.me.harris.awesomelib.utils.VideoUtil

class RemoteMediaPlayerBackEndService:Service() {

    lateinit var stub: IPlayerService.Stub
    override fun onCreate() {
        super.onCreate()
        Log.e("=A=", "onCreate in Service process Name = ${currentProcessName()}")
        initStub()
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.e("=A=", "onBind in Service process Name = ${currentProcessName()}")
        return stub
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    private fun initStub() {
        VideoUtil.setUrl()
        val player = MediaPlayer()
        player.setOnPreparedListener {
            logCurrentProcess()
            Log.w("=A=", "onPrepared process Name = ${currentProcessName()}")
            it.start()
        }
        stub = object : IPlayerService.Stub() {
            override fun sendMessage(messageModel: MessageModel?) {
                logCurrentProcess()
            }

            override fun getDuration() = player.duration


            override fun isPlaying(): Boolean {
                return player.isPlaying
            }

            override fun setLooping(looping: Boolean) {
                logCurrentProcess()
                player.isLooping = looping
            }

            override fun setDataSource(path: String?) {
                logCurrentProcess()
                player.setDataSource(path)
            }

            override fun prepare() {
                player.prepare()
            }

            override fun prepareAsync() {
                logCurrentProcess()
                player.prepareAsync()
            }

            override fun release() {
                player.release()
            }

            override fun reset() {
                player.reset()
            }

            override fun seekTo(millsec: Int) {
                player.seekTo(millsec)
            }

            override fun setSurface(surface: Surface?) {
                player.setSurface(surface)
            }

            override fun start() {
                player.start()
            }

            override fun stop() {
                player.stop()
            }

            override fun pause() {
                player.pause()
            }

            override fun setOnErrorListener() {
//                player.setOnErrorListener()
            }
        }
    }

    private fun logCurrentProcess() {
        Log.w(
            "=A=",
            "current process id = ${android.os.Process.myPid()} process Name = ${currentProcessName()}"
        )
    }

    private fun currentProcessName(): String {
        // Log.d(TAG, "getCurrentProcessName");
        val pid = Process.myPid()
        val processes = this.getSystemService<ActivityManager>()!!.runningAppProcesses
        return processes.firstOrNull { it.pid == pid }?.processName.orEmpty()
    }
}