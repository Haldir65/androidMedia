package com.jadyn.mediakit.framer

import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.jadyn.mediakit.databinding.ActivityDecodeFrameBinding
import com.jadyn.mediakit.function.durationSecond
import com.jadyn.mediakit.video.decode.VideoAnalyze
import com.jadyn.mediakit.video.decode.VideoDecoder2
import com.me.harris.awesomelib.utils.VideoUtil

internal class MediaKitDecodeFrameActivity : AppCompatActivity() {

    private val decodeMP4Path = VideoUtil.strVideo



    private var videoAnalyze: VideoAnalyze? = null

    private var videoDecoder2: VideoDecoder2? = null

    lateinit var binding: ActivityDecodeFrameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDecodeFrameBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        binding.fileFrameEt.setText(decodeMP4Path)

        binding.sureVideo.setOnClickListener {
            val dataSource = decodeMP4Path
            if (videoAnalyze != null && videoAnalyze!!.dataSource.equals(dataSource)) {
                return@setOnClickListener
            }
            videoAnalyze = VideoAnalyze(dataSource)
            binding.videoSeek.max = videoAnalyze!!.mediaFormat.durationSecond
            binding.videoSeek.progress = 0

            binding.msVideoSeek.max = videoAnalyze!!.mediaFormat.durationSecond * 1000
            binding.msVideoSeek.progress = 0

            videoDecoder2 = VideoDecoder2(dataSource)
            updateTime(0, binding.videoSeek.max)
        }

        binding.videoSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                videoAnalyze?.apply {
                    updateTime(progress, mediaFormat.durationSecond)
                }
                videoDecoder2?.apply {
                    getFrame(seekBar.progress.toFloat(), {
                        binding.frameImg.setImageBitmap(it)
                    }, {
                        Log.d("cece", "throwable ${it.message}: ")
                    })
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

        binding.msVideoSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                videoAnalyze?.apply {
                    updateTime(seekBar.progress, mediaFormat.durationSecond * 1000)
                }
                videoDecoder2?.apply {
                    getFrameMs(seekBar.progress.toLong(), {
                        binding.frameImg.setImageBitmap(it)
                    }, {
                        Log.d("cece", "throwable ${it.message}: ")
                    })
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
    }

    private fun updateTime(progress: Int, max: Int) {
        binding.time.text = "现在 : $progress 总时长为 : $max"
    }

    override fun onDestroy() {
        super.onDestroy()
        videoDecoder2?.release()
    }
}
