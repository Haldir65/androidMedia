@file:JvmName("ExtractFramer")
package com.me.harris.extractframe.contract


object ExtractConfiguration {
    const val EXTRACT_FRAME_GAP_IN_BETWEEN_SECONDS = 5 // 越小，耗时越长，抽出的图片数量越多
    const val EXTRACT_FRAME_PARALLEISM = 3 // 并发度，可以是mediaMetaDataRetriever的实例个数 或者mediaCodec的个数
    // 经验值，不是越大越好，测下来3是一个比较好的值， 3 -> 4之后会劣化
    const val EXTRACT_FRAME_USE_LIBYUV = true // 极容易抛出oom


    const val LOG_TAG = "=A="

}
