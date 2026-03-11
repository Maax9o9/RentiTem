package com.rentitem.core.di

import android.content.Context
import com.rentitem.core.hardware.data.AndroidCameraManager
import com.rentitem.core.hardware.data.AndroidGpsManager
import com.rentitem.core.hardware.domain.CameraManager
import com.rentitem.core.hardware.domain.GpsManager
import dagger.Binds
import javax.inject.Singleton

abstract class HardwareModule(private val context: Context) {

    val gpsManager: GpsManager by lazy {
        AndroidGpsManager(context)
    }

    @Binds
    @Singleton
    abstract fun bindCameraManager(
        impl: AndroidCameraManager
    ): CameraManager
}