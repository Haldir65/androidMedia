package com.me.harris.extractframe.finale

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.awesomelib.viewBinding
import com.me.harris.extractframe.R
import com.me.harris.extractframe.databinding.FrameExtractFinalActivityBinding
import java.io.File

class FrameExtractFinalActivity:AppCompatActivity(R.layout.frame_extract_final_activity)
{

    private val binding by viewBinding(FrameExtractFinalActivityBinding::bind)
    private val viewModel by viewModels<FrameExtractFinaleViewModel>()


    val saveDir by lazy {
        "${application.filesDir}${File.separator}photos4"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        start()
    }

    private fun start(){
        val filepath = VideoUtil.strVideo

        viewModel.startExtract()
    }

    private fun makeSureSaveDirExists(){
        val fileStorageDir = File(saveDir)
        fileStorageDir.deleteRecursively()
        fileStorageDir.mkdir()
    }





}
