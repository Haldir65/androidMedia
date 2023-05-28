package com.me.harris.filterlibrary.baisc

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daasuu.epf.filter.GlFilter
import com.me.harris.awesomelib.applicationViewModels
import com.me.harris.awesomelib.updateProgressWithMediaPlayer
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.awesomelib.viewBinding
import com.me.harris.awesomelib.whenProgressChanged
import com.me.harris.filterlibrary.R
import com.me.harris.filterlibrary.databinding.ActivityFilterBasicBinding
import com.me.harris.filterlibrary.databinding.ItemFilterOptionBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FilterBasicActivity : AppCompatActivity(R.layout.activity_filter_basic) {

    private val binding by viewBinding(ActivityFilterBasicBinding::bind)
    private val viewModel by viewModels<FilterBasicViewModel>()
    private lateinit var mediaPath: String
    private val adapter by lazy {
        FilterListAdapter(this, setupAllFilters(this).toMutableList()) { filter ->
            val duration = binding.playerViewMp.mMediaPlayer.duration
            binding.playerViewMp.getFilterList()
            binding.playerViewMp.setFiler(0, duration.toLong(), filter)
            binding.inuseFilter.text = "当前生效的滤镜: ${filter.name}"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaPath = VideoUtil.strVideo
        binding.playerViewMp.setDataSource(mediaPath)
        binding.playerViewMp.start()
        binding.seekbar.whenProgressChanged(::seekWhenStopTracking)
        binding.allFilters.adapter = adapter
        binding.allFilters.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.allFilters.addItemDecoration(CornerDecoration(this))
        lifecycleScope.launch {
            delay(1000)
            binding.seekbar.updateProgressWithMediaPlayer(binding.playerViewMp.mMediaPlayer)
        }
        binding.btnPause.setOnClickListener { binding.playerViewMp.pausePlay() }
        binding.btnResume.setOnClickListener { binding.playerViewMp.resumePlay() }


        // has effect
//        binding.btnFilter1.setOnClickListener {  binding.playerViewMp.addFiler(0,400_000, GlMonochromeFilter()) }
//        binding.btnFilter1.setOnClickListener {  binding.playerViewMp.addFiler(0,400_000, GLImageBeautyHighPassFilter()) }


        // no effect
//        binding.btnFilter1.setOnClickListener {  binding.playerViewMp.addFiler(0,400_000, GLImageBeautyFilter(this)) }

    }

    override fun onResume() {
        super.onResume()
        binding.playerViewMp.resumePlay()
    }

    override fun onStop() {
        super.onStop()
        binding.playerViewMp.pausePlay()
    }

    private fun seekWhenStopTracking(seekBar: SeekBar) {
        val player = binding.playerViewMp.mMediaPlayer
        player?.run {
            val duration = this.duration
            val targetDuration = duration * (seekBar.progress * 1.0f / seekBar.max)
            seekTo(targetDuration.toInt())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.playerViewMp.release()
    }

}


private class FilterViewHolder(val binding: ItemFilterOptionBinding) :
    RecyclerView.ViewHolder(binding.root)

private class FilterListAdapter(
    val context: Context,
    val availableFilters: MutableList<GlFilter>,
    val onclick: ((filter: GlFilter) -> Unit)
) : RecyclerView.Adapter<FilterViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val binding =
            ItemFilterOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = FilterViewHolder(binding)
        binding.root.setOnClickListener {
            onclick(availableFilters[holder.adapterPosition])
        }
        return holder
    }

    override fun getItemCount() = availableFilters.size

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.binding.text.text = availableFilters[position].name
    }



}

private class CornerDecoration(val context: Context):RecyclerView.ItemDecoration(){
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.set(10, 20, 10, 10)
    }
}


