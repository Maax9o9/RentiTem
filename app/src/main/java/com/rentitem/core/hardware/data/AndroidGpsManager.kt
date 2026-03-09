package com.rentitem.core.hardware.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.rentitem.core.hardware.domain.GpsManager
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AndroidGpsManager(
    private val context: Context
) : GpsManager {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location? = suspendCoroutine { continuation ->
        try {
            if (!hasPermission() || !isGpsEnabled()) {
                continuation.resume(null)
                return@suspendCoroutine
            }

            val providers = locationManager.getProviders(true)
            var bestLocation: Location? = null

            for (provider in providers) {
                val l = locationManager.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || l.accuracy < bestLocation!!.accuracy) {
                    bestLocation = l
                }
            }
            continuation.resume(bestLocation)
        } catch (e: Exception) {
            continuation.resume(null)
        }
    }

    override fun isGpsEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }
}