package com.rentitem.core.hardware.domain

import com.rentitem.core.hardware.domain.model.CapturedPhoto

interface CameraManager {
    suspend fun capturePhoto(): Result<CapturedPhoto>
}