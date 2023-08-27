package com.me.harris.extractframe.finale.creator

import com.me.harris.extractframe.contract.ExtractConfiguration
import com.me.harris.extractframe.parallel.getVideoDurationInMicroSeconds

const val PARALISIM = ExtractConfiguration.EXTRACT_FRAME_PARALLEISM
class ExtractLegion {

    suspend fun distributeIntoMultipleUnits(filepath:String) {
        val durationMicroSeconds = getVideoDurationInMicroSeconds(filepath)
        require(durationMicroSeconds > 0)

    }
}
