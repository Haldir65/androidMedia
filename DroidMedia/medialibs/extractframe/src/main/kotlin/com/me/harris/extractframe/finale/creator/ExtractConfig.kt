package com.me.harris.extractframe.finale.creator

import com.me.harris.extractframe.contract.ExtractConfiguration

internal data class ExtractConfig(val filepath: String, val storageDir: String, val statement: ExtractStatement) {
    val videoDurationMicroSecond by statement::videoDurationMicroSecond
    val gapInBetweenSeconds = ExtractConfiguration.EXTRACT_FRAME_GAP_IN_BETWEEN_SECONDS
}

internal data class ExtractStatement(val videoDurationMicroSecond: Int)
