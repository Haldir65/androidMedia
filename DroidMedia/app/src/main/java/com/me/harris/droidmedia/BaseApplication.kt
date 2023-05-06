package com.me.harris.droidmedia

import android.app.Application
import com.jadyn.mediakit.MediaKitApplication
import com.jadyn.mediakit.gl.GLJni
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication:Application() {

    companion object {
        lateinit var instance: Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        MediaKitApplication.instance = this // TDDO : USE DI TO Supply application context in another library module?
       val a =  GLJni // eagerly laad jni
//        com.jadyn.ai.kotlind.base.BaseApplication.instance = this
    }
}