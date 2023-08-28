package com.me.harris.extractframe.finale.creator

import com.me.harris.extractframe.contract.ExtractConfiguration

internal data class ExtractConfig(val filepath: String, val storageDir: String, val statement: ExtractStatement) {
    val videoDurationMicroSecond by statement::videoDurationMicroSecond
    val gapInBetweenSeconds = ExtractConfiguration.EXTRACT_FRAME_GAP_IN_BETWEEN_SECONDS
    val useLibYuv = ExtractConfiguration.EXTRACT_FRAME_USE_LIBYUV
//  com.me.harris.droidmedia     W  Throwing OutOfMemoryError "Failed to allocate a 66846739 byte allocation with 13249245 free bytes and 243MB until OOM, target footprint 26498493, growth limit 268435456" (VmSize 5906056 kB)
}

internal data class ExtractStatement(val videoDurationMicroSecond: Long)
