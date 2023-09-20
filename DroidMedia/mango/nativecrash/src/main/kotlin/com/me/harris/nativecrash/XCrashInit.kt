package com.me.harris.nativecrash

import android.content.Context
import android.util.Log
import xcrash.TombstoneManager
import java.io.File
import java.nio.charset.StandardCharsets

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
    val recent_crash =  TombstoneManager.getAllTombstones().orEmpty()
    recent_crash.filter(File::exists).take(5).joinToString(separator = System.lineSeparator()) { f ->
        runCatching { f.readText(charset = StandardCharsets.UTF_8) }.getOrNull().orEmpty()
    }.let {
        Log.e("=A=",it)
    }

}

