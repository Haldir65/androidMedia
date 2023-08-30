package com.me.harris.extractframe.glextractor

import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.awesomelib.viewBinding
import com.me.harris.extractframe.R
import com.me.harris.extractframe.databinding.FrameExtractGlActivityBinding
import java.io.File

class ExtractUingOpenglActivity :AppCompatActivity(R.layout.frame_extract_gl_activity){

    private val binding by viewBinding(FrameExtractGlActivityBinding::bind)
    private val viewModel by viewModels<FrameExtractGlViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    private fun setupGL(){
        val filepath = VideoUtil.strVideo
        val extractor = VideoFrameExtractor(this, Uri.fromFile(File(filepath)))
//        extractor.extractMpegFrames()

    }




}
