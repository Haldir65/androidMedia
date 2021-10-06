package com.me.harris.droidmedia.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.FutureTask

fun blockOnMainThread(action: Runnable) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        action.run()
    } else {
        FutureTask<Unit>(action, Unit)
            .also {
                Handler(Looper.getMainLooper()).post(it)
            }.getOrThrow()
    }
}

fun <T> blockOnMainThread(block:() -> T): T {
    return if (Looper.myLooper() == Looper.getMainLooper()){
        block()
    }else {
        FutureTask<T>(Callable { block() }).also {
            Handler(Looper.getMainLooper()).post(it)
        }?.getOrThrow()
    }
}


@Suppress("NOTHING_TO_INLINE")
private inline fun <T> FutureTask<T>.getOrThrow(): T {
    try {
        return this.get()
    } catch (e: ExecutionException) {
        throw e.cause ?: e
    } catch (e: InterruptedException) {
        throw IllegalStateException(e)
    }
}