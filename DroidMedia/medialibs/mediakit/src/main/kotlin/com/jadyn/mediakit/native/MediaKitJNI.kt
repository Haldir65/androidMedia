package com.jadyn.mediakit.native

import androidx.annotation.Keep

@Keep
object MediaKitJNI {


    init {
        System.loadLibrary("gljni")
    }

    external fun mediakitProbeInfo(filepath: String);

    external fun mediakitExtractFrame(filepath:String,storageDir:String,gapInBetweenSeconds:Long)

    external fun readFileContentUsingMMap(filepath: String):String

}
