package com.me.harris.droidmedia.filter

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.droidmedia.R
import com.me.harris.awesomelib.utils.ToastUtils
import com.me.harris.awesomelib.utils.VideoUtil
import com.pinssible.librecorder.filter.Filters

import com.pinssible.librecorder.player.PinMediaPlayer
import com.pinssible.librecorder.player.SimplePinPlayerView
import java.lang.Exception

class VideoPlayFilterActivity:AppCompatActivity() {

    //view
    private  lateinit var previewSurface: com.pinssible.librecorder.player.SimplePinPlayerView
    private  lateinit var filterBtn: Button
    private lateinit var mBtn2: Button
    private lateinit var mTextView:TextView
    private lateinit var mImageView:ImageView

    //player
    private var player: com.pinssible.librecorder.player.PinMediaPlayer? = null

    private var filterType = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_video_filter)

        //view
        previewSurface = findViewById(R.id.player_view)
        filterBtn = findViewById(R.id.btn_filter)
        mBtn2 = findViewById(R.id.btn2)
        mTextView = findViewById(R.id.text_cur_filter)
        mImageView = findViewById(R.id.image_top)
        //player

        //player
        VideoUtil.setUrl()
        val outputPath: String = VideoUtil.strVideo
        val source = Uri.parse(outputPath)
        try {
            player = com.pinssible.librecorder.player.PinMediaPlayer(
                this,
                source,
                true
            )
            previewSurface?.setPlayer(player)
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtils.showTextShort(this,"create player fail cause = $e")
        }


        //filter
        filterBtn?.setOnClickListener(View.OnClickListener {
            val type = filterType++ % 17
            Log.e("Test", "filterType = $type")
            player!!.setFilter(type)
            mTextView.text = filterTypeToText(type)
        })

        mBtn2.setOnClickListener {
            ToastUtils.showTextShort(this,"开始截屏！！")
            player?.render?.takeShot {
                bmp ->
                runOnUiThread {
                    mImageView.setImageBitmap(bmp)
                }
            }
        }


    }

    override fun finish() {
        super.finish()
        player?.stop()
    }

    fun filterTypeToText(type:Int):String{
        // Camera filters; must match up with camera_filter_names in strings.xml
        return when(type){
            com.pinssible.librecorder.filter.Filters.FILTER_NONE -> "FILTER_NONE"
            com.pinssible.librecorder.filter.Filters.FILTER_BLACK_WHITE -> "FILTER_BLACK_WHITE"
            com.pinssible.librecorder.filter.Filters.FILTER_NIGHT -> "FILTER_NIGHT"
            com.pinssible.librecorder.filter.Filters.FILTER_CHROMA_KEY -> "FILTER_CHROMA_KEY"
            com.pinssible.librecorder.filter.Filters.FILTER_BLUR -> "FILTER_BLUR"
            com.pinssible.librecorder.filter.Filters.FILTER_SHARPEN -> "FILTER_SHARPEN"
            com.pinssible.librecorder.filter.Filters.FILTER_EDGE_DETECT -> "FILTER_EDGE_DETECT"
            com.pinssible.librecorder.filter.Filters.FILTER_EMBOSS -> "FILTER_EMBOSS"
            com.pinssible.librecorder.filter.Filters.FILTER_SQUEEZE -> "FILTER_SQUEEZE"
            com.pinssible.librecorder.filter.Filters.FILTER_TWIRL -> "FILTER_TWIRL"
            com.pinssible.librecorder.filter.Filters.FILTER_TUNNEL -> "FILTER_TUNNEL"
            com.pinssible.librecorder.filter.Filters.FILTER_BULGE -> "FILTER_BULGE"
            com.pinssible.librecorder.filter.Filters.FILTER_DENT -> "FILTER_DENT"
            com.pinssible.librecorder.filter.Filters.FILTER_FISHEYE -> "FILTER_FISHEYE"
            com.pinssible.librecorder.filter.Filters.FILTER_STRETCH -> "FILTER_STRETCH"
            com.pinssible.librecorder.filter.Filters.FILTER_MIRROR -> "FILTER_MIRROR"
            com.pinssible.librecorder.filter.Filters.FILTER_GPU_LERP_BLUR -> "FILTER_GPU_LERP_BLUR"
            else -> "unknown"
        }
    }

}