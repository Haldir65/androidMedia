package com.me.harris.gpuvideo.preview

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.daasuu.gpuv.R
import com.daasuu.gpuv.egl.filter.GlFilter
import com.daasuu.gpuv.gpuvideoandroid.FilterType
import com.daasuu.gpuv.player.GPUPlayerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.gpuv.FilterAdapter
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GPUVideoPreviewVideoActivity:AppCompatActivity(R.layout.activity_gpuvideo_preview) {


    private lateinit var gpuPlayerView: GPUPlayerView
    private  var player: ExoPlayer? = null
    private lateinit var button:Button
    private lateinit var timeSeekBar:SeekBar
    private lateinit var filterSeekBar:SeekBar
    private  var filter: GlFilter? = null
    private  var adjuster: FilterAdjuster? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        VideoUtil.setUrl()
        setUpViews()
    }

    override fun onResume() {
        super.onResume()
        setUpSimpleExoplayer()
        setUpGlPlayerView()
        setUpTimer()
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    private fun setUpViews(){
        button = findViewById(R.id.btn)
        button.setOnClickListener {
            when(button.text.toString()){
                getString(R.string.pause) ->{
                    player?.playWhenReady = false
                    button.text = getString(R.string.play)
                }
                else -> {
                    player?.playWhenReady = true
                    button.text = getString(R.string.pause)
                }
            }
        }

        timeSeekBar = findViewById(R.id.timeSeekBar)
        timeSeekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    player?.seekTo((progress*1000).toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        filterSeekBar = findViewById(R.id.filterSeekBar)
        filterSeekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                filter?.let { adjuster?.adjust(it,progress) }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        val listView = findViewById<ListView>(R.id.list)
        val filteTypes = FilterType.createFilterList()
        listView.adapter =
            FilterAdapter(this, R.layout.row_text, filteTypes)
        listView.setOnItemClickListener { parent, view, position, id ->
            val filter = FilterType.createGlFilter(filteTypes.get(position),applicationContext)
            val adjuster = FilterType.createFilterAdjuster(filteTypes.get(position))
            findViewById<View>(R.id.filterSeekBarLayout).isVisible = adjuster!=null
            gpuPlayerView.setGlFilter(filter)
        }

    }

    private fun setUpSimpleExoplayer(){
        player = ExoPlayer.Builder(this)
            .setTrackSelector(DefaultTrackSelector(this))
            .build()
        player?.addMediaItem(MediaItem.fromUri(VideoUtil.strVideo))
        player?.prepare()
        player?.playWhenReady = true
    }

    private fun setUpGlPlayerView(){
        gpuPlayerView = GPUPlayerView(this)
        gpuPlayerView.setExoPlayer(player)
        gpuPlayerView.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT)
        findViewById<MovieWrapperView>(R.id.layout_movie_wrapper).addView(gpuPlayerView)
        gpuPlayerView.onResume()
    }

    private fun setUpTimer(){
        lifecycleScope.launch {
            while (currentCoroutineContext().isActive){
                delay(1000)
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)){
                    val position = player?.currentPosition?:0
                    val duration = player?.duration?:0
                    if (duration>0){
                        timeSeekBar.max = ((duration/1000).toInt())
                        timeSeekBar.progress = ((position/1000).toInt())
                    }
                }
            }
        }
    }

    private fun releasePlayer(){
        gpuPlayerView.onPause()
        findViewById<MovieWrapperView>(R.id.layout_movie_wrapper).removeAllViews()
        player?.stop()
        player?.release()
        player = null
    }






}