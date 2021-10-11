package com.me.harris.droidmedia.utils

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
        val dir = File(
            Environment.getExternalStorageDirectory().path +
                    File.separator + Environment.DIRECTORY_MOVIES
        )
        val fs = dir.listFiles { dir1: File?, name: String ->
            name.endsWith(
                ".mp4"
            ) || name.endsWith(".mkv") || name.endsWith("webm")
        }!!
        strVideo = fs[Random().nextInt(fs.size)].absolutePath
        strVideo = fs[4].absolutePath
    }
}