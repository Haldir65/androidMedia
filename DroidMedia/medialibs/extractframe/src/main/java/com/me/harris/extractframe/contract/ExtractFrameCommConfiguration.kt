@file:JvmName("ExtractFramer")
package com.me.harris.extractframe.contract


object ExtractConfiguration {
    const val EXTRACT_FRAME_GAP_IN_BETWEEN_SECONDS = 5
    const val EXTRACT_FRAME_PARALLEISM = 4 // 并发度，可以是mediaMetaDataRetriever的实例个数 或者mediaCodec的个数
}
