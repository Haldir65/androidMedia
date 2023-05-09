package com.me.harris.awesomelib

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

// The only drawback is that you would have to inject a Manager instance (created by Dagger)
// into every class that needs to use it instead of calling its methods statically.
// https://stackoverflow.com/a/59516127

// Dagger 2 simply does not support injection into static fields
// kotlin object 不支持注入

/**
 * Dagger 2 simply does not support injection into static fields. However,
 * even if Dagger let you do it, the dependency wouldn't be satisfied.
 * That's because Dagger won't be able to inject into an object unless is
 * explicitly told to do so (like you do when injecting for example into activities)
 * or creates the object by itself.
 * Obviously the latter doesn't apply when it comes to Kotlin's objects.
 */
@Singleton
class ApplicationContextProvider @Inject constructor(@ApplicationContext val context: Context) {

    fun doStuff() {

    }
}

