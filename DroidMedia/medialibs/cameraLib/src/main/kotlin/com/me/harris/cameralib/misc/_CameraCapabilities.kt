package com.me.harris.cameralib.misc

import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Log

internal fun getCameraHardwareSupport(cameraManager:CameraManager) {
    // cameraCharacteristics: 相机设备的属性类,
    // 通过 CameraManager 的 getCameraCharacteristics(String cameraId) 方法获取指定相机设备的 CameraCharacteristics 对象
    val cameraManager = cameraManager ?: return
    val cameraIdList = cameraManager.cameraIdList
    // 这里直接拿第一个摄像头
    val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraIdList[0])
    val deviceLevel = cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL) ?: return
    when(deviceLevel) {
        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY -> {
            Log.w("=A=","CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY")
        }
        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED -> {
            Log.w("=A=","CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED")
        }
        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL -> {
            // CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL
            Log.w("=A=","CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL")
        }
        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3 -> {
            Log.w("=A=","CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3")
        }
        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL -> {
            Log.w("=A=","CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL")
        }
    }
}

