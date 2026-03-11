package com.rentitem.core.hardware.presentation

import androidx.camera.core.ImageCapture
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rentitem.core.hardware.data.AndroidCameraManager
import com.rentitem.core.hardware.domain.CameraManager
import com.rentitem.core.hardware.domain.usecases.CapturePhotoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CameraViewModel(
    private val capturePhotoUseCase: CapturePhotoUseCase,
    private val cameraManager: CameraManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<CameraUiState>(CameraUiState.Idle)
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    private val _flashEnabled = MutableStateFlow(false)
    val flashEnabled: StateFlow<Boolean> = _flashEnabled.asStateFlow()

    fun toggleFlash() {
        _flashEnabled.value = !_flashEnabled.value
        (cameraManager as? AndroidCameraManager)?.imageCapture?.flashMode =
            if (_flashEnabled.value) ImageCapture.FLASH_MODE_ON
            else ImageCapture.FLASH_MODE_OFF
    }

    fun registerImageCapture(imageCapture: ImageCapture) {
        (cameraManager as? AndroidCameraManager)?.imageCapture = imageCapture
    }

    fun capturePhoto() {
        if (_uiState.value is CameraUiState.Capturing) return

        viewModelScope.launch {
            _uiState.value = CameraUiState.Capturing

            capturePhotoUseCase().fold(
                onSuccess = { photo ->
                    _uiState.value = CameraUiState.Success(photo)
                },
                onFailure = { error ->
                    val previousPhoto = (_uiState.value as? CameraUiState.Success)?.photo
                    _uiState.value = CameraUiState.Error(
                        message = error.message ?: "Error desconocido al capturar",
                        previousPhoto = previousPhoto
                    )
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = CameraUiState.Idle
    }

    fun clearError() {
        val previousPhoto = (_uiState.value as? CameraUiState.Error)?.previousPhoto
        _uiState.value = if (previousPhoto != null) {
            CameraUiState.Success(previousPhoto)
        } else {
            CameraUiState.Idle
        }
    }

    companion object {
        fun provideFactory(cameraManager: CameraManager): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CameraViewModel(
                        capturePhotoUseCase = CapturePhotoUseCase(cameraManager),
                        cameraManager = cameraManager
                    ) as T
                }
            }
    }
}

