package com.me.harris.nativecrash

import android.content.Context
import android.util.Log
import xcrash.TombstoneManager

fun initXcrash(context:Context){
    val params = xcrash.XCrash.InitParameters()
        .setNativeCallback { logPath, emergency ->
            Log.e("xrash","${logPath} $emergency")
        }.setNativeDumpFds(true).setAnrDumpFds(true)
        .setNativeDumpAllThreads(true)
        .setNativeDumpElfHash(true)
        .setNativeRethrow(true)
        .setNativeDumpMap(true)
        .setNativeDumpNetwork(true)
    xcrash.XCrash.init(context,params);
    TombstoneManager.getAllTombstones().forEach { f ->
        Log.w("xcrash","${f.absolutePath}")
    }
}