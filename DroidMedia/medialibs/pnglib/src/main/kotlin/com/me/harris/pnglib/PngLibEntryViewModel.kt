package com.me.harris.pnglib

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import java.nio.ByteBuffer

class PngLibEntryViewModel(app:Application):AndroidViewModel(app) {

     val spoon = PngSpoon

    fun checkPngFileIsPngFile(filepath:String){
        viewModelScope.launch(Dispatchers.IO) {
            if (spoon.probeFileInfo(filepath)==0){
                Log.w("=A=","file ${filepath} is png file")
            }else {
                Log.w("=A=","file ${filepath} is not png file")
            }
        }
    }


    fun getPngWidth(filepath:String):Int{
        val width = spoon.getPngWidth(filepath)
        Log.w("=A=","png image width for file ${filepath} is ${width}")
        return width
    }

    fun getPngHeight(filepath:String):Int{
        val height = spoon.getPngHeight(filepath)
        Log.w("=A=","png image height for file ${filepath} is ${height}")
        return height
    }



    fun decodePngToByteBuffer(filepath:String,buffer:ByteBuffer){
        spoon.decodePngToDirectBuffer(filepath,buffer)
    }

    fun saveBitmapToPngFile(destfile:String,buffer:ByteBuffer){
        spoon.compressBitmapToPngFile(destfile =destfile ,buffer = buffer)
    }

}
