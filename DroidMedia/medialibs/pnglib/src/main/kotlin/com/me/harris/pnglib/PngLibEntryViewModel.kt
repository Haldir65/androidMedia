package com.me.harris.pnglib

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

class PngLibEntryViewModel(app:Application):AndroidViewModel(app) {

    private val spoon = PngSpoon

    fun checkPngFileIsPngFile(filepath:String){
        viewModelScope.launch(Dispatchers.IO) {
            if (spoon.probeFileInfo(filepath)==0){
                Log.w("=A=","file ${filepath} is png file")
            }else {
                Log.w("=A=","file ${filepath} is not png file")
            }
        }
    }

}
