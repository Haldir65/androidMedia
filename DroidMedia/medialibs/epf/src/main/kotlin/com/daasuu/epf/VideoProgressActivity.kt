package com.daasuu.epf

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.daasuu.epf.filter.FilterType
import com.daasuu.mp4compose.composer.Mp4Composer
import com.me.harris.awesomelib.ServiceHelper
import com.me.harris.serviceapi.KEY_VIDEO_URL
import com.me.harris.serviceapi.PlayerLibService
import com.spx.egl.GlFilterList
import com.spx.egl.GlFilterPeriod
import com.spx.egl.VideoProcessConfig

class VideoProgressActivity : AppCompatActivity() {
    companion object {
        const val TAG = "VideoProgressActivity"
    }

    lateinit var videoProcessConfig: VideoProcessConfig
    var glFilterList = GlFilterList()

    var handler = Handler()
    var progression = 0

    private lateinit var pb_progress:ProgressBar
    private lateinit var tv_progress:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.video_process_activity_layout)
        pb_progress = findViewById(R.id.pb_progress)
        tv_progress = findViewById(R.id.tv_progress)

        videoProcessConfig = intent.getSerializableExtra("videoProcessConfig") as VideoProcessConfig
        Log.d(TAG, "on create mediaPath:${videoProcessConfig.srcMediaPath}")

        val filterConfigList = videoProcessConfig.filterConfigList

        for (fconfig in filterConfigList){
            glFilterList.putGlFilter(GlFilterPeriod(fconfig.startTimeMs, fconfig.endTimeMs, FilterType.createGlFilter(fconfig.filterName, null, this)))
        }
    }

    override fun onResume() {
        super.onResume()
        var s = System.currentTimeMillis();
        var mp4Composer = Mp4Composer(videoProcessConfig.srcMediaPath, videoProcessConfig.outMediaPath)
            .frameRate(30)
            .filterList(glFilterList)
            .listener(object : Mp4Composer.Listener {
                override fun onProgress(_p: Double) {
                    Log.d(TAG, "onProgress $_p")
                    progression = (100 * _p).toInt()
                }

                override fun onCompleted() {
                    Log.d(TAG, "onCompleted()")
                    runOnUiThread {
                        var e = System.currentTimeMillis()
                        Toast.makeText(this@VideoProgressActivity, "生成视频成功,耗时${e-s}ms, 文件放在:${videoProcessConfig.outMediaPath}", Toast.LENGTH_LONG).show()
                        openVideoPlayerActivityViaServiceLoader(videoProcessConfig.outMediaPath)
                    }
                    progression = 100
                    finish()
                }

                override fun onCanceled() {
                    runOnUiThread {
                        Toast.makeText(this@VideoProgressActivity, "生成视频取消", Toast.LENGTH_LONG).show()
                    }

                }

                override fun onFailed(exception: Exception) {
                    Log.d(TAG, "onFailed()")
                    runOnUiThread {
                        Toast.makeText(this@VideoProgressActivity, "生成视频失败", Toast.LENGTH_LONG).show()
                    }

                }
            })
            .start()

        showProgress()
    }

    private fun showProgress() {
        if (progression > 100) {
            return
        }
        pb_progress.progress = progression
        tv_progress.text = "$progression%"
        if (progression <= 100) {
            handler.postDelayed({ showProgress() }, 200)
        }

    }

    private fun openVideoPlayerActivityViaServiceLoader(filePath:String){
         ServiceHelper.getService(PlayerLibService::class.java)?.startPlayVideoActivity(activity = this, bundle = bundleOf(
KEY_VIDEO_URL to filePath))
    }
}