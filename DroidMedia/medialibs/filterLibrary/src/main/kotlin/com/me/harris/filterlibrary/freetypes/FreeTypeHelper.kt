package com.me.harris.filterlibrary.freetypes

import android.graphics.Bitmap

class FreeTypeHelper {

    companion object {
        init {
            System.loadLibrary("nativefilter")
        }
    }

    external fun loadFreeTypeIntoBitMap(text:String,fontAssetName:String,bitmap: Bitmap)


}
