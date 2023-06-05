package com.me.harris.playerLibrary.externalservice

import android.util.Log
import com.me.harris.serviceapi.DisplayB

class BImpl:DisplayB {
    override val name: String?
        get() = "this is BImpl"

    init {
        Log.e("ServiceHelper", "BImpl()")
    }

}