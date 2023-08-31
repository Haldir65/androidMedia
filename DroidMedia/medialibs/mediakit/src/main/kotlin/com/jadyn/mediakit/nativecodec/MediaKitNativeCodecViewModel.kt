package com.jadyn.mediakit.nativecodec

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jadyn.mediakit.native.MediaKitJNI
import kotlinx.coroutines.*

class MediaKitNativeCodecViewModel(val app: Application,val savedStateHandle: SavedStateHandle):AndroidViewModel(app) {


    fun mediaCodecProbeInfo(filepath:String){
        viewModelScope.launch {
            MediaKitJNI.mediakitProbeInfo(filepath = filepath)
        }
    }
}
