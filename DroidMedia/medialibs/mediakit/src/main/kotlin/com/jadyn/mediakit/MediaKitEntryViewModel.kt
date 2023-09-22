package com.jadyn.mediakit

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.jadyn.mediakit.native.MediaKitJNI
import java.io.File

class MediaKitEntryViewModel(app:Application):AndroidViewModel(app) {


    fun readTextWithMMAP(){
        val filepath = "${getApplication<Application>().filesDir}${File.separator}${System.currentTimeMillis()}.text"
        val contentWritten = "today is the day , another beautiful day 今？天呢"
        File(filepath).writeText(contentWritten, Charsets.UTF_8)
        val readContent = MediaKitJNI.readFileContentUsingMMap(filepath)
        Log.w("=A=",readContent)
    }
}