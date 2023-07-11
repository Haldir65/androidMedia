@file:JvmName("assetsHelp")
package com.me.harris.awesomelib


import android.content.Context

fun readAssetFileContentAsString(context:Context,assetFileName:String):String{
   return context.assets.open(assetFileName).bufferedReader().use { it.readText() }
}