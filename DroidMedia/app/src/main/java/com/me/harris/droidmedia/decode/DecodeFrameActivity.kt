package com.me.harris.droidmedia.decode

import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.jadyn.mediakit.function.durationSecond
import com.jadyn.mediakit.video.decode.VideoAnalyze
import com.jadyn.mediakit.video.decode.VideoDecoder2
import com.me.harris.droidmedia.R

class DecodeFrameActivity class DecodeFrameActivity : AppCompatActivity() {

    private val decodeMP4Path = TextUtils.concat(
        Environment.getExternalStorageDirectory().path,
        "/yazi.mp4").toString()

    private var videoAnalyze: VideoAnalyze? = null

    private var videoDecoder2: VideoDecoder2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decode_frame)
        file_frame_et.setText(decodeMP4Path)

        sure_video.setOnClickListener {
            val dataSource = file_frame_et.text.toString()
            if (videoAnalyze != null && videoAnalyze!!.dataSource.equals(dataSource)) {
                return@setOnClickListener
            }
            videoAnalyze = VideoAnalyze(dataSource)
            video_seek.max = videoAnalyze!!.mediaFormat.durationSecond
            video_seek.progress = 0

            ms_video_seek.max = videoAnalyze!!.mediaFormat.durationSecond * 1000
            ms_video_seek.progress = 0

            videoDecoder2 = VideoDecoder2(dataSource)
            updateTime(0, video_seek.max)
        }

        video_seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                videoAnalyze?.apply {
                    updateTime(progress, mediaFormat.durationSecond)
                }
                videoDecoder2?.apply {
                    getFrame(seekBar.progress.toFloat(), {
                        frame_img.setImageBitmap(it)
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

        ms_video_seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                videoAnalyze?.apply {
                    updateTime(seekBar.progress, mediaFormat.durationSecond * 1000)
                }
                videoDecoder2?.apply {
                    getFrameMs(seekBar.progress.toLong(), {
                        frame_img.setImageBitmap(it)
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
        time.text = "现在 : $progress 总时长为 : $max"
    }

    override fun onDestroy() {
        super.onDestroy()
        videoDecoder2?.release()
    }
}