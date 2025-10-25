package com.me.harris.awesomelib.utils

import android.app.Activity
import android.graphics.Matrix
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.me.harris.awesomelib.videoutil.VideoInfoHelper
import java.io.File

object VideoUtil {

    @JvmField
    var strVideo: String = ""



    @JvmField
    var remoteUrl: String = ""
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

    private var choosenFile = ""

    fun setUrl(path:String){
        choosenFile = path
    }

    @JvmStatic
    fun setUrl() {
        val selfHosted = Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM
        val glassy = Build.VERSION.SECURITY_PATCH == "2020-12-01"
        val dir :File = File(
            Environment.getExternalStorageDirectory().path +
                    File.separator + Environment.DIRECTORY_MOVIES
        )
        val fs = dir.listFiles { f -> f.name.endsWith(".mp4") || f.name.endsWith(".mkv") || f.name.endsWith(".webm")  }
//        val fs = dir.listFiles { f -> f.name.endsWith(".mp4")  }.orEmpty()
//        val fs = dir.listFiles { f -> f.name.endsWith(".webm")  }.orEmpty()
//        strVideo = fs[Random().nextInt(fs.size)].absolutePath
//        strVideo = fs[1].absolutePath
//        strVideo = fs[0].absolutePath
        if (fs.isNotEmpty()){
            strVideo = if (selfHosted) {
                dir.listFiles { f -> f.name.endsWith(".webm")  }.orEmpty()[0].absolutePath
            } else if (glassy){
                dir.listFiles { f -> f.name.endsWith(".mkv")  }.orEmpty()[1]?.absolutePath.orEmpty()
            }
            else {
                fs.firstOrNull { it.name.contains("365") }?.absolutePath.orEmpty()
            }
        }
        if (!TextUtils.isEmpty(choosenFile)){
            strVideo = choosenFile
        }
//        strVideo = remoteUrl
        Log.w("=A=","strVideo = $strVideo")

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

    @JvmStatic
    fun TextureView.adjustTextureViewPerVideoAspectRation(url:String){
        val activity = context as Activity

        val arr = VideoInfoHelper.queryVideoInfo(url)
        val params = layoutParams

        val displayMetrics = DisplayMetrics()
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics)
//        val height = displayMetrics.heightPixels.toFloat()
        val width = displayMetrics.widthPixels.toFloat()
        adjustAspectRatio(this,arr[0],arr[1])
    }

    @JvmStatic

    fun adjustViewRatio(maxWidth:Int,videoWitdh:Int, videoHeight:Int, view: View){
        view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            this.width = maxWidth
            this.height = (maxWidth * (videoHeight.toFloat() / videoWitdh.toFloat())).toInt()
        }
    }


    @JvmStatic

    // https://github.com/google/grafika/blob/b1df331e89cffeab621f02b102d4c2c25eb6088a/app/src/main/java/com/android/grafika/PlayMovieActivity.java#L21
     fun adjustAspectRatio(mTextureView:TextureView,videoWidth: Int, videoHeight: Int) {
        val viewWidth: Int = mTextureView.getWidth()
        val viewHeight: Int = mTextureView.getHeight()
        val aspectRatio = videoHeight.toDouble() / videoWidth
        val newWidth: Int
        val newHeight: Int
        if (viewHeight > (viewWidth * aspectRatio).toInt()) {
            // limited by narrow width; restrict height
            newWidth = viewWidth
            newHeight = (viewWidth * aspectRatio).toInt()
        } else {
            // limited by short height; restrict width
            newWidth = (viewHeight / aspectRatio).toInt()
            newHeight = viewHeight
        }
        val xoff = (viewWidth - newWidth) / 2
        val yoff = (viewHeight - newHeight) / 2
//        Log.v(
//            TAG, "video=" + videoWidth + "x" + videoHeight +
//                " view=" + viewWidth + "x" + viewHeight +
//                " newView=" + newWidth + "x" + newHeight +
//                " off=" + xoff + "," + yoff
//        )
        val txform = Matrix()
        mTextureView.getTransform(txform)
        txform.setScale(newWidth.toFloat() / viewWidth, newHeight.toFloat() / viewHeight)
        //txform.postRotate(10);          // just for fun
        txform.postTranslate(xoff.toFloat(), yoff.toFloat())
        mTextureView.setTransform(txform)
    }
}
