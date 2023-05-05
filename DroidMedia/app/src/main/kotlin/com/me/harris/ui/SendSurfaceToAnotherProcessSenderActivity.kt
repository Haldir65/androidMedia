package com.me.harris.ui

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.awesomelib.viewBinding
import com.me.harris.droidmedia.IPlayerService
import com.me.harris.droidmedia.R
import com.me.harris.droidmedia.databinding.ActivitySendSurfaceToAnotherProcessBinding
import com.me.harris.ipc.RemoteMediaPlayerBackEndService
import com.me.harris.viewmodels.SendSurfaceToAnotherProcessViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class SendSurfaceToAnotherProcessSenderActivity:AppCompatActivity(R.layout.activity_send_surface_to_another_process) {

    private val binding by viewBinding(ActivitySendSurfaceToAnotherProcessBinding::bind)
    private val viewModel by viewModels<SendSurfaceToAnotherProcessViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root.post {
            bindService(Intent(this,RemoteMediaPlayerBackEndService::class.java),connnection,
                BIND_AUTO_CREATE
            )
        }
        binding.btn1.setOnClickListener {
            configurePlayerInAnotherProcess()
        }
        binding.btn2.setOnClickListener {
            playerService?.pause()
        }

        binding.btn3.setOnClickListener {
            playerService?.resume()
        }

        val callback = object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                playerService?.stop()
                playerService?.release() // crash?
                playerService?.let {
                    unbindService(connnection)
                }
                playerService = null
                isEnabled = false // 1. step 1
                onBackPressedDispatcher.onBackPressed() // 2. step 2
            }
        }
        onBackPressedDispatcher.addCallback(callback)
        binding.seekbar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                playerService?.run {
                    val duration = this.duration
                    val targetDuration = duration * (seekBar.progress*1.0f/seekBar.max)
                    seekTo(targetDuration.toInt())
                }
            }
        })

        lifecycleScope.launch {
            while (isActive){
                delay(1000)
                playerService?.let { player ->
                    if (player.isPlaying){
                        val currentProgress = player.currentPosition
                        val duration = player.duration
                        val percentage = currentProgress.toFloat()/duration
                        binding.seekbar.progress = (binding.seekbar.max*percentage).toInt()
                    }
                }
            }
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