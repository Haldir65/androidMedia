@file:JvmName("ImageLoader")
package com.me.harris.gpuv.compose

import android.content.Context
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.provider.MediaStore.Video.Thumbnails
import android.widget.ImageView
import androidx.activity.findViewTreeFullyDrawnReporterOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import coil.decode.VideoFrameDecoder
import coil.load
import coil.request.videoFrameMillis
import coil.request.videoFramePercent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


fun loadImage(view:ImageView,data:String,context:Context){
    view.load(File(data))
}


 fun loadVideoThumbnail(imageView:ImageView,videoPath:String,context: Context){
     imageView.load(videoPath){
         decoderFactory { result, options, _ -> VideoFrameDecoder(result.source, options) }
//         videoFramePercent(0.0)
     }
//   imageView.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
//       val bitmap = withContext(Dispatchers.IO){
//           ThumbnailUtils.createVideoThumbnail(videoPath,MediaStore.Video.Thumbnails.MINI_KIND)
//       }
//       imageView.setImageBitmap(bitmap)
//   }
}
