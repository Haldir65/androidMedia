package com.jadyn.mediakit.video.encode

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

internal fun createFloatBuffer(array: FloatArray): FloatBuffer {
    val buffer = ByteBuffer
        // 分配顶点坐标分量个数 * Float占的Byte位数
        .allocateDirect(array.size * 4)
        // 按照本地字节序排序
        .order(ByteOrder.nativeOrder())
        // Byte类型转Float类型
        .asFloatBuffer()

    // 将Dalvik的内存数据复制到Native内存中
    buffer.put(array).position(0)
    return buffer
}