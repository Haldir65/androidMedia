package com.me.harris.awesomelib

import android.os.Bundle

inline operator fun <reified T> Bundle?.get(key: String): T? = this?.get(key) as? T