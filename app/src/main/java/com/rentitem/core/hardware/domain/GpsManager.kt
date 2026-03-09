package com.rentitem.core.hardware.domain

import android.location.Location

interface GpsManager {

    suspend fun getCurrentLocation(): Location?

    fun isGpsEnabled(): Boolean
}