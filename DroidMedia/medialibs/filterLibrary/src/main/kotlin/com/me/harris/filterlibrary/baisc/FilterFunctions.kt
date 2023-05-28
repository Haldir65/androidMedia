package com.me.harris.filterlibrary.baisc

import android.app.Application
import android.content.Context
import com.daasuu.epf.filter.FilterType
import com.daasuu.epf.filter.GlFilter

fun setupAllFilters(context:Context):List<GlFilter> {
    val types = FilterType.values()
    return types.map { FilterType.createGlFilter(it,if (it == FilterType.PNG_CUS)"filter_white" else "",context) }
}