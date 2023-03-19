package com.me.harris.awesomelib


import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment


fun <T : Fragment> T.withArguments(vararg pairs: Pair<String, *>) = apply {
    arguments = bundleOf(*pairs)
}

inline fun <reified T> Fragment.arguments(key: String) = lazy<T?> {
    arguments[key]
}

inline fun <reified T> Fragment.arguments(key: String, default: T) = lazy {
    arguments[key] ?: default
}

inline fun <reified T> Fragment.safeArguments(name: String) = lazy<T> {
    checkNotNull(arguments[name]) { "No intent value for key \"$name\"" }
}


