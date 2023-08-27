package com.me.harris.extractframe.finale.creator

import android.util.Log
import com.me.harris.extractframe.contract.ExtractConfiguration
import com.me.harris.extractframe.parallel.ExtractMouse
import com.me.harris.extractframe.parallel.PARALLISM
import com.me.harris.extractframe.parallel.Range
import com.me.harris.extractframe.parallel.getVideoDurationInMicroSeconds

const val PARALISIM = ExtractConfiguration.EXTRACT_FRAME_PARALLEISM
internal class ExtractLegion(val config: ExtractConfig) {

    suspend fun distributeIntoMultipleUnits():List<ExtractMouse> {
        val durationMicroSeconds = config.videoDurationMicroSecond
        require(durationMicroSeconds > 0)
        val gap = config.gapInBetweenSeconds*1000_000
        val saveDir = config.storageDir
        val filepath = config.filepath
        val durationUs = durationMicroSeconds
        val allPoints = List((durationUs/gap).toInt()){ a ->
            a * gap.toLong()
        }
        val chunks = allPoints.chunked((allPoints.size/ PARALLISM).coerceAtLeast(1))

        val result = chunks.mapIndexed { index, c  -> ExtractMouse(id = index+1, filepath = filepath, saveDirPath = saveDir, range = Range(c)) }
        val str = """
        【Thread】 ${Thread.currentThread().id} ${Thread.currentThread().name}
        distributing result is

        一共${PARALLISM}个分片

        ${result.joinToString(separator = System.lineSeparator()) { a -> a.range.points.joinToString(separator = ",") { c -> "${(c/1000_000).toString()}秒" } }}
    """.lineSequence().joinToString(transform = String::trimStart, separator = System.lineSeparator())
        Log.w("=A=",str)
        require(result.isNotEmpty())
        return result
    }
}
