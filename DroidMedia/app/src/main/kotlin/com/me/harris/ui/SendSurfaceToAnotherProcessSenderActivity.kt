package com.me.harris.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.view.Surface
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.addCallback
import com.me.harris.awesomelib.viewBinding
import com.me.harris.droidmedia.IPlayerService
import com.me.harris.droidmedia.R
import com.me.harris.droidmedia.databinding.ActivitySendSurfaceToAnotherProcessBinding
import com.me.harris.droidmedia.utils.VideoUtil
import com.me.harris.droidmedia.video.sharedSurface.SharedSurfaceManager
import com.me.harris.ipc.RemoteMediaPlayerBackEndService
import com.me.harris.viewmodels.SendSurfaceToAnotherProcessViewModel

class SendSurfaceToAnotherProcessSenderActivity:AppCompatActivity(R.layout.activity_send_surface_to_another_process) {

    private val binding by viewBinding(ActivitySendSurfaceToAnotherProcessBinding::bind)
    private val viewModel by viewModels<SendSurfaceToAnotherProcessViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root.post {
            bindService(Intent(this,RemoteMediaPlayerBackEndService::class.java),connnection,Context.BIND_AUTO_CREATE)
        }
        binding.btn1.setOnClickListener {
            configurePlayerInAnotherProcess()
        }
        binding.btn2.setOnClickListener {
            playerService?.run {
                pause()
            }
        }

        binding.btn3.setOnClickListener {
            playerService?.run {
                start()
            }
        }

        onBackPressedDispatcher.addCallback(this /* lifecycle owner */) {
            // onBackPressed deprecated?
            //
            playerService?.run {
                stop()
                release()
            }
            finish()
        }
    }

    private fun configurePlayerInAnotherProcess() {
        VideoUtil.setUrl()
        playerService?.run {
            setDataSource(VideoUtil.strVideo)
            setSurface(binding.playerSurfaceView.holder.surface)
            setLooping(true)
            prepareAsync()
        }

//        MediaPlayer().also {
//            mMediaPlayer = it
//            it.isLooping = true
//            SharedSurfaceManager.mPlayer = it
//
//            SharedSurfaceManager.playingUrl = VideoUtil.strVideo
//            it.setDataSource(SharedSurfaceManager.playingUrl)
//            val surface = SharedSurfaceManager.mSurfaceTexture ?: kotlin.run {
//                surfaceTexture
//            }
//            it.setSurface(Surface(surface))
//            it.setOnPreparedListener {
//                it?.start()
//            }
//            it.prepareAsync()
//        }
    }

    private var playerService:IPlayerService? = null

    private val connnection = object :ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            playerService = IPlayerService.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            playerService = null
        }

    }


    private fun startRemotePlayerService(){

    }




}