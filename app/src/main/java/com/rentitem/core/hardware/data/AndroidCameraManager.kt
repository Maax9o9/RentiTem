package com.rentitem.core.hardware.data

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import com.rentitem.core.hardware.domain.CameraManager
import com.rentitem.core.hardware.domain.model.CapturedPhoto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class AndroidCameraManager @Inject constructor(
    @ApplicationContext private val context: Context
) : CameraManager {

    @Volatile
    var imageCapture: ImageCapture? = null

    override suspend fun capturePhoto(): Result<CapturedPhoto> {
        val capture = imageCapture
            ?: return Result.failure(
                IllegalStateException("La cámara no está inicializada. ¿Se llamó registerImageCapture()?")
            )

        val contentValues = ContentValues().apply {
            put(
                MediaStore.Images.Media.DISPLAY_NAME,
                "RENTITEM_${System.currentTimeMillis()}.jpg"
            )
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        /**
         * suspendCancellableCoroutine: convierte el callback de CameraX en una
         * suspend function limpia. Si la coroutine es cancelada (ej: usuario
         * sale de la pantalla), el bloque onCancellation evita memory leaks
         * limpiando el callback pendiente.
         */
        return suspendCancellableCoroutine { continuation ->
            capture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {


                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        val uri = output.savedUri?.toString()

                        if (uri != null) {
                            continuation.resume(
                                Result.success(
                                    CapturedPhoto(
                                        uri = uri,
                                        timestamp = System.currentTimeMillis(),
                                        isTemporary = true
                                    )
                                )
                            )
                        } else {
                            continuation.resume(
                                Result.failure(
                                    Exception("La foto se guardó pero la URI retornada fue nula")
                                )
                            )
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        continuation.resume(
                            Result.failure(exception)
                        )
                    }
                }
            )

            // Si la coroutine es cancelada externamente, no hay nada que liberar
            continuation.invokeOnCancellation {
            }
        }
    }
}
