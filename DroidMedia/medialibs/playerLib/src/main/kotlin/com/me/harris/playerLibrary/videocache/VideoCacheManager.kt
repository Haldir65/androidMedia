package com.me.harris.playerLibrary.videocache

import android.app.Application
import android.content.Context
import com.danikula.videocache.HttpProxyCacheServer


object VideoCacheManager {


    private  var _proxy :HttpProxyCacheServer? = null

    fun getProxy(context: Context): HttpProxyCacheServer {
        val app: Context = context.applicationContext
        return if (_proxy == null) HttpProxyCacheServer.Builder(app).apply {
            maxCacheFilesCount(20)
        }.build().also { _proxy = it } else _proxy!!
    }



}