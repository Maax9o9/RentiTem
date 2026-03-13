package com.rentitem.core.di

import android.content.Context
import com.rentitem.core.hardware.data.AndroidCameraManager
import com.rentitem.core.hardware.data.AndroidGpsManager
import com.rentitem.core.hardware.domain.CameraManager
import com.rentitem.core.hardware.domain.GpsManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HardwareModule {

    @Binds
    @Singleton
    abstract fun bindGpsManager(impl: AndroidGpsManager): GpsManager

    @Binds
    @Singleton
    abstract fun bindCameraManager(impl: AndroidCameraManager): CameraManager
}
