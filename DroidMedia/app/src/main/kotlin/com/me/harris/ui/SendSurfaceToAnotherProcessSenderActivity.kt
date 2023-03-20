package com.me.harris.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.awesomelib.viewBinding
import com.me.harris.droidmedia.IPlayerService
import com.me.harris.droidmedia.R
import com.me.harris.droidmedia.databinding.ActivitySendSurfaceToAnotherProcessBinding
import com.me.harris.viewmodels.SendSurfaceToAnotherProcessViewModel

class SendSurfaceToAnotherProcessSenderActivity:AppCompatActivity(R.layout.activity_send_surface_to_another_process) {

    private val binding by viewBinding(ActivitySendSurfaceToAnotherProcessBinding::bind)
    private val viewModel by viewModels<SendSurfaceToAnotherProcessViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    private fun startRemotePlayerService(){
        
    }





}