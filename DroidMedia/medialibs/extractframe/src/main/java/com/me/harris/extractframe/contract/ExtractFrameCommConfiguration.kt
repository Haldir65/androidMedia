@file:JvmName("ExtractFramer")
package com.me.harris.extractframe.contract


object ExtractConfiguration {
    const val EXTRACT_FRAME_GAP_IN_BETWEEN_SECONDS = 5 // 越小，耗时越长，抽出的图片数量越多
    // 对于一些时长较长的视频，例如，1h以上，gap设置成5s要等很久，甚至失败
    const val EXTRACT_FRAME_PARALLEISM = 3 // 并发度，可以是mediaMetaDataRetriever的实例个数 或者mediaCodec的个数
    // 经验值，不是越大越好，测下来3是一个比较好的值， 3 -> 4之后会劣化, 异步场景下，处理4K视频甚至出现了 AMEDIACODEC_ERROR_INSUFFICIENT_RESOURCE
//  OMX.qcom.video.decoder.vp9
//codec android.media.MediaCodec@aa6635b
//diagnosticInfo =  android.media.MediaCodec.error_1100
//1100
//false
//false
//android.media.MediaCodec$CodecException: Error 0xfffffff4

///    codec android.media.MediaCodec@ad6239b
//                         diagnosticInfo =  android.media.MediaCodec.error_1100
//                         1100   AMEDIACODEC_ERROR_INSUFFICIENT_RESOURCE = 1100,
//                         false
//                         false
//                         android.media.MediaCodec CodecException: Error 0xfffffff4

    const val EXTRACT_FRAME_USE_LIBYUV = false // 极容易抛出oom

    const val BITMAP_RESIZE_FACTOR = 4 // Save bitmap之前先宽高缩小对应比例
    const val SAVE_BIT_MAP_USE_TURBO_JPEG = false

    const val LOG_TAG = "=A="

}
