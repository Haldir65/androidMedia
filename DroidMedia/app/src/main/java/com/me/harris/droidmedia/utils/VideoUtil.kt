package com.me.harris.droidmedia.utils

import android.os.Build
import android.os.Environment
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
        val fs = dir.listFiles { f -> f.name.endsWith(".mp4") || f.name.endsWith(".mkv") || f.name.endsWith(".webm")  }
        strVideo = fs[Random().nextInt(fs.size)].absolutePath
//        strVideo = fs[4].absolutePath
        strVideo = fs[1].absolutePath
//        strVideo = "/storage/emulated/0/Movies/video_001.mp4"
    }
}