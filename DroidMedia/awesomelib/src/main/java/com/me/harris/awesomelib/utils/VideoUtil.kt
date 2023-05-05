package com.me.harris.awesomelib.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.me.harris.awesomelib.videoutil.VideoInfoHelper
import java.io.File
import java.util.*

object VideoUtil {

    @JvmField
    var strVideo: String = ""
//	static {
//		if (strVideo==null){
//			File dir = new File(Environment.getExternalStorageDirectory().getPath()+
//					File.separator+Environment.DIRECTORY_MOVIES);
//			strVideo = dir.listFiles()[1].getAbsolutePath();
//		}
////		private static final String strVideo = Environment.getExternalStorageDirectory().getPath()+
////				File.separator+Environment.DIRECTORY_MOVIES+File.separator + "/h265.mp4";
//	}


    //	static {
    //		if (strVideo==null){
    //			File dir = new File(Environment.getExternalStorageDirectory().getPath()+
    //					File.separator+Environment.DIRECTORY_MOVIES);
    //			strVideo = dir.listFiles()[1].getAbsolutePath();
    //		}
    ////		private static final String strVideo = Environment.getExternalStorageDirectory().getPath()+
    ////				File.separator+Environment.DIRECTORY_MOVIES+File.separator + "/h265.mp4";
    //	}
    @JvmStatic
    fun setUrl() {
        val dir :File = if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1)
            File(Environment.getExternalStorageDirectory().absolutePath).parentFile.parentFile.listFiles()[0] else  File(
            Environment.getExternalStorageDirectory().path +
                    File.separator + Environment.DIRECTORY_MOVIES
        )
//        val fs = dir.listFiles { f -> f.name.endsWith(".mp4") || f.name.endsWith(".mkv") || f.name.endsWith(".webm")  }
        val fs = dir.listFiles { f -> f.name.endsWith(".webm")  }
//        strVideo = fs[Random().nextInt(fs.size)].absolutePath
//        strVideo = fs[4].absolutePath
        strVideo = fs[0].absolutePath
//        strVideo = "/storage/emulated/0/Movies/video_001.mp4"
    }


    @JvmStatic
    fun View.adjustPlayerViewPerVideoAspectRation(url:String){
        val activity = context as Activity

        val arr = VideoInfoHelper.queryVideoInfo(url)
        val params = layoutParams

        val displayMetrics = DisplayMetrics()
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics)
//        val height = displayMetrics.heightPixels.toFloat()
        val width = displayMetrics.widthPixels.toFloat()

        updateLayoutParams<ViewGroup.MarginLayoutParams> {
            this.width = width.toInt()
            this.height = (width * (arr[1].toFloat() / arr[0].toFloat())).toInt()
        }
    }
}