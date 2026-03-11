package com.rentitem.core.di

import android.content.Context
import com.rentitem.core.hardware.data.AndroidCameraManager
import com.rentitem.core.hardware.data.AndroidGpsManager
import com.rentitem.core.hardware.domain.CameraManager
import com.rentitem.core.hardware.domain.GpsManager

class HardwareModule(private val context: Context) {

    val gpsManager: GpsManager by lazy {
        AndroidGpsManager(context)
    }

    val cameraManager: CameraManager by lazy {
        AndroidCameraManager(context)
    }
}