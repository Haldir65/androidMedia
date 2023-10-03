package com.me.harris.audiolib.audioPlayer.utils

import java.nio.ByteBuffer
import java.nio.ByteOrder


object ByteUtil {
    /** 该类提供字节转换功能
     * java 默认只有小端字节序
     */
    // 将byte[] 数组转换成short[]数组
    fun bytesToShorts(bytes: ByteArray?, len: Int, isBe: Boolean): ShortArray? {
        if (bytes == null) {
            return null
        }
        val shorts = ShortArray(len / 2)
        // 大端序
        if (isBe) {
            ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).asShortBuffer()[shorts]
        } else {
            ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()[shorts]
        }
        return shorts
    }

    // 将byte[] 数组转换成float[]数组
    fun bytesToFloats(bytes: ByteArray?, len: Int, isBe: Boolean): FloatArray? {
        if (bytes == null) {
            return null
        }
        val floats = FloatArray(len / 4)
        // 大端序
        if (isBe) {
            ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).asFloatBuffer()[floats]
        } else {
            ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer()[floats]
        }
        return floats
    }

    // 将short[] 转换为bytes[]
    fun shortsToBytes(shorts: ShortArray?): ByteArray? {
        if (shorts == null) {
            return null
        }
        val bytes = ByteArray(shorts.size * 2)
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(shorts)
        return bytes
    }

    // 将float[] 转换为bytes[]
    fun floatsToBytes(floats: FloatArray?): ByteArray? {
        if (floats == null) {
            return null
        }
        val bytes = ByteArray(floats.size * 4)
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().put(floats)
        return bytes
    }

    fun byte2hex(buffer: ByteArray): String? {
        var h = ""
        for (i in buffer.indices) {
            var temp = Integer.toHexString(buffer[i].toInt() and 0xFF)
            if (temp.length == 1) {
                temp = "0$temp"
            }
            h = "$h $temp"
        }
        return h
    }
}