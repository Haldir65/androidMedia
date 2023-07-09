package com.me.harris.cameralib

import android.content.Context

interface FilterAttributer {
    fun onShaderLoaded(engine: FilterEngine)
    fun onDraw(engine: FilterEngine)
    fun fragmentShader():Int


    companion object {
        @JvmStatic
        fun attributer():FilterAttributer{
//            return FilterSoulOutAttributer()
//            return FilterScaleAttributer()
//            return FilterThreeScreenWhiteBlack()
//            return Filter4ScreenAttributer()
//            return FilterJIngShenAttributer()
            return FilterMosaicAttributer()
        }
    }
}


