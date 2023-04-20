package com.me.harris.droidmedia

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication:Application() {

    companion object {
        lateinit var instance: Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
//        com.jadyn.ai.kotlind.base.BaseApplication.instance = this
    }
}