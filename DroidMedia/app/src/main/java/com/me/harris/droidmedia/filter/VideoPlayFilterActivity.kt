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
import com.me.harris.droidmedia.utils.ToastUtils
import com.me.harris.droidmedia.utils.VideoUtil
import com.me.harris.droidmedia.video.VideoPlayView
import com.pinssible.librecorder.filter.Filters

import com.pinssible.librecorder.player.PinMediaPlayer
import com.pinssible.librecorder.player.SimplePinPlayerView
import java.lang.Exception

class VideoPlayFilterActivity:AppCompatActivity() {

    //view
    private  lateinit var previewSurface: SimplePinPlayerView
    private  lateinit var filterBtn: Button
    private lateinit var mBtn2: Button
    private lateinit var mTextView:TextView
    private lateinit var mImageView:ImageView

    //player
    private var player: PinMediaPlayer? = null

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
            player = PinMediaPlayer(this, source, true)
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
            Filters.FILTER_NONE -> "FILTER_NONE"
            Filters.FILTER_BLACK_WHITE -> "FILTER_BLACK_WHITE"
            Filters.FILTER_NIGHT -> "FILTER_NIGHT"
            Filters.FILTER_CHROMA_KEY -> "FILTER_CHROMA_KEY"
            Filters.FILTER_BLUR -> "FILTER_BLUR"
            Filters.FILTER_SHARPEN -> "FILTER_SHARPEN"
            Filters.FILTER_EDGE_DETECT -> "FILTER_EDGE_DETECT"
            Filters.FILTER_EMBOSS -> "FILTER_EMBOSS"
            Filters.FILTER_SQUEEZE -> "FILTER_SQUEEZE"
            Filters.FILTER_TWIRL -> "FILTER_TWIRL"
            Filters.FILTER_TUNNEL -> "FILTER_TUNNEL"
            Filters.FILTER_BULGE -> "FILTER_BULGE"
            Filters.FILTER_DENT -> "FILTER_DENT"
            Filters.FILTER_FISHEYE -> "FILTER_FISHEYE"
            Filters.FILTER_STRETCH -> "FILTER_STRETCH"
            Filters.FILTER_MIRROR -> "FILTER_MIRROR"
            Filters.FILTER_GPU_LERP_BLUR -> "FILTER_GPU_LERP_BLUR"
            else -> "unknown"
        }
    }

}