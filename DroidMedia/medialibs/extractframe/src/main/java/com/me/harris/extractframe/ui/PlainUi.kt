package com.me.harris.extractframe.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.me.harris.extractframe.databinding.ItemExtractingFrameNailBinding
import java.io.File


internal class VideoFrameDisplayViewHolder(val binding: ItemExtractingFrameNailBinding):
    RecyclerView.ViewHolder(binding.root) {
    fun bindData(imageUrl:String,position:Int){
        val ctx = binding.root.context.applicationContext
        binding.image.load(File(imageUrl)){
//            transformations(CircleCropTransformation())
            transformations(RoundedCornersTransformation(25f))
//            transformations(GrayscaleTransformation())
//            transformations(BlurTransformation(ctx))
//            transformations(BlurTransformation(ctx, 5f))
        }
        binding.desc.text = position.toString()
    }
}
internal class VideoFrameDisplayAdapter(): RecyclerView.Adapter<VideoFrameDisplayViewHolder>() {

    val list = mutableListOf<String>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoFrameDisplayViewHolder {
        val binding = ItemExtractingFrameNailBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return VideoFrameDisplayViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: VideoFrameDisplayViewHolder, position: Int) {
        holder.bindData(list[position],position)
    }
}