package com.me.harris.gpuvideo

import com.daasuu.gpuv.egl.filter.GlFilter

interface FilterAdjuster {
    fun adjust(filter:GlFilter,percentage:Int)
}