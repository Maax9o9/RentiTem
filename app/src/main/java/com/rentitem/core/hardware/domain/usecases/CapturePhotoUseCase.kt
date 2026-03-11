package com.rentitem.core.hardware.domain.usecases

import com.rentitem.core.hardware.domain.CameraManager
import com.rentitem.core.hardware.domain.model.CapturedPhoto
import javax.inject.Inject

/**
 * Caso de uso reutilizable — usado en itempublications Y profileInfo.
 * SRP: su única responsabilidad es orquestar la captura.
 * No sabe nada de UI, no sabe nada de publicaciones, no sabe nada de perfiles.
 */

class CapturePhotoUseCase @Inject constructor(
    private val cameraManager: CameraManager
) {
    suspend operator fun invoke(): Result<CapturedPhoto> =
        cameraManager.capturePhoto()
}
